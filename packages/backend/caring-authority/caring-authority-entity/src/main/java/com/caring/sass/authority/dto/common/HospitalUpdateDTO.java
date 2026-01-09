package com.caring.sass.authority.dto.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 字典项
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
@ApiModel(value = "HospitalUpdateDTO", description = "医院修改")
public class HospitalUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
