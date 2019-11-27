package org.cbq.common.http;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.cbq.common.exception.HttpClientRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.cbq.common.constant.CbqConstant.ZERO;


/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/9/24 15:55
 * @Version 1.0
 **/
public class ProxyIpCheckAsyncClientFactory {

    static final Logger logger = LoggerFactory.getLogger(ProxyIpCheckAsyncClientFactory.class);

    private static final Map<Long, CloseableHttpAsyncClient> DELAYED_CLOSE = Maps.newConcurrentMap();


    /**
     * 延迟关闭时间 单位毫秒
     */
    private static final long DELAYED_COLSE_TIME = 5000;

    private static final int CONNECT_TIMEOUT = 10000;

    private static final int SOCKET_TIMEOUT = 10000;

    private static final int CONNECTION_REQUEST_TIMEOUT = 10000;

    private static final int MAX_TOTAL = 2;

    private static final int DEFAULT_MAX_PER_ROUTE = 2;

    private static final int CORE_POOL_SIZE = 1;

    private static boolean running = true;

    private static final String NAMING_PATTERN = "delayed-close-schedule-pool-%d";

    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,
            new BasicThreadFactory.Builder().namingPattern(NAMING_PATTERN).daemon(true).build());


    private ProxyIpCheckAsyncClientFactory() {
    }

    public static CloseableHttpAsyncClient build(String ip, int port, String scheme) {
        try {
            CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom().
                    setConnectionManager(createPoolingNHttpClientConnectionManager(createConnectingIOReactor()))
                    .setDefaultRequestConfig(createRequestConfig(ip, port, scheme))
                    .build();
            DELAYED_CLOSE.put(System.currentTimeMillis(), closeableHttpAsyncClient);
            delayedCloseTimer();
            return closeableHttpAsyncClient;
        } catch (IOReactorException e) {
            logger.error(" build CloseableHttpAsyncClient error！ error info : ", e);
            throw new HttpClientRuntimeException("build CloseableHttpAsyncClient error！");
        }
    }

    private static void delayedCloseTimer() {
        if (running) {
            running = false;
            try {
                DelayedCloseTask task = new DelayedCloseTask();
                executorService.scheduleAtFixedRate(task, ZERO, DELAYED_COLSE_TIME, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.error("delayedCloseTimer Initialization failure！ ", e);
                running = true;
            }

        }

    }

    private static RequestConfig createRequestConfig(String ip, int port, String scheme) {

        return RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setProxy(new HttpHost(ip, port, scheme))
                .build();
    }

    private static ConnectingIOReactor createConnectingIOReactor() throws IOReactorException {

        return new DefaultConnectingIOReactor(IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build());

    }

    private static PoolingNHttpClientConnectionManager createPoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor) {
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setMaxTotal(MAX_TOTAL);
        connManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        return connManager;
    }

    static class DelayedCloseTask extends TimerTask {

        @Override
        public void run() {
            if (!DELAYED_CLOSE.isEmpty()) {
                logger.info("DELAYED_COLSE_MAP count : {}", DELAYED_CLOSE.size());
                Map<Long, CloseableHttpAsyncClient> closeMap = DELAYED_CLOSE.entrySet().stream().filter(f -> (f.getKey() + DELAYED_COLSE_TIME) < System.currentTimeMillis()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                logger.info("DELAYED_COLSE count : {}", DELAYED_CLOSE.size());
                if (!closeMap.isEmpty()) {
                    closeMap.entrySet().stream().forEach(f -> {
                        DELAYED_CLOSE.remove(f.getKey());
                        if (f.getValue().isRunning()) {
                            try {
                                f.getValue().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}
