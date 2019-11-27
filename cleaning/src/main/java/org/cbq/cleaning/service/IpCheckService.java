package org.cbq.cleaning.service;

import org.cbq.cleaning.entity.IpBaseInfo;
import org.cbq.cleaning.ienum.Generation;
import org.cbq.cleaning.vo.IpVisitVo;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/11 09:30
 * @Version 1.0
 **/
public interface IpCheckService {

    public IpVisitVo checkProxy(IpBaseInfo baseInfo, String targetAddress);

    public void generationCheckProxy(Generation generation);
    
}
