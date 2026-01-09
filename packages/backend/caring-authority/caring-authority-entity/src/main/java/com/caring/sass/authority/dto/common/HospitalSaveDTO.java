package com.caring.sass.authority.dto.common;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 字典类型
 * </p>
 *
 * @author caring
 * @since 2020-01-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "HospitalSaveDTO", description = "医院")
public class HospitalSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("省ID")
    private Long provinceId;

    @ApiModelProperty("市ID")
    private Long cityId;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("医院编码")
    private String hospitalCode;

    @ApiModelProperty("医院名称")
    private String hospitalName;

    @ApiModelProperty("医院简称")
    private String hospitalAbbreviation;

    @ApiModelProperty("医院等级" +
            "一级医院" +
            "三级医院" +
            "三级甲等" +
            "二级医院" +
            "二级甲等" +
            "未定级")
    @TableField(value = "hospital_level", condition = EQUAL)
    private String hospitalLevel;

    @ApiModelProperty("医院属性(0-非公立 1-公立)")
    private Integer hospitalProperty;

    @ApiModelProperty("联系电话")
    private String telephone;

    @ApiModelProperty("简介")
    private String simpleIntroduce;

}
