package org.cbq.common.http.jdk11;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.concurrent.Executors.*;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/17 15:14
 * @Version 1.0
 **/
public class JdkHttpClient1 {

    static HttpClient.Builder builder = HttpClient.newBuilder();
    static HttpRequest.Builder reBuilder = HttpRequest.newBuilder();
    static List<Long> countTime = Collections.synchronizedList( Lists.newArrayList());

    static int DEFAULT_THREAD_POOL_MAX = 500;
    static int DEFAULT_MAX_CONPER_ROUTE = 50;

    public static HttpClient builder() {

        return builder.connectTimeout(Duration.ofMillis(10000))
                .executor(newFixedThreadPool(DEFAULT_THREAD_POOL_MAX))
                .build();
    }

    static class HttpClientTest1 implements Runnable {
        String url;

        public HttpClientTest1(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                long startTime = System.currentTimeMillis();
//                System.out.println(Thread.currentThread().getId());
                HttpClient client = builder();
                HttpResponse<String> response = client.send(reBuilder.uri(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    countTime.add((System.currentTimeMillis() - startTime));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String url = "https://www.baidu.com/s?ie=UTF-8&wd=";
        IntStream.range(0,100).forEach(f->{
            HttpClientTest1 test1 = new HttpClientTest1(url+f);
            new Thread(test1).start();
        });
        while (countTime.size()!=100){
            try {
                Thread.sleep(1000);
                System.out.println(countTime.stream().count());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println("-----------------------");
        countTime.stream().forEach(System.out::println);
        System.out.println(countTime.stream().mapToLong(x->x).sum());

    }
}
