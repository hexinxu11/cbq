package org.cbq.cleaning.handler;

import org.cbq.cleaning.vo.IpVisitVo;

import java.util.Date;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/10 16:40
 * @Version 1.0
 **/
public abstract class AbstractProxyCheck implements ProxyCheck {

    public IpVisitVo builder(Integer id, String targetAddress) {
        IpVisitVo visitVo = new IpVisitVo();
        visitVo.setIpId(id);
        visitVo.setTargetAddress(targetAddress);
        visitVo.setVisitTime(new Date());
        visitVo.setStartTime(System.currentTimeMillis());
        visitVo.setReqState(false);
        return visitVo;
    }


}
