package org.cbq.common.http;

import org.apache.http.*;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParser;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.nio.NHttpMessageParser;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/8 19:48
 * @Version 1.0
 **/
public class AsyncClientConfiguration {

    /**
     * 使用自定义消息分析器/编写器自定义HTTP方式
     * 消息解析后写入到数据流中
     **/
    public final static NHttpMessageParserFactory<HttpResponse> RESPONSE_PARSER_FACTORY = new DefaultHttpResponseParserFactory() {
        @Override
        public NHttpMessageParser<HttpResponse> create(
                final SessionInputBuffer buffer,
                final MessageConstraints constraints) {
            LineParser lineParser = new BasicLineParser() {
                @Override
                public Header parseHeader(final CharArrayBuffer buffer) {
                    try {
                        return super.parseHeader(buffer);
                    } catch (ParseException ex) {
                        return new BasicHeader(buffer.toString(), null);
                    }
                }
            };
            return new DefaultHttpResponseParser(
                    buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints);
        }
    };

    public final static NHttpMessageWriterFactory<HttpRequest> REQUEST_WRITER_FACTORY = new DefaultHttpRequestWriterFactory();

    /**
     * 自定义连接工厂
     * 初始化HTTP连接
     * 工厂可以定义http连接消息配置
     * 单个连接要使用的解析器/编写器例程。
     **/
    protected static NHttpConnectionFactory<ManagedNHttpClientConnection> CONN_FACTORY = new ManagedNHttpClientConnectionFactory(
            REQUEST_WRITER_FACTORY, RESPONSE_PARSER_FACTORY, HeapByteBufferAllocator.INSTANCE);

    /**
     * 完全初始化后的客户端HTTP连接对象可以绑定到任意网络套接字。
     * 它控制远程地址的连接和与本地地址的绑定
     **/
    protected static SSLContext SSL_CONTEXT = SSLContexts.createSystemDefault();

    /**
     * 使用自定义主机名验证程序
     **/
    protected static HostnameVerifier HOSTNAME_VERIFIER = new DefaultHostnameVerifier();

    /**
     * 为自定义连接会话策略创建注册表 协议方案
     **/
    protected static Registry<SchemeIOSessionStrategy> SESSION_STRATEGY_REGISTRY = RegistryBuilder.<SchemeIOSessionStrategy>create()
            .register("http", NoopIOSessionStrategy.INSTANCE)
            .register("https", new SSLIOSessionStrategy(SSL_CONTEXT, HOSTNAME_VERIFIER))
            .build();

    /**
     * 使用自定义DNS解析程序覆盖系统DNS解析。
     */
    protected static DnsResolver DNS_RESOLVER = new CbqDnsResolver();

    /**
     * 创建I/O线程配置
     * setIoThreadCount 定义I/O反应器要使用的I/O分派线程数
     * setConnectTimeout 定义非阻塞连接请求的默认连接超时值。
     * setSoTimeout 定义非阻塞I/O操作的默认套接字超时值。
     */
    protected static IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
            .setConnectTimeout(30000)
            .setSoTimeout(30000)
            .build();

    /**
     * 创建自定义I/O Reactor端口
     **/
    protected static ConnectingIOReactor IO_REACTOR = null;


    static {
        try {
            IO_REACTOR = new DefaultConnectingIOReactor(ioReactorConfig);

        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建自定义配置连接管理器
     */
    protected static PoolingNHttpClientConnectionManager CONN_MANAGER = new PoolingNHttpClientConnectionManager(
            IO_REACTOR, CONN_FACTORY, SESSION_STRATEGY_REGISTRY);
    /**
     * 创建消息体约束
     * setMaxHeaderCount 允许的请求头字段的最大数目
     * setMaxLineLength 可以接收最大数据
     */
    protected static MessageConstraints MESSAGE_CONSTRAINTS = MessageConstraints.custom()
            .setMaxHeaderCount(2000)
            .setMaxLineLength(20000)
            .build();

    /**
     * 创建连接配置
     * setMalformedInputAction 设置输入格式错误的action
     * setUnmappableInputAction 设置不可映射的输入操作
     * setCharset 设置编码
     * setMessageConstraints 设置消息体约束
     */
    protected static ConnectionConfig CONNECTION_CONFIG = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(Consts.UTF_8)
            .setMessageConstraints(MESSAGE_CONSTRAINTS)
            .build();

    /**
     * 创建cookie管理器
     */
    protected static org.apache.http.client.CookieStore COOKIE_STORE = new BasicCookieStore();

    /**
     * 创建自定义凭证管理器
     */
//    protected static CredentialsProvider CREDENTIALS_PROVIDER = new BasicCredentialsProvider();

    /**
     * 创建全局请求配置(公共的请求配置，可以被子集继承)
     * setCookieSpec 设置cookie策略(DEFAULT 默认策略)
     */
    protected static RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.DEFAULT)
            .setExpectContinueEnabled(true)
            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
            .build();

    /**
     * 创建子集请求配置->全局请求配置 (可用于指定URL)
     */
    RequestConfig requestConfig = RequestConfig.copy(DEFAULT_REQUEST_CONFIG)
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();

    /**
     * 使用给定的自定义依赖项和配置创建httpclient。
     * setConnectionManager 设置连接管理器
     * setDefaultCredentialsProvider 设置默认认证
     * setDefaultRequestConfig 设置请求配置
     * setProxy 设置代理
     */
    CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
            .setConnectionManager(CONN_MANAGER)
            .setDefaultCookieStore(COOKIE_STORE)
//            .setDefaultCredentialsProvider(CREDENTIALS_PROVIDER)
            .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
            .build();


    static {
        /**
         * 将连接管理器配置为使用连接配置
         * 默认
         */
        CONN_MANAGER.setDefaultConnectionConfig(CONNECTION_CONFIG);
        /**
         * maxTotal 最大连接数
         * setDefaultMaxPerRoute 默认的每个路由的最大连接数
         * setMaxPerRoute 设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute
         * setCredentials 设置认证信息
         */
        CONN_MANAGER.setMaxTotal(100);
        CONN_MANAGER.setDefaultMaxPerRoute(10);
    }

}
