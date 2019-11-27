package org.cbq.cleaning.handler;

import org.cbq.cleaning.entity.IpBaseInfo;
import org.cbq.cleaning.util.ProxyIpCheckUtil;
import org.cbq.cleaning.vo.IpVisitVo;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/10 16:42
 * @Version 1.0
 **/
public class HttpClientProxyCheck extends AbstractProxyCheck {

    @Override
    public IpVisitVo checkProxy(IpBaseInfo baseInfo, String targetAddress) {

        IpVisitVo visitVo = builder(baseInfo.getId(),targetAddress);
        if (ProxyIpCheckUtil.checkProxy(baseInfo.getIp(),baseInfo.getPort(),baseInfo.getReqMethod(),targetAddress)){
            visitVo.setEndTime(System.currentTimeMillis());
            visitVo.setReqState(true);
        }
        return visitVo;
    }




}
