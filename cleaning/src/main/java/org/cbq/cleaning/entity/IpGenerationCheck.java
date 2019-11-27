package org.cbq.cleaning.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * ip分代检测
 * @author kok
 */
@Setter
@Getter
public class IpGenerationCheck {
    private Integer id;

    private Integer ipId;

    private Integer generation;

    private String targetAddress;

    private Date lastCheckTime;

    private Date nextCheckTime;

}