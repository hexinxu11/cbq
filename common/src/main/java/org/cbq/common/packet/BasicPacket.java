package org.cbq.common.packet;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 11:29
 * @Version 1.0
 **/
public class BasicPacket {

    /** URL域 **/
    private String domain;

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPageParse() {
        return pageParse;
    }

    public void setPageParse(String pageParse) {
        this.pageParse = pageParse;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getDownloadTaskQueueName() {
        return downloadTaskQueueName;
    }

    public void setDownloadTaskQueueName(String downloadTaskQueueName) {
        this.downloadTaskQueueName = downloadTaskQueueName;
    }

    public String getParseTaskQueueName() {
        return parseTaskQueueName;
    }

    public void setParseTaskQueueName(String parseTaskQueueName) {
        this.parseTaskQueueName = parseTaskQueueName;
    }

    public String getStorageTaskQueueName() {
        return storageTaskQueueName;
    }

    public void setStorageTaskQueueName(String storageTaskQueueName) {
        this.storageTaskQueueName = storageTaskQueueName;
    }

    public String getUrlCleanTaskQueue() {
        return urlCleanTaskQueue;
    }

    public void setUrlCleanTaskQueue(String urlCleanTaskQueue) {
        this.urlCleanTaskQueue = urlCleanTaskQueue;
    }
}
