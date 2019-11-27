package org.cbq.common.queue;

import org.cbq.common.packet.*;

import java.util.concurrent.TimeUnit;

/**
 * @author kok
 */
public interface BasicQueue {

    public BasicQueue initQueue(BasicPacket basicPacket);

    public boolean offerDownloadTaskPacket(DownloadTaskPacket downloadTaskPacket);

    public boolean offerHtmlParseTaskPacket(ParseTaskPacket htmlParseTaskPacket);

    public boolean offerUrlCleanTaskPacket(UrlCleanTaskPacket urlCleanTaskPacket);

    public boolean offerPersistenceTaskPacket(PersistenceTaskPacket persistenceTaskPacket);

    public boolean offerDownloadTaskPacket(DownloadTaskPacket downloadTaskPacket, long timeout, TimeUnit unit);

    public boolean offerHtmlParseTaskPacket(ParseTaskPacket htmlParseTaskPacket, long timeout, TimeUnit unit);

    public boolean offerUrlCleanTaskPacket(UrlCleanTaskPacket urlCleanTaskPacket, long timeout, TimeUnit unit);

    public boolean offerPersistenceTaskPacket(PersistenceTaskPacket persistenceTaskPacket, long timeout, TimeUnit unit);

    public <T> T pollDownloadTaskPacket(String queueName,Class<T> clzz);

    public <T> T pollHtmlParseTaskPacket(String queueName,Class<T> clzz);

    public <T> T pollUrlCleanTaskPacket(String queueName,Class<T> clzz);

    public <T> T pollPersistenceTaskPacket(String queueName,Class<T> clzz);

    public <T> T pollDownloadTaskPacket(String queueName,Class<T> clzz,long timeout, TimeUnit unit);

    public <T> T pollHtmlParseTaskPacket(String queueName,Class<T> clzz,long timeout, TimeUnit unit);

    public <T> T pollUrlCleanTaskPacket(String queueName,Class<T> clzz,long timeout, TimeUnit unit);

    public <T> T pollPersistenceTaskPacket(String queueName,Class<T> clzz,long timeout, TimeUnit unit);

}
