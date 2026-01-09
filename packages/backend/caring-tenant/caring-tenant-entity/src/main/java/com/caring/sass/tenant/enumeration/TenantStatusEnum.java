package com.caring.sass.tenant.enumeration;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * <p>
 * 实体注释中生成的类型枚举
 * 企业
 * </p>
 *
 * @author caring
 * @date 2019-10-25
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "TenantStatusEnum", description = "状态-枚举")
public enum TenantStatusEnum implements BaseEnum {

    /**
     * NORMAL="正常"
     */
    NORMAL("正常", 1),
    /**
     * WAIT_INIT="待初始化"
     */
    WAIT_INIT("部署中", 3),
    /**
     * FORBIDDEN="禁用"
     */
    FORBIDDEN("禁用", 3),
    /**
     * WAITING="待审核"
     */
    WAITING("待审核",0),
    /**
     * REFUSE="拒绝"
     */
    REFUSE("拒绝", 0),
    DELETING("正在删除", 0),
    ;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "排序")
    private int sort;

    public static TenantStatusEnum match(String val, TenantStatusEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static TenantStatusEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(TenantStatusEnum val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "NORMAL,FORBIDDEN,WAITING,REFUSE", example = "NORMAL")
    public String getCode() {
        return this.name();
    }

}
