package org.cbq.parse.function;

import org.cbq.common.packet.ParseTaskPacket;
import org.cbq.common.packet.PersistenceTaskPacket;

/**
 * @author kok
 */
public interface Parse {

    public PersistenceTaskPacket parse(ParseTaskPacket taskPacket);

    public PersistenceTaskPacket parse(ParseTaskPacket taskPacket,ParseFunctionalInterface functionalInterface);

}
