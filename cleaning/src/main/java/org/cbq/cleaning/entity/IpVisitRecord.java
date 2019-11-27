package org.cbq.cleaning.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 访问记录
 * @author kok
 */
@Setter
@Getter
public class IpVisitRecord {
    private Integer id;

    private Integer ipId;

    private String ip;

    private Date visitTime;

    private String targetAddress;

    private Integer speed;

}