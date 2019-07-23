package org.cbq.common.queue;

import org.apache.commons.lang3.StringUtils;
import org.cbq.common.exception.QueueRuntimeException;
import org.cbq.common.packet.BasicPacket;

import java.util.Map;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 19:52
 * @Version 1.0
 **/
public abstract class AbstractBasicQueue implements BasicQueue {

    public abstract boolean isQueueExist(String queueName);

    public abstract boolean createQueue(String queueName);

    public abstract boolean DeleteQueue(String queueName);

    public abstract Map<String, String> getQueueAttributes(String queueName);

    @Override
    public BasicQueue initQueue(BasicPacket basicPacket) {

        checkCondition(basicPacket);
        if (!isQueueExist(basicPacket.getDownloadTaskQueueName())){
            createQueue(basicPacket.getDownloadTaskQueueName());
        }
        if (!isQueueExist(basicPacket.getParseTaskQueueName())){
            createQueue(basicPacket.getDownloadTaskQueueName());
        }
        if (!isQueueExist(basicPacket.getUrlCleanTaskQueue())){
            createQueue(basicPacket.getUrlCleanTaskQueue());
        }
        if (!isQueueExist(basicPacket.getStorageTaskQueueName())){
            createQueue(basicPacket.getStorageTaskQueueName());
        }

        return this;
    }

    protected void checkCondition(BasicPacket basicPacket) {
        if (basicPacket == null) {
            throw new QueueRuntimeException("初始化队列失败，初始化所需信息为空！");
        }
        if (StringUtils.isEmpty(basicPacket.getDownloadTaskQueueName())) {
            throw new QueueRuntimeException("初始化队列失败，下载任务队列名称为空！");
        }
        if (StringUtils.isEmpty(basicPacket.getParseTaskQueueName())) {
            throw new QueueRuntimeException("初始化队列失败，网页解析任务队列为空！");
        }
        if (StringUtils.isEmpty(basicPacket.getUrlCleanTaskQueue())) {
            throw  new QueueRuntimeException("初始化队列失败，URL清洗任务队列为空！");
        }
        if (StringUtils.isEmpty(basicPacket.getStorageTaskQueueName())) {
            throw new QueueRuntimeException("初始化队列失败，存储任务队列为空！");
        }
    }


}
