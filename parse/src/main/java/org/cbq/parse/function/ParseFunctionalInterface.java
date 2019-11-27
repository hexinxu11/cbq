package org.cbq.parse.function;

import org.cbq.common.packet.ParseTaskPacket;
import org.cbq.common.packet.PersistenceTaskPacket;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/17 11:29
 * @Version 1.0
 **/
@FunctionalInterface
public interface ParseFunctionalInterface {

    public PersistenceTaskPacket apply(ParseTaskPacket taskPacket);
}
