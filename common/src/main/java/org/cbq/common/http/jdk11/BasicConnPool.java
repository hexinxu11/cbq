package org.cbq.common.http.jdk11;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/25 14:51
 * @Version 1.0
 **/
public class BasicConnPool extends AbstractConnPool<HttpRequest, HttpClient>{
    private static final AtomicLong COUNTER = new AtomicLong();
    private final Log log = LogFactory.getLog(BasicConnPool.class);
    private final long timeToLive;
    private final TimeUnit tunit;

    public BasicConnPool(ConnFactory<HttpRequest, HttpClient> connFactory, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit tunit) {
        super(connFactory, defaultMaxPerRoute, maxTotal);
        this.timeToLive = timeToLive;
        this.tunit = tunit;
    }

    @Override
    protected BasicPoolEntry createEntry(HttpRequest route, HttpClient conn) {
        String id = Long.toString(COUNTER.getAndIncrement());
        return new BasicPoolEntry(id, route, conn, this.timeToLive, this.tunit);
    }
}
