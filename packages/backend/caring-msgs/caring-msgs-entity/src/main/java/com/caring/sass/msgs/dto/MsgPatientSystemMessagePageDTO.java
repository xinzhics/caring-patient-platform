package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者系统消息
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MsgPatientSystemMessagePageDTO", description = "患者系统消息")
public class MsgPatientSystemMessagePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)
     */
    @ApiModelProperty(value = "功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访, 互动消息)")
    @Length(max = 100, message = "功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)长度不能超过255")
    private String functionType;

    /**
     * 接收患者ID
     */
    @ApiModelProperty(value = "接收患者ID")
    @NotNull(message = "接收患者ID不能为空")
    private Long patientId;


}
