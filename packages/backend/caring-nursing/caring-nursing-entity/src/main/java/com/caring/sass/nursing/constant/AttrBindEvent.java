package com.caring.sass.nursing.constant;

/**
 * @ClassName AttrBindEvent
 * @Description
 * @Author yangShuai
 * @Date 2022/4/24 13:40
 * @Version 1.0
 */
public enum AttrBindEvent {

    /**
     * 标签修改
     */
    ATTR_CHANGE("ATTR_CHANGE", "标签变化"),

    /**
     * 基本信息
     */
    BASE_INFO("base_info", "基本信息"),

    /**
     * 疾病信息
     */
    HEALTH_RECORD("HEALTH_RECORD", "疾病信息"),

    /**
     * 监测计划
     */
    MONITORING_PLAN("MONITORING_PLAN", "监测计划"),

    /**
     * 新增药品
     */
    ADD_DRUG("ADD_DRUG", "新增药品"),

    /**
     * 手动停止药品
     */
    STOP_DRUG("STOP_DRUG", "停止药品");


    /**
     * 事件的code
     */
    private String code;

    /**
     * 事件的名称
     */
    private String name;

    AttrBindEvent(String code, String name) {
        this.code = code;
        this.name = name;
    }


}
