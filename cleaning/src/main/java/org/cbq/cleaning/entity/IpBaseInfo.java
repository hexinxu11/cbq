package org.cbq.cleaning.entity;


import lombok.Getter;
import lombok.Setter;

/**
 * ip 基本信息
 * @author kok
 */
@Setter
@Getter
public class IpBaseInfo {
    private Integer id;

    private String ip;

    private Integer port;

    private Integer anonymous;

    private String ipOwnership;

    private String reqMethod;

    private Integer status;

}