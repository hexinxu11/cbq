package org.cbq.common.http;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/7/9 14:48
 * @Version 1.0
 **/
public class CbqDnsResolver implements DnsResolver {

    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        if (host.equalsIgnoreCase("myhost")) {
            return DnsManage.getByAddress(host);
        } else {
            return InetAddress.getAllByName(host);

        }
    }
}
