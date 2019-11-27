package org.cbq.cleaning.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * ip检测记录
 * @author kok
 */
@Setter
@Getter
public class IpCheckRecord {
    private Integer id;

    private Integer ipId;

    private Date survivalTime;

    private Date lastCheckTime;

    private String targetAddress;

    private Integer speed;

}