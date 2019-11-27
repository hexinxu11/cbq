package org.cbq.cleaning.handler;

import org.cbq.cleaning.entity.IpBaseInfo;
import org.cbq.cleaning.vo.IpVisitVo;

/**
 * @author kok
 */
public interface ProxyCheck {


    public IpVisitVo checkProxy(IpBaseInfo baseInfo, String targetAddress);

}
