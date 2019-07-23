package org.cbq.common.http;

import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.util.Args;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 定期清理无效的http连接
 *
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/9 16:34
 * @Version 1.0
 **/
public class AsyncIdleConnectionEvictor {

    private final NHttpClientConnectionManager connectionManager;
    private final ThreadFactory threadFactory;
    private final Thread thread;
    private final long sleepTimeMs;
    private final long maxIdleTimeMs;
    private volatile Exception exception;

    public AsyncIdleConnectionEvictor(final NHttpClientConnectionManager connectionManager, ThreadFactory threadFactory, long sleepTime, TimeUnit sleepTimeUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this.connectionManager =  Args.notNull(connectionManager, "Connection manager");
        this.threadFactory =  (threadFactory != null ? threadFactory : new AsyncIdleConnectionEvictor.DefaultThreadFactory());
        this.sleepTimeMs = sleepTimeUnit != null ? sleepTimeUnit.toMillis(sleepTime) : sleepTime;
        this.maxIdleTimeMs = maxIdleTimeUnit != null ? maxIdleTimeUnit.toMillis(maxIdleTime) : maxIdleTime;
        this.thread = this.threadFactory.newThread(() -> {
            while (true) {
                try {
                    if (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(AsyncIdleConnectionEvictor.this.sleepTimeMs);
                        connectionManager.closeExpiredConnections();
                        if (AsyncIdleConnectionEvictor.this.maxIdleTimeMs > 0L) {
                            connectionManager.closeIdleConnections(AsyncIdleConnectionEvictor.this.maxIdleTimeMs, TimeUnit.MILLISECONDS);
                        }
                        continue;
                    }
                } catch (Exception var2) {
                    AsyncIdleConnectionEvictor.this.exception = var2;
                }

                return;
            }
        });
    }

    public AsyncIdleConnectionEvictor(NHttpClientConnectionManager connectionManager, long sleepTime, TimeUnit sleepTimeUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this(connectionManager,  null, sleepTime, sleepTimeUnit, maxIdleTime, maxIdleTimeUnit);
    }

    public AsyncIdleConnectionEvictor(NHttpClientConnectionManager connectionManager, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        this(connectionManager,  null, maxIdleTime > 0L ? maxIdleTime : 5L, maxIdleTimeUnit != null ? maxIdleTimeUnit : TimeUnit.SECONDS, maxIdleTime, maxIdleTimeUnit);
    }

    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.thread.interrupt();
    }

    public boolean isRunning() {
        return this.thread.isAlive();
    }

    public void awaitTermination(long time, TimeUnit tunit) throws InterruptedException {
        this.thread.join((tunit != null ? tunit : TimeUnit.MILLISECONDS).toMillis(time));
    }

    static class DefaultThreadFactory implements ThreadFactory {
        DefaultThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Connection evictor");
            t.setDaemon(true);
            return t;
        }
    }
}
