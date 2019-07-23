package org.cbq.common.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.cbq.common.exception.CheckRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/9 15:15
 * @Version 1.0
 **/
public class CloseableHttpAsyncClientPool {

    Logger logger = LoggerFactory.getLogger(CloseableHttpAsyncClientPool.class);

    private PoolingNHttpClientConnectionManager connectionManager;
    private org.apache.http.client.CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private RequestConfig requestConfig;
    private AsyncIdleConnectionEvictor connectionEvictor;

    public CloseableHttpAsyncClientPool(PoolingNHttpClientConnectionManager connectionManager, CookieStore cookieStore, CredentialsProvider credentialsProvider, RequestConfig requestConfig) {
        this.connectionManager = connectionManager;
        this.cookieStore = cookieStore;
        this.credentialsProvider = credentialsProvider;
        this.requestConfig = requestConfig;
    }

    public CloseableHttpAsyncClient build() {
        if (null == connectionManager) {
            logger.error("connectionManager is null!");
            throw new CheckRuntimeException("connectionManager is null!");
        }
        if (null == requestConfig) {
            logger.error("requestConfig is null!");
            throw new CheckRuntimeException("requestConfig is null!");
        }
        CloseableHttpAsyncClient asyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(requestConfig)
                .build();
        /**
         * http连接清理器
         */
        connectionEvictor = new AsyncIdleConnectionEvictor(connectionManager,1000L,TimeUnit.MINUTES);
        connectionEvictor.start();
        return asyncClient;
    }

    public static void main(String[] args) {
        try {
            HttpGet httpGet = new HttpGet("https://www.mzitu.com/tag/youhuo/");
            CloseableHttpAsyncClientPool clientPool =  AsyncClientPoolFactory.custom()
                    .setConnectTimeout(5000)
                    .setProxy(new HttpHost("121.10.141.149",8080))
                    .build();
            CloseableHttpAsyncClient client  = clientPool.build();
            client.start();
            client.execute(httpGet, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse httpResponse) {
                    String body="";
                    //这里使用EntityUtils.toString()方式时会大概率报错，原因：未接受完毕，链接已关
                    try {
                        HttpEntity entity = httpResponse.getEntity();
                        if (entity != null) {
                            final InputStream instream = entity.getContent();
                            try {
                                final StringBuilder sb = new StringBuilder();
                                final char[] tmp = new char[1024];
                                final Reader reader = new InputStreamReader(instream,"utf-8");
                                int l;
                                while ((l = reader.read(tmp)) != -1) {
                                    sb.append(tmp, 0, l);
                                }
                                body = sb.toString();
                            } finally {
                                instream.close();
                                EntityUtils.consume(entity);
                            }
                        }
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                  System.out.println(body);
                }

                @Override
                public void failed(Exception e) {

                }

                @Override
                public void cancelled() {

                }
            });
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }
}
