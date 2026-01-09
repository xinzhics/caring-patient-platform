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
 * 地区表
 * </p>
 *
 * @author caring
 * @since 2020-02-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "HospitalPageDTO", description = "医院")
public class HospitalPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("省ID")
    private Long provinceId;

    @ApiModelProperty("市ID")
    private Long cityId;

    @ApiModelProperty("医院名称")
    private String hospitalName;


    @ApiModelProperty("医院等级" +
            "一级医院" +
            "三级医院" +
            "三级甲等" +
            "二级医院" +
            "二级甲等" +
            "未定级")
    @TableField(value = "hospital_level", condition = EQUAL)
    private String hospitalLevel;


}
