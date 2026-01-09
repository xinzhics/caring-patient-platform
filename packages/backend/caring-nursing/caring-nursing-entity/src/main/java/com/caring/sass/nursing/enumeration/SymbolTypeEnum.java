package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName 符号   1-小于 2-小于等于 3-等于
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 13:31
 * @Version 1.0
 */
public enum SymbolTypeEnum {

    /**
     * 小于
     */
    LT("1","小于"),
    /**
     * 小于等于
     */
    LE("2","小于等于"),
    /**
     * 等于
     */
    EQ("3","等于");

    @ApiModelProperty(value = "类型")
    public String operateType;

    @ApiModelProperty(value = "类型名称")
    public String operateTypeName;

    SymbolTypeEnum(String operateType, String operateTypeName) {
        this.operateType = operateType;
        this.operateTypeName = operateTypeName;
    }
}
