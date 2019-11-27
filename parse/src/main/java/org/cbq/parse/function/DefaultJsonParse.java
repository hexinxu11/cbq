package org.cbq.parse.function;

import org.apache.commons.lang3.StringUtils;
import org.cbq.common.packet.ParseTaskPacket;
import org.cbq.common.packet.PersistenceTaskPacket;
import org.cbq.common.utils.ConvertUtil;


/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/10/12 10:00
 * @Version 1.0
 **/
public class DefaultJsonParse implements  JsonParse {

    @Override
    public PersistenceTaskPacket parse(ParseTaskPacket taskPacket) {

        return parse(taskPacket,(func -> ConvertUtil.convert(taskPacket,PersistenceTaskPacket.class)));
    }

    @Override
    public PersistenceTaskPacket parse(ParseTaskPacket taskPacket, ParseFunctionalInterface functionalInterface) {
        return functionalInterface.apply(taskPacket);
    }
}
