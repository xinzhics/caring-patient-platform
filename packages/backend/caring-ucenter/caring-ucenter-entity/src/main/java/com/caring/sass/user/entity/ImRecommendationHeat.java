package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * im推荐功能热度
 * </p>
 *
 * @author 杨帅
 * @since 2024-01-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_im_recommendation_heat")
@ApiModel(value = "ImRecommendationHeat", description = "im推荐功能热度")
@AllArgsConstructor
public class ImRecommendationHeat extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 功能ID
     */
    @ApiModelProperty(value = "功能ID")
    @TableField("function_id")
    @Excel(name = "功能ID")
    private Long functionId;

    /**
     * 功能热度
     */
    @ApiModelProperty(value = "功能热度")
    @TableField("function_heat")
    @Excel(name = "功能热度")
    private Integer functionHeat;

    /**
     * 功能类型
     */
    @ApiModelProperty(value = "功能类型")
    @Length(max = 50, message = "功能类型长度不能超过50")
    @TableField(value = "function_type", condition = LIKE)
    @Excel(name = "功能类型")
    private String functionType;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型")
    @Length(max = 20, message = "用户类型长度不能超过20")
    @TableField(value = "user_type", condition = LIKE)
    @Excel(name = "用户类型")
    private String userType;


}
