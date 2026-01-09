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
 * @ClassName City
 * @Description
 * @Author yangShuai
 * @Date 2022/4/15 13:22
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("city")
@ApiModel(value = "City", description = "市区表")
public class City extends Entity<Long> {

    @ApiModelProperty("市区名称")
    @TableField(value = "city", condition = LIKE)
    private String city;

    @ApiModelProperty("市区code")
    @TableField(value = "city_code", condition = EQUAL)
    private String cityCode;

    @ApiModelProperty("省ID")
    @TableField(value = "province_id", condition = EQUAL)
    private Long provinceId;

    @ApiModelProperty("省code")
    @TableField(value = "province_code", condition = EQUAL)
    private String provinceCode;

    @ApiModelProperty("省名称")
    @TableField(value = "province_name", condition = EQUAL)
    private String provinceName;


}
