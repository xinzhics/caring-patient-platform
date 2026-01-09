package com.caring.sass.wx.dto.enums;

/**
 * @Author yangShuai
 * @Description 微信标签
 * @Date 2020/9/16 9:32
 * @return
 */
public enum TagsEnum {

    /**
     * 医助
     */
    ASSISTANT_TAG("assistant_tag"),

    /**
     * 微信医生标签
     */
    DOCTOR_TAGS("doctor_tag"),

    /**
     * 微信会员标签
     */
    PATIENT_TAG("patient_tag"),

    /**
     * 游客标签
     */
    TOURISTS_TAG("tourists_tag"),

    /**
     * 给敏识用户打上sass用户标签
     */
    CARING_SASS_PATIENT("CARING_SASS_PATIENT"),
    /**
     * 没有标签
     */
    NO_TAG("no_tag");
    /**
     * 标签值
     */
    private String value;

    public String getValue() {
        return this.value;
    }

    TagsEnum(String value) {
        this.value = value;
    }
}
