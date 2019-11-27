package org.cbq.common.packet;

import lombok.Getter;
import lombok.Setter;

/**
 * 下载任务
 *
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/1 17:44
 * @Version 1.0
 **/
@Setter
@Getter
public class DownloadTaskPacket extends BasicPacket {

    private String url;
}
