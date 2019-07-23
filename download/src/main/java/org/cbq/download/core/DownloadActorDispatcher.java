package com.cbq.download.core;

import akka.actor.ActorSystem;

/**
 *
 * download系统调度器
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 11:13
 * @Version 1.0
 **/
public class DownloadActorDispatcher {

    final static String DOWNLOAD_ACTOR_SYSTEM = "DOWNLOAD_ACTOR_SYSTEM";

    final static ActorSystem system = ActorSystem.create(DOWNLOAD_ACTOR_SYSTEM);
}
