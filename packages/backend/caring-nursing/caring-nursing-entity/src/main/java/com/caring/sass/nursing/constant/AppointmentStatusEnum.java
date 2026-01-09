package com.caring.sass.nursing.constant;

/**
 * 预约记录中预约状态的枚举
 * @author 杨帅
 */
public enum AppointmentStatusEnum {

    /**
     * 未就诊
     */
    NO_VISIT(0),

    /**
     * 已就诊
     */
    VISITED(1),

    /**
     * 取消就诊
     */
    CANCEL_VISIT(2),

    /**
     * 审核中
     */
    UNDER_REVIEW(-2),

    /**
     * (预约失败)审核失败
     */
    AUDIT_FAILED(3),

    /**
     * 就诊过期
     */
    VISIT_EXPIRED(4);

    int code;

    public int getCode() {
        return code;
    }

    AppointmentStatusEnum(int code) {
        this.code = code;
    }
}
