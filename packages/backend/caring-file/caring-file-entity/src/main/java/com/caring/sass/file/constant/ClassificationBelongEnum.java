package com.caring.sass.file.constant;

public enum ClassificationBelongEnum {

    /**
     * 我的图片
     */
    MY_IMAGE("MY_IMAGE"),

    /**
     * 公共图片
     */
    PUBLIC_IMAGE("PUBLIC_IMAGE");

    private String value;

    public String getValue() {
        return this.value;
    }

    ClassificationBelongEnum(String value) {
        this.value = value;
    }
}
