package com.caring.sass.authority.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName Hospital
 * @Description
 * @Author yangShuai
 * @Date 2022/4/15 13:03
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("hospital")
@ApiModel(value = "Hospital", description = "医院表")
public class Hospital extends Entity<Long> {

    @ApiModelProperty("省ID")
    @TableField(value = "province_id", condition = EQUAL)
    private Long provinceId;

    @ApiModelProperty("市ID")
    @TableField(value = "city_id", condition = EQUAL)
    private Long cityId;

    @ApiModelProperty("区域ID")
    @TableField(value = "region_id", condition = EQUAL)
    private Long regionId;

    @ApiModelProperty("地址")
    @TableField(value = "address", condition = EQUAL)
    private String address;

    @ApiModelProperty("医院编码")
    @TableField(value = "hospital_code", condition = EQUAL)
    private String hospitalCode;

    @ApiModelProperty("医院名称")
    @TableField(value = "hospital_name", condition = LIKE)
    private String hospitalName;

    @ApiModelProperty("医院简称")
    @TableField(value = "hospital_abbreviation", condition = LIKE)
    private String hospitalAbbreviation;

    @ApiModelProperty("医院等级" +
            "一级医院" +
            "一级甲等" +
            "三级医院" +
            "三级甲等" +
            "二级医院" +
            "二级甲等" +
            "未定级")
    @TableField(value = "hospital_level", condition = EQUAL)
    private String hospitalLevel;

    @ApiModelProperty("医院属性(0-非公立 1-公立)")
    @TableField(value = "hospital_property", condition = EQUAL)
    private Integer hospitalProperty;

    @ApiModelProperty("联系电话")
    @TableField(value = "telephone", condition = EQUAL)
    private String telephone;

    @ApiModelProperty("简介")
    @TableField(value = "simple_introduce", condition = EQUAL)
    private String simpleIntroduce;


    @TableField(exist = false)
    private String provinceName;


    @TableField(exist = false)
    private String cityName;






}
