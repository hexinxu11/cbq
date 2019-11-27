package org.cbq.cleaning.mapper;

import org.cbq.cleaning.entity.IpBaseInfo;

public interface IpBaseInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IpBaseInfo record);

    int insertSelective(IpBaseInfo record);

    IpBaseInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IpBaseInfo record);

    int updateByPrimaryKey(IpBaseInfo record);
}