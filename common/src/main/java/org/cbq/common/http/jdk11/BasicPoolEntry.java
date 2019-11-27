package org.cbq.common.http.jdk11;


import org.apache.http.nio.NHttpClientConnection;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/25 14:26
 * @Version 1.0
 **/
public class BasicPoolEntry extends PoolEntry<HttpRequest, HttpClient> {

    public BasicPoolEntry(String id, HttpRequest route, HttpClient conn, long timeToLive, TimeUnit tunit) {
        super(id, route, conn, timeToLive, tunit);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
