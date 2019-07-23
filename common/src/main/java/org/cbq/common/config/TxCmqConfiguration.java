package org.cbq.common.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 20:35
 * @Version 1.0
 **/
@Component
@Configuration
public class TxCmqConfiguration {

    @NacosValue(value = "${cbq.queue.tx.name-server:http://cmq-nameserver-gz.tencentcloudapi.com}")
    private String nameServer;

    @NacosValue(value = "${cbq.queue.tx.secretId:AKID52tnT2vQtBOUYW6xMckiYHuQnHamvuIV}")
    private String secretId;

    @NacosValue(value = "${cbq.queue.tx.secretKey:FMI6nPpGJ0Wr34xissmX8i6B5xeActDU}")
    private String secretKey;

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
