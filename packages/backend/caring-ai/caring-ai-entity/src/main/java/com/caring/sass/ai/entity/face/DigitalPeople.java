package com.caring.sass.ai.entity.face;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 数字人
 * </p>
 *
 * @author 杨帅
 * @since 2024-06-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_megvii_digital_people")
@ApiModel(value = "DigitalPeople", description = "数字人")
@AllArgsConstructor
public class DigitalPeople extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 50, message = "姓名长度不能超过50")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "姓名")
    private String name;

    @ApiModelProperty(value = "用户")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;

    /**
     * 图片url
     */
    @ApiModelProperty(value = "图片url")
    @Length(max = 255, message = "图片url长度不能超过255")
    @TableField(value = "image_url", condition = LIKE)
    @Excel(name = "图片url")
    private String imageUrl;


}
