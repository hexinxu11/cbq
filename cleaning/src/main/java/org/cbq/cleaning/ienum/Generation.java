package org.cbq.cleaning.ienum;

/**分代枚举
 * @author kok
 */

public enum Generation {

    /**
     * 1天
     */
    ONE_DAY(1),

    /**
     * 3天
     */
    THREE_DAY(3),

    /**
     * 7天
     */
    SEVEN_DAY(7),

    /**
     * 15天
     */
    FIFTEEN_DAY(15),

    /**
     * 30天
     */
    THIRTY_DAY(30),

    /**
     * 正常ip
     */
    NORMAL(0)
    ;
    private int value;

    Generation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
