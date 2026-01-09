package com.caring.sass.nursing.constant;

/**
 * 预约记录中预约状态的枚举
 * @author 杨帅
 */
public enum AppointmentSortEnum {

    /**
     * 未就诊
     */
    NO_VISIT(0),

    /**
     * 审核中
     */
    UNDER_REVIEW(1),

    /**
     * 预约失败
     */
    APPOINT_FAILED(2),

    /**
     * 已就诊
     */
    VISITED(3),

    /**
     * 已取消
     */
    CANCELED(4),

    /**
     * 就诊过期
     */
    EXPIRED(5);

    int code;

    public int getCode() {
        return code;
    }

    AppointmentSortEnum(int code) {
        this.code = code;
    }
}
