package org.cbq.cleaning.mapper;

import org.cbq.cleaning.entity.IpCheckRecord;

public interface IpCheckRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IpCheckRecord record);

    int insertSelective(IpCheckRecord record);

    IpCheckRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IpCheckRecord record);

    int updateByPrimaryKey(IpCheckRecord record);
}