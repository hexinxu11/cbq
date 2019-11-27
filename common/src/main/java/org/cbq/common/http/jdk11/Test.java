package org.cbq.common.http.jdk11;

import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/25 11:32
 * @Version 1.0
 **/
public class Test extends PoolEntry {

    public Test(String id, Object route, Object conn, long timeToLive, TimeUnit tunit) {
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
