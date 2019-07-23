package com.cbq.download.core;

import akka.actor.AbstractActor;

/**
 *
 * 下载任务调度器(监听消息中间件，获取下载任务，根据任务配置，启动下载任务处理器)
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 11:16
 * @Version 1.0
 **/
public class DownloadTaskDispatcher  extends AbstractActor {


    @Override
    public Receive createReceive() {
        return null;
    }
}
