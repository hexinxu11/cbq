package org.cbq.common.queue;

import com.qcloud.cmq.client.common.ClientConfig;
import com.qcloud.cmq.client.consumer.Consumer;
import com.qcloud.cmq.client.exception.MQClientException;
import com.qcloud.cmq.client.producer.Producer;
import org.cbq.common.config.TxCmqConfiguration;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/2 19:37
 * @Version 1.0
 **/
public class TxCmqFactory {


    public static Producer buildProducer(TxCmqConfiguration cmqConfiguration){
        try {
            Producer producer = new Producer();
            producer.setNameServerAddress(cmqConfiguration.getNameServer());
            producer.setSecretId(cmqConfiguration.getSecretId());
            producer.setSecretKey(cmqConfiguration.getSecretKey());
            producer.setSignMethod(ClientConfig.SIGN_METHOD_SHA256);
            /**
             * 设置发送消息失败时，重试的次数，设置为0表示不重试，默认为2
             */
            producer.setRetryTimesWhenSendFailed(3);
            /**
             * 设置请求超时时间， 默认3000ms
             */
            producer.setRequestTimeoutMS(5000);
            producer.start();
            return producer;
        }catch (MQClientException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Consumer buildConsumer(TxCmqConfiguration cmqConfiguration){
        try {
            Consumer consumer = new Consumer();
            consumer.setNameServerAddress(cmqConfiguration.getNameServer());
            consumer.setSecretId(cmqConfiguration.getSecretId());
            consumer.setSecretKey(cmqConfiguration.getSecretKey());
            consumer.setSignMethod(ClientConfig.SIGN_METHOD_SHA256);
            /** 批量拉取时最大拉取消息数量，范围为1-16 **/
            consumer.setBatchPullNumber(16);
            consumer.start();
            return consumer;
        } catch (MQClientException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
