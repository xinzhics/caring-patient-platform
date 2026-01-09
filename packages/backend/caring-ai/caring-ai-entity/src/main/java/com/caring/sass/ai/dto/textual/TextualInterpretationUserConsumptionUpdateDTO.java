package com.caring.sass.ai.dto.textual;

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
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TextualInterpretationUserConsumptionUpdateDTO", description = "文献解读能量豆消费记录")
public class TextualInterpretationUserConsumptionUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 消费类型
     */
    @ApiModelProperty(value = "消费类型")
    @Length(max = 255, message = "消费类型长度不能超过255")
    private String consumptionType;
    /**
     * 消耗能量币数量
     */
    @ApiModelProperty(value = "消耗能量币数量")
    private Integer consumptionAmount;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 255, message = "备注长度不能超过255")
    private String messageRemark;
}
