package com.caring.sass.ai.entity.textual;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 文献解读能量豆消费记录
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_textual_interpretation_user_consumption")
@ApiModel(value = "TextualInterpretationUserConsumption", description = "文献解读能量豆消费记录")
@AllArgsConstructor
public class TextualInterpretationUserConsumption extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 消费类型
     */
    @ApiModelProperty(value = "消费类型")
    @Length(max = 255, message = "消费类型长度不能超过255")
    @TableField(value = "consumption_type", condition = LIKE)
    @Excel(name = "消费类型")
    private TextualConsumptionType consumptionType;

    /**
     * 消耗能量币数量
     */
    @ApiModelProperty(value = "消耗能量币数量")
    @TableField("consumption_amount")
    @Excel(name = "消耗能量币数量")
    private Integer consumptionAmount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 255, message = "备注长度不能超过255")
    @TableField(value = "message_remark", condition = LIKE)
    @Excel(name = "备注")
    private String messageRemark;




}
