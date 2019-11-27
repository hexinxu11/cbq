package org.cbq.common.http.jdk11;

import org.apache.http.concurrent.FutureCallback;


import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接池
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/25 11:18
 * @Version 1.0
 **/
public abstract class AbstractConnPool<T, C> {
    private final Lock lock;
    private final ConnFactory<T, C> connFactory;
    private final Condition condition;
    private final Map<T, RouteSpecificPool<T, C>> routeToPool;
    private final Set<PoolEntry<T,C>> leased;
    private final LinkedList<PoolEntry<T,C>> available;
    private final LinkedList<Future<PoolEntry<T,C>>> pending;
    private final Map<T, Integer> maxPerRoute;
    private volatile boolean isShutDown;
    private volatile int defaultMaxPerRoute;
    private volatile int maxTotal;
    private volatile int validateAfterInactivity;

    public AbstractConnPool(ConnFactory<T, C> connFactory,int defaultMaxPerRoute, int maxTotal) {

        this.connFactory =  Args.notNull(connFactory, "Connection factory");
        this.defaultMaxPerRoute = Args.positive(defaultMaxPerRoute, "Max per route value");
        this.maxTotal = Args.positive(maxTotal, "Max total value");
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.routeToPool = new HashMap();
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.pending = new LinkedList();
        this.maxPerRoute = new HashMap();
    }

    /**
     * 创建连接实体
     * @param var1
     * @param var2
     * @return
     */
    protected abstract PoolEntry<T,C> createEntry(T var1, C var2);

    protected void onLease (PoolEntry<T,C>entry) {
    }

    protected void onRelease (PoolEntry<T,C>entry) {
    }

    protected void onReuse (PoolEntry<T,C>entry) {
    }

    protected boolean validate (PoolEntry<T,C>entry) {
        return true;
    }

    public boolean isShutdown() {
        return this.isShutDown;
    }

    /**
     * 关闭连接池-清空所有连接信息
     * @throws IOException
     */
    public void shutdown() throws IOException {

        if (!this.isShutDown) {
            this.isShutDown = true;
            this.lock.lock();
            try {
                this.available.stream().map(m->((PoolEntry)m)).forEach(f->f.close());
                this.leased.stream().map(m->((PoolEntry)m)).forEach(f->f.close());
                this.routeToPool.values().stream().map(m->((RouteSpecificPool)m)).forEach(f->f.shutdown());

                this.routeToPool.clear();
                this.leased.clear();
                this.available.clear();
            } finally {
                this.lock.unlock();
            }
        }
    }

    private RouteSpecificPool<T, C> getPool(final T route) {
        RouteSpecificPool<T, C> pool =this.routeToPool.get(route);
        if (pool == null) {
            pool = new RouteSpecificPool<>(route) {
                @Override
                protected PoolEntry<T,C> createEntry(C conn) {
                    return AbstractConnPool.this.createEntry(route, conn);
                }
            };
            this.routeToPool.put(route, pool);
        }

        return pool;
    }

    public Future<PoolEntry<T,C>> lease(final T route, final Object state, final FutureCallback<PoolEntry<T,C>> callback) {
        Args.notNull(route, "Route");
        Asserts.check(!this.isShutDown, "Connection pool shut down");
        return new Future<>() {
            private final AtomicBoolean cancelled = new AtomicBoolean(false);
            private final AtomicBoolean done = new AtomicBoolean(false);
            private final AtomicReference<PoolEntry<T,C>> entryRef = new AtomicReference((Object)null);

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (this.cancelled.compareAndSet(false, true)) {
                    this.done.set(true);
                    AbstractConnPool.this.lock.lock();
                    try {
                        AbstractConnPool.this.condition.signalAll();
                    } finally {
                        AbstractConnPool.this.lock.unlock();
                    }
                    if (callback != null) {
                        callback.cancelled();
                    }
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean isCancelled() {
                return this.cancelled.get();
            }

            @Override
            public boolean isDone() {
                return this.done.get();
            }

            @Override
            public PoolEntry<T,C> get() throws InterruptedException, ExecutionException {
                try {
                    return this.get(0L, TimeUnit.MILLISECONDS);
                } catch (TimeoutException var2) {
                    throw new ExecutionException(var2);
                }
            }

            @Override
            public PoolEntry<T,C> get(long timeout, TimeUnit tunit) throws InterruptedException, ExecutionException, TimeoutException {
                PoolEntry<T,C> entry = this.entryRef.get();
                if (entry != null) {
                    return entry;
                } else {
                    synchronized(this) {
                        try {
                            while(true) {
                                PoolEntry<T,C> leasedEntry = AbstractConnPool.this.getPoolEntryBlocking(route, state, timeout, tunit, this);
                                if (AbstractConnPool.this.validateAfterInactivity <= 0 || leasedEntry.getUpdated() + (long) AbstractConnPool.this.validateAfterInactivity > System.currentTimeMillis() || AbstractConnPool.this.validate(leasedEntry)) {
                                    this.entryRef.set(leasedEntry);
                                    this.done.set(true);
                                    AbstractConnPool.this.onLease(leasedEntry);
                                    if (callback != null) {
                                        callback.completed(leasedEntry);
                                    }
                                    return leasedEntry;
                                }

                                leasedEntry.close();
                                AbstractConnPool.this.release(leasedEntry, false);
                            }
                        } catch (IOException var8) {
                            this.done.set(true);
                            if (callback != null) {
                                callback.failed(var8);
                            }

                            throw new ExecutionException(var8);
                        }
                    }
                }
            }
        };
    }

    public Future<PoolEntry<T,C>> lease(T route, Object state) {
        return this.lease(route, state, (FutureCallback)null);
    }

    private PoolEntry<T,C> getPoolEntryBlocking(T route, Object state, long timeout, TimeUnit tunit, Future<PoolEntry<T,C>> future) throws IOException, InterruptedException, TimeoutException {
        Date deadline = null;
        if (timeout > 0L) {
            deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
        }

        this.lock.lock();

        try {
            RouteSpecificPool pool = this.getPool(route);

            boolean success;
            do {
                Asserts.check(!this.isShutDown, "Connection pool shut down");

                PoolEntry entry;
                while(true) {
                    entry = pool.getFree(state);
                    if (entry == null) {
                        break;
                    }

                    if (entry.isExpired(System.currentTimeMillis())) {
                        entry.close();
                    }

                    if (!entry.isClosed()) {
                        break;
                    }

                    this.available.remove(entry);
                    pool.free(entry, false);
                }

                if (entry != null) {
                    this.available.remove(entry);
                    this.leased.add(entry);
                    this.onReuse(entry);
                    PoolEntry var25 = entry;
                    return var25;
                }

                int maxPerRoute = this.getMax(route);
                int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
                int totalUsed;
                if (excess > 0) {
                    for(totalUsed = 0; totalUsed < excess; ++totalUsed) {
                        PoolEntry<T,C> lastUsed = pool.getLastUsed();
                        if (lastUsed == null) {
                            break;
                        }

                        lastUsed.close();
                        this.available.remove(lastUsed);
                        pool.remove(lastUsed);
                    }
                }

                if (pool.getAllocatedCount() < maxPerRoute) {
                    totalUsed = this.leased.size();
                    int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
                    if (freeCapacity > 0) {
                        int totalAvailable = this.available.size();
                        if (totalAvailable > freeCapacity - 1 && !this.available.isEmpty()) {
                            PoolEntry<T,C> lastUsed = (PoolEntry)this.available.removeLast();
                            lastUsed.close();
                            RouteSpecificPool<T, C> otherpool = this.getPool(lastUsed.getRoute());
                            otherpool.remove(lastUsed);
                        }

                        C conn = this.connFactory.create(route);
                        entry = pool.add(conn);
                        this.leased.add(entry);
                        PoolEntry var29 = entry;
                        return var29;
                    }
                }

                success = false;

                try {
                    if (future.isCancelled()) {
                        throw new InterruptedException("Operation interrupted");
                    }

                    pool.queue(future);
                    this.pending.add(future);
                    if (deadline != null) {
                        success = this.condition.awaitUntil(deadline);
                    } else {
                        this.condition.await();
                        success = true;
                    }

                    if (future.isCancelled()) {
                        throw new InterruptedException("Operation interrupted");
                    }
                } finally {
                    pool.unqueue(future);
                    this.pending.remove(future);
                }
            } while(success || deadline == null || deadline.getTime() > System.currentTimeMillis());

            throw new TimeoutException("Timeout waiting for connection");
        } finally {
            this.lock.unlock();
        }
    }

    public void release (PoolEntry<T,C>entry, boolean reusable) {
        this.lock.lock();

        try {
            if (this.leased.remove(entry)) {
                RouteSpecificPool<T, C> pool = this.getPool(entry.getRoute());
                pool.free(entry, reusable);
                if (reusable && !this.isShutDown) {
                    this.available.addFirst(entry);
                } else {
                    entry.close();
                }

                this.onRelease(entry);
                Future<PoolEntry<T,C>> future = pool.nextPending();
                if (future != null) {
                    this.pending.remove(future);
                } else {
                    future = (Future)this.pending.poll();
                }

                if (future != null) {
                    this.condition.signalAll();
                }
            }
        } finally {
            this.lock.unlock();
        }

    }

    private int getMax(T route) {
        Integer v = this.maxPerRoute.get(route);
        return v != null ? v : this.defaultMaxPerRoute;
    }

    public void setMaxTotal(int max) {
        Args.positive(max, "Max value");
        this.lock.lock();

        try {
            this.maxTotal = max;
        } finally {
            this.lock.unlock();
        }

    }

    public int getMaxTotal() {
        this.lock.lock();

        int var1;
        try {
            var1 = this.maxTotal;
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public void setDefaultMaxPerRoute(int max) {
        Args.positive(max, "Max per route value");
        this.lock.lock();

        try {
            this.defaultMaxPerRoute = max;
        } finally {
            this.lock.unlock();
        }

    }

    public int getDefaultMaxPerRoute() {
        this.lock.lock();

        int var1;
        try {
            var1 = this.defaultMaxPerRoute;
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public void setMaxPerRoute(T route, int max) {
        Args.notNull(route, "Route");
        Args.positive(max, "Max per route value");
        this.lock.lock();

        try {
            this.maxPerRoute.put(route, max);
        } finally {
            this.lock.unlock();
        }

    }

    public int getMaxPerRoute(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();

        int var2;
        try {
            var2 = this.getMax(route);
        } finally {
            this.lock.unlock();
        }

        return var2;
    }

    public PoolStats getTotalStats() {
        this.lock.lock();

        PoolStats var1;
        try {
            var1 = new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public PoolStats getStats(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();

        PoolStats var3;
        try {
            RouteSpecificPool<T, C> pool = this.getPool(route);
            var3 = new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), this.getMax(route));
        } finally {
            this.lock.unlock();
        }

        return var3;
    }

    public Set<T> getRoutes() {
        this.lock.lock();

        HashSet var1;
        try {
            var1 = new HashSet(this.routeToPool.keySet());
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    protected void enumAvailable(PoolEntryCallback<T, C> callback) {
        this.lock.lock();

        try {
            Iterator it = this.available.iterator();

            while(it.hasNext()) {
                PoolEntry<T,C> entry = (PoolEntry)it.next();
                callback.process(entry);
                if (entry.isClosed()) {
                    RouteSpecificPool<T, C> pool = this.getPool(entry.getRoute());
                    pool.remove(entry);
                    it.remove();
                }
            }

            this.purgePoolMap();
        } finally {
            this.lock.unlock();
        }
    }

    protected void enumLeased(PoolEntryCallback<T, C> callback) {
        this.lock.lock();

        try {
            Iterator it = this.leased.iterator();

            while(it.hasNext()) {
                PoolEntry<T,C> entry = (PoolEntry)it.next();
                callback.process(entry);
            }
        } finally {
            this.lock.unlock();
        }

    }

    private void purgePoolMap() {
        Iterator it = this.routeToPool.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<T, RouteSpecificPool<T, C>> entry = (Map.Entry)it.next();
            RouteSpecificPool<T, C> pool = (RouteSpecificPool)entry.getValue();
            if (pool.getPendingCount() + pool.getAllocatedCount() == 0) {
                it.remove();
            }
        }

    }

    public void closeIdle(long idletime, TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        long time = tunit.toMillis(idletime);
        if (time < 0L) {
            time = 0L;
        }

        final long deadline = System.currentTimeMillis() - time;
        this.enumAvailable(entry -> {
            if (entry.getUpdated() <= deadline) {
                entry.close();
            }

        });
    }

    public void closeExpired() {
        final long now = System.currentTimeMillis();
        this.enumAvailable(entry -> {
            if (entry.isExpired(now)) {
                entry.close();
            }

        });
    }

    public int getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    public void setValidateAfterInactivity(int ms) {
        this.validateAfterInactivity = ms;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[leased: ");
        buffer.append(this.leased);
        buffer.append("][available: ");
        buffer.append(this.available);
        buffer.append("][pending: ");
        buffer.append(this.pending);
        buffer.append("]");
        return buffer.toString();
    }
}
