package com.caring.sass.nursing.dto.plan;

import java.time.LocalDateTime;
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
 * 护理计划-学习
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
@ApiModel(value = "PlanLearnDetailPageDTO", description = "护理计划-学习")
public class PlanLearnDetailPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学习计划ID
     */
    @ApiModelProperty(value = "学习计划ID")
    private Long learnPlanId;
    @ApiModelProperty(value = "")
    private Long nursingPlanDetailTimeId;
    /**
     * 0 未推送 1已推送
     */
    @ApiModelProperty(value = "0 未推送 1已推送")
    private Boolean status;
    /**
     * 学习计划顺序
     */
    @ApiModelProperty(value = "学习计划顺序")
    private Integer order;

}
