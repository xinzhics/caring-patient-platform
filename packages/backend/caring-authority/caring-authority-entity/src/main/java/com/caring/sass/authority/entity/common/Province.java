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
 * @ClassName Province
 * @Description
 * @Author yangShuai
 * @Date 2022/4/15 13:27
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("province")
@ApiModel(value = "Province", description = "省")
public class Province extends Entity<Long> {

    @ApiModelProperty("省的code")
    @TableField(value = "province_code", condition = EQUAL)
    private String provinceCode;

    @ApiModelProperty("省名称")
    @TableField(value = "province", condition = LIKE)
    private String province;


}
