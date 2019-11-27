package org.cbq.cleaning.service;

import org.cbq.cleaning.entity.IpGenerationCheck;
import org.cbq.cleaning.ienum.Generation;
import org.cbq.cleaning.mapper.IpGenerationCheckMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/11 09:44
 * @Version 1.0
 **/
@Service
public class IpGenerationService {

    @Resource
    IpGenerationCheckMapper ipGenerationCheckMapper;

    public void addIpGenerationCheck(IpGenerationCheck generationCheck) {
        ipGenerationCheckMapper.insertSelective(generationCheck);
    }

    public List<IpGenerationCheck> findByGenerationList(Generation generation) {

        return ipGenerationCheckMapper.selectByGenerationList(generation.getValue());
    }

    public void modifyIpGenerationCheck(IpGenerationCheck generationCheck) {
        ipGenerationCheckMapper.updateByPrimaryKeySelective(generationCheck);
    }
}
