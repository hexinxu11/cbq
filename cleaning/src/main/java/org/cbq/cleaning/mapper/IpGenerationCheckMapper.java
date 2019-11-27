package org.cbq.cleaning.mapper;

import org.cbq.cleaning.entity.IpGenerationCheck;
import org.cbq.cleaning.ienum.Generation;

import java.util.List;

public interface IpGenerationCheckMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IpGenerationCheck record);

    int insertSelective(IpGenerationCheck record);

    IpGenerationCheck selectByPrimaryKey(Integer id);

    public List<IpGenerationCheck> selectByGenerationList(Integer generation);

    int updateByPrimaryKeySelective(IpGenerationCheck record);

    int updateByPrimaryKey(IpGenerationCheck record);
}