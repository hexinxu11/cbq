package org.cbq.common.http.jdk11;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/25 11:23
 * @Version 1.0
 **/
@Contract(
        threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public abstract class PoolEntry<T, C>  {

    private final String id;
    private final T route;
    private final C conn;
    private final long created;
    private final long validityDeadline;
    private long updated;
    private long expiry;
    private volatile Object state;

    public PoolEntry(String id, T route, C conn, long timeToLive, TimeUnit tunit) {
        Args.notNull(route, "Route");
        Args.notNull(conn, "Connection");
        Args.notNull(tunit, "Time unit");
        this.id = id;
        this.route = route;
        this.conn = conn;
        this.created = System.currentTimeMillis();
        this.updated = this.created;
        if (timeToLive > 0L) {
            long deadline = this.created + tunit.toMillis(timeToLive);
            this.validityDeadline = deadline > 0L ? deadline : 9223372036854775807L;
        } else {
            this.validityDeadline = 9223372036854775807L;
        }

        this.expiry = this.validityDeadline;
    }

    public PoolEntry(String id, T route, C conn) {
        this(id, route, conn, 0L, TimeUnit.MILLISECONDS);
    }

    public String getId() {
        return this.id;
    }

    public T getRoute() {
        return this.route;
    }

    public C getConnection() {
        return this.conn;
    }

    public long getCreated() {
        return this.created;
    }

    public long getValidityDeadline() {
        return this.validityDeadline;
    }

    /** @deprecated */
    @Deprecated
    public long getValidUnit() {
        return this.validityDeadline;
    }

    public Object getState() {
        return this.state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public synchronized long getUpdated() {
        return this.updated;
    }

    public synchronized long getExpiry() {
        return this.expiry;
    }

    public synchronized void updateExpiry(long time, TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        this.updated = System.currentTimeMillis();
        long newExpiry;
        if (time > 0L) {
            newExpiry = this.updated + tunit.toMillis(time);
        } else {
            newExpiry = 9223372036854775807L;
        }

        this.expiry = Math.min(newExpiry, this.validityDeadline);
    }

    public synchronized boolean isExpired(long now) {
        return now >= this.expiry;
    }

    public abstract void close();

    public abstract boolean isClosed();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PoolEntry<?, ?> poolEntry = (PoolEntry<?, ?>) o;

        return new EqualsBuilder()
                .append(created, poolEntry.created)
                .append(validityDeadline, poolEntry.validityDeadline)
                .append(updated, poolEntry.updated)
                .append(expiry, poolEntry.expiry)
                .append(id, poolEntry.id)
                .append(route, poolEntry.route)
                .append(conn, poolEntry.conn)
                .append(state, poolEntry.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(route)
                .append(conn)
                .append(created)
                .append(validityDeadline)
                .append(updated)
                .append(expiry)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[id:");
        buffer.append(this.id);
        buffer.append("][route:");
        buffer.append(this.route);
        buffer.append("][state:");
        buffer.append(this.state);
        buffer.append("]");
        return buffer.toString();
    }
}
