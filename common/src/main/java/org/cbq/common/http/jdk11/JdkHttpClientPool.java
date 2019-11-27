package org.cbq.common.http.jdk11;

import com.google.common.collect.Maps;

import java.net.http.HttpClient;
import java.util.Map;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/25 11:03
 * @Version 1.0
 **/
public class JdkHttpClientPool {

    private final static Map<String, HttpClient> domain_pool = Maps.newHashMap();

}
