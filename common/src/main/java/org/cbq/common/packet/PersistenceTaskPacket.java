package org.cbq.common.packet;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 持久化任务
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 17:47
 * @Version 1.0
 **/
@Setter
@Getter
public class PersistenceTaskPacket extends BasicPacket {

    private Map<String,Object> parseResult;
    private String content;
    private String url;
}
