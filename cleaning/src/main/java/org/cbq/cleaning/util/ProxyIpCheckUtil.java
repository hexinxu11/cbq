package org.cbq.cleaning.util;


import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.cbq.cleaning.entity.IpBaseInfo;
import org.cbq.cleaning.vo.IpVisitVo;
import org.cbq.common.http.HttpClientPool;
import org.cbq.common.http.ProxyIpCheckAsyncClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.function.Consumer;

/**
 * 检测代理ip是否可用
 *
 * @Description TODO
 * @Author kok
 * @Dte 2019/9/24 15:48
 * @Version 1.0
 **/
public class ProxyIpCheckUtil {

    static Logger logger = LoggerFactory.getLogger(ProxyIpCheckUtil.class);


    public static boolean checkProxy(String ip, int port, String scheme, String targetAddress) {

        HttpHost proxy = new HttpHost(ip, port, scheme);
        CloseableHttpClient closeableHttpClient = HttpClientPool.getConnection(proxy);
        HttpGet httpGet = new HttpGet(targetAddress);

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            closeableHttpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            logger.error("ip[{}] proxy check failed", ip);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        IpBaseInfo baseInfo = new IpBaseInfo();
        baseInfo.setId(1);
        baseInfo.setIp("42.238.89.154");
        baseInfo.setPort(9999);
        baseInfo.setReqMethod("http");
        String targetAddress = "http://www.baidu.com";
        String tar1 = "https://www.meizitu.com/";
        String tar2 = "http://139.199.225.187:8080/msp-cas";
        checkProxy(baseInfo.getIp(), baseInfo.getPort(), baseInfo.getReqMethod(), targetAddress);

    }

}



