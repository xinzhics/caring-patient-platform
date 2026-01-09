package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName DrugsOperateType
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 13:31
 * @Version 1.0
 */
public enum DrugsOperateType {

    /**
     * 新增用药
     */
    CREATED("CREATED","新增用药", 4),

    /**
     * 修改用药
     */
    UPDATED("UPDATED","修改用药", 3),

    /**
     * 停止用药
     */
    STOP("STOP","停止用药", 2),

    /**
     * 当前用药
     */
    CURRENT("CURRENT","当前用药", 1);

    @ApiModelProperty(value = "操作类型")
    public String operateType;

    @ApiModelProperty(value = "操作类型名称")
    public String operateTypeName;

    @ApiModelProperty(value = "操作类型排序")
    public Integer operateTypeSort;

    DrugsOperateType(String operateType, String operateTypeName, Integer operateTypeSort) {
        this.operateType = operateType;
        this.operateTypeName = operateTypeName;
        this.operateTypeSort = operateTypeSort;
    }
}
