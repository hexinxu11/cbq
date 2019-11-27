package org.cbq.common.packet;

import lombok.Getter;
import lombok.Setter;

/**
 * url清洗任务
 *
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 17:46
 * @Version 1.0
 **/
@Setter
@Getter
public class UrlCleanTaskPacket extends BasicPacket {

    private String url;

}
