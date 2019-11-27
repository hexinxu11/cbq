package org.cbq.cleaning.mapper;

import org.cbq.cleaning.entity.IpVisitRecord;

public interface IpVisitRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IpVisitRecord record);

    int insertSelective(IpVisitRecord record);

    IpVisitRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IpVisitRecord record);

    int updateByPrimaryKey(IpVisitRecord record);
}