package org.cbq.cleaning.service.impl;


import com.google.common.collect.Lists;
import org.cbq.cleaning.entity.IpBaseInfo;
import org.cbq.cleaning.ienum.Generation;
import org.cbq.cleaning.service.IpCheckService;
import org.cbq.cleaning.vo.IpVisitVo;

import java.util.ArrayList;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/11 09:31
 * @Version 1.0
 **/
public class IpCheckDefaultService implements IpCheckService {



    @Override
    public IpVisitVo checkProxy(IpBaseInfo baseInfo, String targetAddress) {
        var list = Lists.newArrayList();
        return null;
    }

    @Override
    public void generationCheckProxy(Generation generation) {

    }
}
