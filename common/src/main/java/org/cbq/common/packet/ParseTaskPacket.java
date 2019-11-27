package org.cbq.common.packet;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 网页解析任务
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 17:46
 * @Version 1.0
 **/
@Getter
@Setter
public class ParseTaskPacket extends BasicPacket {

    private String content;
    private String url;
}
