package org.cbq.common.queue;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.qcloud.cmq.client.common.ResponseCode;
import com.qcloud.cmq.client.consumer.Consumer;
import com.qcloud.cmq.client.consumer.ReceiveResult;
import com.qcloud.cmq.client.exception.MQClientException;
import com.qcloud.cmq.client.exception.MQServerException;
import com.qcloud.cmq.client.producer.Producer;
import com.qcloud.cmq.client.producer.SendResult;
import org.cbq.common.config.TxCmqConfiguration;
import org.cbq.common.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 19:52
 * @Version 1.0
 **/
@Component
public class DefaultTxQueue extends AbstractBasicQueue{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TxCmqConfiguration cmqConfiguration;

    private  Map<String,Producer> producers = Maps.newConcurrentMap();

    private  Map<String,Consumer> consumers = Maps.newConcurrentMap();

    @Override
    public boolean isQueueExist(String queueName) {
        return producers.containsKey(queueName);
    }

    @Override
    public boolean createQueue(String queueName) {
        if (!producers.containsKey(queueName)){
            Producer producer = TxCmqFactory.buildProducer(cmqConfiguration);
            if (producer != null){
                producers.put(queueName,producer);
            }

        }
        if (!consumers.containsKey(queueName)){
            Consumer consumer = TxCmqFactory.buildConsumer(cmqConfiguration);
            if (consumer!=null){
                consumers.put(queueName,consumer);
            }
        }
        return false;
    }


    @Override
    public boolean DeleteQueue(String queueName) {
        if (!producers.containsKey(queueName)){
            producers.remove(queueName);
        }
        return false;
    }

    @Override
    public Map<String, String> getQueueAttributes(String queueName) {
        return null;
    }

    @Override
    public boolean offerDownloadTaskPacket(DownloadTaskPacket downloadTaskPacket) {
        return sendMsg(downloadTaskPacket);
    }

    @Override
    public boolean offerHtmlParseTaskPacket(HtmlParseTaskPacket htmlParseTaskPacket) {
        return sendMsg(htmlParseTaskPacket);
    }

    @Override
    public boolean offerUrlCleanTaskPacket(UrlCleanTaskPacket urlCleanTaskPacket) {
        return sendMsg(urlCleanTaskPacket);
    }

    @Override
    public boolean offerPersistenceTaskPacket(PersistenceTaskPacket persistenceTaskPacket) {
        return sendMsg(persistenceTaskPacket);
    }

    @Override
    public boolean offerDownloadTaskPacket(DownloadTaskPacket downloadTaskPacket, long timeout, TimeUnit unit) {
        return sendMsg(downloadTaskPacket);
    }

    @Override
    public boolean offerHtmlParseTaskPacket(HtmlParseTaskPacket htmlParseTaskPacket, long timeout, TimeUnit unit) {
        return sendMsg(htmlParseTaskPacket);
    }

    @Override
    public boolean offerUrlCleanTaskPacket(UrlCleanTaskPacket urlCleanTaskPacket, long timeout, TimeUnit unit) {
        return sendMsg(urlCleanTaskPacket);
    }

    @Override
    public boolean offerPersistenceTaskPacket(PersistenceTaskPacket persistenceTaskPacket, long timeout, TimeUnit unit) {
        return sendMsg(persistenceTaskPacket);
    }

    @Override
    public <T> T pollDownloadTaskPacket(String queueName, Class<T> clzz) {
        return receiveMsg(queueName,clzz);
    }

    @Override
    public <T> T pollHtmlParseTaskPacket(String queueName, Class<T> clzz) {
        return receiveMsg(queueName,clzz);
    }

    @Override
    public <T> T pollUrlCleanTaskPacket(String queueName, Class<T> clzz) {
        return receiveMsg(queueName,clzz);
    }

    @Override
    public <T> T pollPersistenceTaskPacket(String queueName, Class<T> clzz) {
        return receiveMsg(queueName,clzz);
    }

    @Override
    public <T> T pollDownloadTaskPacket(String queueName, Class<T> clzz, long timeout, TimeUnit unit) {
        return receiveMsg(queueName,clzz,timeout,unit);
    }

    @Override
    public <T> T pollHtmlParseTaskPacket(String queueName, Class<T> clzz, long timeout, TimeUnit unit) {
        return receiveMsg(queueName,clzz,timeout,unit);
    }

    @Override
    public <T> T pollUrlCleanTaskPacket(String queueName, Class<T> clzz, long timeout, TimeUnit unit) {
        return receiveMsg(queueName,clzz,timeout,unit);
    }

    @Override
    public <T> T pollPersistenceTaskPacket(String queueName, Class<T> clzz, long timeout, TimeUnit unit) {
        return receiveMsg(queueName,clzz,timeout,unit);
    }

    private boolean sendMsg(BasicPacket packet){
        String queueName = packet.getDownloadTaskQueueName();
        if (producers.containsKey(queueName)){
            try {
                SendResult result = producers.get(queueName).send(queueName, JSON.toJSONString(packet));
                if (result.getReturnCode() == ResponseCode.SUCCESS) {
                    logger.info("==> send success! msg_id:{} request_id:{}",result.getMsgId(),result.getRequestId());
                    return true;
                } else {
                    logger.error("==> code:{} error:{}",result.getReturnCode(),result.getErrorMsg());
                }
            } catch (MQClientException e) {
                logger.error("offer task packet error:",e);
            } catch (MQServerException e) {
                logger.error("offer task packet error:",e);
            }
        }
        return false;
    }

    private <T> T receiveMsg(String queueName,Class<T> clazz){
        return receiveMsg(queueName,clazz,0,TimeUnit.SECONDS);
    }

    private <T> T receiveMsg(String queueName,Class<T> clazz,long timeout, TimeUnit unit){

        if (consumers.containsKey(queueName)){
            try {
                timeout = timeoutConversion(timeout,unit);
                ReceiveResult result = consumers.get(queueName).receiveMsg(queueName,new BigDecimal(timeout).intValue());
                if (result.getReturnCode() == ResponseCode.SUCCESS){
                    logger.info("receive success, msgId:{} ReceiptHandle:{} Data:{}",result.getMessage().getMessageId(),result.getMessage().getReceiptHandle(),result.getMessage().getData());
                    T t = JSON.parseObject(result.getMessage().getData(),clazz);
                    consumers.get(queueName).deleteMsg(queueName, result.getMessage().getReceiptHandle());
                    return t;
                }else {
                    logger.error("==> receive Error code:{} error:{}",result.getReturnCode(),result.getErrorMessage());
                }
            } catch (MQClientException e) {
                logger.error("offer download task packet error:",e);
            } catch (MQServerException e) {
                logger.error("offer download task packet error:",e);
            }
        }
        return null;
    }

    private long timeoutConversion(long timeout, TimeUnit unit){
        if (unit!=null){
            switch (unit){
                case DAYS:return timeout*86400;
                case HOURS: return timeout*3600;
                case MINUTES: return timeout*60;
                case SECONDS: return timeout;
                case NANOSECONDS: return new BigDecimal(timeout).divide(new BigDecimal(1000000000)).longValue();
                case MICROSECONDS: return new BigDecimal(timeout).divide(new BigDecimal(1000000)).longValue();
                case MILLISECONDS: return new BigDecimal(timeout).divide(new BigDecimal(1000)).longValue();
                default:return timeout;
            }
        }
        return timeout;
    }

    public void setCmqConfiguration(TxCmqConfiguration cmqConfiguration) {
        this.cmqConfiguration = cmqConfiguration;
    }

    public static void main(String[] args) {
        DefaultTxQueue defaultTxQueue = new DefaultTxQueue();
        TxCmqConfiguration cmqConfiguration = new TxCmqConfiguration();
        cmqConfiguration.setNameServer("http://cmq-nameserver-gz.tencentcloudapi.com");
        cmqConfiguration.setSecretId("AKID52tnT2vQtBOUYW6xMckiYHuQnHamvuIV");
        cmqConfiguration.setSecretKey("FMI6nPpGJ0Wr34xissmX8i6B5xeActDU");
        defaultTxQueue.setCmqConfiguration(cmqConfiguration);
        defaultTxQueue.createQueue("kok-1");
        DownloadTaskPacket taskPacket = new DownloadTaskPacket();
        taskPacket.setDomain("www.liujingsuiyue.com");
        taskPacket.setDownloadTaskQueueName("kok-1");
        taskPacket.setPageParse("tx.parse.HtmlParse");
        taskPacket.setParseTaskQueueName("kk1");
        defaultTxQueue.offerDownloadTaskPacket(taskPacket);
        DownloadTaskPacket taskPacket1 = defaultTxQueue.pollDownloadTaskPacket(taskPacket.getDownloadTaskQueueName(),DownloadTaskPacket.class);
        System.out.println(JSON.toJSONString("msg >>> "+taskPacket1));
    }
}
