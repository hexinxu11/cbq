package org.cbq.common.http;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * @author hexinxu
 * @version 2018年05月26日 hexinxu
 * @since 1.0
 */
public class HttpClientPool {
    private static PoolingHttpClientConnectionManager poolConnManager;
    private static final int MAX_TOTAL_POOL = 300;
    private static final int MAX_CONPER_ROUTE = 200;
    private static final int SOCKET_TIMEOUT = 200000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 300000;
    private static final int CONNECT_TIMEOUT = 100000;
    private static org.apache.http.client.CookieStore cookieStore = new BasicCookieStore();

    static {
        try {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,
                    new TrustSelfSignedStrategy())
                    .build();
            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolConnManager.setMaxTotal(MAX_TOTAL_POOL);
            poolConnManager.setDefaultMaxPerRoute(MAX_CONPER_ROUTE);
//            poolConnManager.setMaxPerRoute();
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SOCKET_TIMEOUT).build();
            poolConnManager.setDefaultSocketConfig(socketConfig);
        } catch (Exception e) {

        }
    }

    public static CloseableHttpClient getConnection(){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).setCookieSpec(CookieSpecs.DEFAULT).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager).setDefaultRequestConfig(requestConfig)
                .setConnectionManagerShared(true)
                .setDefaultCookieStore(getCookieStore())
                .build();
        if(poolConnManager!=null&&poolConnManager.getTotalStats()!=null){

            System.out.println("now client pool "+poolConnManager.getTotalStats().toString());
        }
        return httpClient;
    }

    public static org.apache.http.client.CookieStore getCookieStore(){
        if (null == cookieStore){
            synchronized (HttpClientPool.class){
                cookieStore = new BasicCookieStore();
            }
        }
        return cookieStore;
    }

    public static PoolStats getTotalStats() {
        return poolConnManager.getTotalStats();
    }
}
