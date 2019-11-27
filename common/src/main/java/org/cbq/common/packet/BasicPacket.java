package org.cbq.common.packet;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 11:29
 * @Version 1.0
 **/
@Getter
@Setter
public class BasicPacket {

    /** URL域 **/
    private String domain;

    private String taskId;

    /** 网页解析器，可以是已有的java解析器，也可以是groovy脚本 **/
    private String pageParse;

    /** 存储器，只能使用已有的java存储器 **/
    private String storage;

    /** 下载任务队列 **/
    private String downloadTaskQueueName;

    /** 解析任务队列 **/
    private String parseTaskQueueName;

    /** 存储任务队列 **/
    private String storageTaskQueueName;

    /** url清洗任务队列 **/
    private String urlCleanTaskQueue;

}
