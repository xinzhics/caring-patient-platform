package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * AI医生名片工作室
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
@TableName("m_ai_business_card_studio")
@ApiModel(value = "BusinessCardStudio", description = "AI医生名片工作室")
@AllArgsConstructor
public class BusinessCardStudio extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医生名片ID
     */
    @ApiModelProperty(value = "医生名片ID")
    @NotNull(message = "医生名片ID不能为空")
    @TableField("business_card")
    @Excel(name = "医生名片ID")
    private Long businessCard;

    /**
     * 工作室
     */
    @ApiModelProperty(value = "工作室")
    @Length(max = 300, message = "工作室长度不能超过300")
    @TableField(value = "doctor_studio", condition = LIKE)
    @Excel(name = "工作室")
    private String doctorStudio;


    @ApiModelProperty(value = "工作室说明")
    @Length(max = 300, message = "工作室说明长度不能超过300")
    @TableField(value = "studio_title", condition = LIKE)
    private String studioTitle;


}
