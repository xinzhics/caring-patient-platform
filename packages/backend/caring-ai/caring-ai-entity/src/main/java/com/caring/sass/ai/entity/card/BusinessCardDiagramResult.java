package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.constant.GenderType;
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
 * 医生名片头像合成结果
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-10
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_diagram_result")
@ApiModel(value = "BusinessCardDiagramResult", description = "医生名片头像合成结果")
@AllArgsConstructor
public class BusinessCardDiagramResult extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    @TableField("order_")
    @Excel(name = "优先级")
    private Integer order;

    @TableField(value = "gender", condition = EQUAL)
    private GenderType gender;

    /**
     * 图片的obs地址
     */
    @ApiModelProperty(value = "图片的obs地址")
    @Length(max = 350, message = "图片的obs地址长度不能超过350")
    @TableField(value = "image_obs_url", condition = LIKE)
    @Excel(name = "图片的obs地址")
    private String imageObsUrl;


    @ApiModelProperty(value = "是否过期")
    @TableField(value = "history_", condition = EQUAL)
    private Boolean history_;


    @ApiModelProperty(value = "被使用中")
    @TableField(value = "useding", condition = EQUAL)
    private Boolean useding;


    @ApiModelProperty(value = "任务ID")
    @TableField(value = "task_id", condition = EQUAL)
    private Long taskId;


    @ApiModelProperty(value = "是否原图 0 不是， 1 是")
    @TableField(value = "original_drawing", condition = EQUAL)
    private Integer originalDrawing;


}
