package com.caring.sass.nursing.dto.plan;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
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
 * 护理计划详情
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PlanDetailTimePageDTO", description = "护理计划详情")
public class PlanDetailTimePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 推送频率(0:单次 )
     */
    @ApiModelProperty(value = "推送频率(0:单次 )")
    private Integer frequency;
    /**
     * 推送时间
     */
    @ApiModelProperty(value = "推送时间")
    private LocalTime time;
    /**
     * 护理计划详情ID
     */
    @ApiModelProperty(value = "护理计划详情ID")
    private Long nursingPlanDetailId;
    /**
     * 触发前？触发后？几天？
     */
    @ApiModelProperty(value = "触发前？触发后？几天？")
    private Integer preTime;
    /**
     * 护理计划推送 指定 使用的模板
     */
    @ApiModelProperty(value = "护理计划推送 指定 使用的模板")
    @Length(max = 32, message = "护理计划推送 指定 使用的模板长度不能超过32")
    private String templateMessageId;

}
