package com.caring.sass.nursing.dto.information;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 信息完整度监控指标
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "InformationIntegrityMonitoringTaskDTO", description = "信息完整度监控指标")
public class InformationIntegrityMonitoringTaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    private String tenantCode;

}
