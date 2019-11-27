package org.cbq.cleaning.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cbq.cleaning.entity.IpVisitRecord;

/**
 * @Description TODO
 * @Author kok
 * @Dte 2019/9/25 14:26
 * @Version 1.0
 **/
@Getter
@Setter
public class IpVisitVo extends IpVisitRecord {

    private long startTime;
    private long endTime;
    private boolean reqState;


}
