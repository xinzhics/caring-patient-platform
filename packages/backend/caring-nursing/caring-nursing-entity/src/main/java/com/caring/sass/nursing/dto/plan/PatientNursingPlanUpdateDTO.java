package com.caring.sass.nursing.dto.plan;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
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
 * 会员订阅护理计划
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
@ApiModel(value = "PatientNursingPlanUpdateDTO", description = "会员订阅护理计划")
public class PatientNursingPlanUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 病人ID
     */
    @ApiModelProperty(value = "病人ID")
    private Long patientId;
    /**
     * 护理计划ID
     */
    @ApiModelProperty(value = "护理计划ID")
    private Long nursingPlantId;
    /**
     * 护理开始时间
     */
    @ApiModelProperty(value = "护理开始时间")
    private LocalDate startDate;
    /**
     * 订阅状态(1：订阅   0：未订阅)
     */
    @ApiModelProperty(value = "订阅状态(1：订阅   0：未订阅)")
    private Integer isSubscribe;
}
