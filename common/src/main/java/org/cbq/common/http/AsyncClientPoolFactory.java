package org.cbq.common.http;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.nio.reactor.IOReactorException;

import java.net.InetAddress;
import java.util.Collection;

import static org.cbq.common.http.AsyncClientConfiguration.*;

/**
 * 连接池工厂-对每个url域任务都会创建一个连接池
 *
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/9 14:40
 * @Version 1.0
 **/
public class AsyncClientPoolFactory implements Cloneable {


    public static AsyncClientPoolFactory.Builder custom() {
        return new AsyncClientPoolFactory.Builder();
    }


    public static class Builder {
        private boolean expectContinueEnabled;
        private HttpHost proxy;
        private InetAddress localAddress;
        private String cookieSpec;
        private boolean redirectsEnabled = true;
        private boolean relativeRedirectsAllowed = true;
        private boolean circularRedirectsAllowed;
        private int maxRedirects = 50;
        private boolean authenticationEnabled = true;
        private Collection<String> targetPreferredAuthSchemes;
        private Collection<String> proxyPreferredAuthSchemes;
        private int connectionRequestTimeout = -1;
        private int connectTimeout = -1;
        private int socketTimeout = -1;
        private boolean contentCompressionEnabled = true;

        Builder() {

        }

        public AsyncClientPoolFactory.Builder setExpectContinueEnabled(boolean expectContinueEnabled) {
            this.expectContinueEnabled = expectContinueEnabled;
            return this;
        }

        public AsyncClientPoolFactory.Builder setProxy(HttpHost proxy) {
            this.proxy = proxy;
            return this;
        }

        public AsyncClientPoolFactory.Builder setLocalAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            return this;
        }

        public AsyncClientPoolFactory.Builder setCookieSpec(String cookieSpec) {
            this.cookieSpec = cookieSpec;
            return this;
        }

        public AsyncClientPoolFactory.Builder setRedirectsEnabled(boolean redirectsEnabled) {
            this.redirectsEnabled = redirectsEnabled;
            return this;
        }

        public AsyncClientPoolFactory.Builder setRelativeRedirectsAllowed(boolean relativeRedirectsAllowed) {
            this.relativeRedirectsAllowed = relativeRedirectsAllowed;
            return this;
        }

        public AsyncClientPoolFactory.Builder setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
            this.circularRedirectsAllowed = circularRedirectsAllowed;
            return this;
        }

        public AsyncClientPoolFactory.Builder setMaxRedirects(int maxRedirects) {
            this.maxRedirects = maxRedirects;
            return this;
        }

        public AsyncClientPoolFactory.Builder setAuthenticationEnabled(boolean authenticationEnabled) {
            this.authenticationEnabled = authenticationEnabled;
            return this;
        }

        public AsyncClientPoolFactory.Builder setTargetPreferredAuthSchemes(Collection<String> targetPreferredAuthSchemes) {
            this.targetPreferredAuthSchemes = targetPreferredAuthSchemes;
            return this;
        }

        public AsyncClientPoolFactory.Builder setProxyPreferredAuthSchemes(Collection<String> proxyPreferredAuthSchemes) {
            this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes;
            return this;
        }

        public AsyncClientPoolFactory.Builder setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return this;
        }

        public AsyncClientPoolFactory.Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public AsyncClientPoolFactory.Builder setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public AsyncClientPoolFactory.Builder setDecompressionEnabled(boolean decompressionEnabled) {
            this.contentCompressionEnabled = decompressionEnabled;
            return this;
        }

        public AsyncClientPoolFactory.Builder setContentCompressionEnabled(boolean contentCompressionEnabled) {
            this.contentCompressionEnabled = contentCompressionEnabled;
            return this;
        }

        public CloseableHttpAsyncClientPool build() throws IOReactorException {

            RequestConfig requestConfig = RequestConfig.copy(DEFAULT_REQUEST_CONFIG)
                    .setSocketTimeout(this.socketTimeout)
                    .setConnectTimeout(this.connectTimeout)
                    .setConnectionRequestTimeout(this.connectionRequestTimeout)
                    .setAuthenticationEnabled(this.authenticationEnabled)
                    .setCircularRedirectsAllowed(this.circularRedirectsAllowed)
                    .setContentCompressionEnabled(this.contentCompressionEnabled)
                    .setTargetPreferredAuthSchemes(this.targetPreferredAuthSchemes)
                    .setRelativeRedirectsAllowed(this.relativeRedirectsAllowed)
                    .setRedirectsEnabled(this.redirectsEnabled)
                    .setProxyPreferredAuthSchemes(this.proxyPreferredAuthSchemes)
                    .setProxy(this.proxy)
                    .setLocalAddress(this.localAddress)
                    .setExpectContinueEnabled(this.expectContinueEnabled)
                    .setCookieSpec(this.cookieSpec)
                    .setTargetPreferredAuthSchemes(this.targetPreferredAuthSchemes)
                    .setMaxRedirects(this.maxRedirects)
                    .build();
            return new CloseableHttpAsyncClientPool(CONN_MANAGER, COOKIE_STORE, null, requestConfig);
        }
    }
}
