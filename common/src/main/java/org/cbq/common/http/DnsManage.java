package org.cbq.common.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 定时更新DNS，
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/9 09:06
 * @Version 1.0
 **/
public class DnsManage {

    static Logger logger = LoggerFactory.getLogger(DnsManage.class);

    public static InetAddress[] getByAddress(final String host){
        try {
            return new InetAddress[]{InetAddress.getByAddress(new byte[]{127, 0, 0, 1})};
        } catch (UnknownHostException e) {
            logger.error("DNS parse error! ",e);
        }
        return null;
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress address3 = InetAddress.getByName("https://www.mzitu.com/tag/youhuo/");
        System.out.println(address3);
    }
}
