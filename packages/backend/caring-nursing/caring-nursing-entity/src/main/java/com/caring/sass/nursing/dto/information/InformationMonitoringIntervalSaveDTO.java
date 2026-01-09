package com.caring.sass.nursing.dto.information;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 信息完整度区间设置
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
@ApiModel(value = "InformationMonitoringIntervalSaveDTO", description = "信息完整度区间设置")
public class InformationMonitoringIntervalSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "信息完整度区间设置ID 更新时候传入")
    private Long id;
    /**
     * 区间名称
     */
    @ApiModelProperty(value = "区间名称",required = true)
    @Length(max = 255, message = "区间名称长度不能超过255")
    private String name;
    /**
     * 最小值
     */
    @ApiModelProperty(value = "最小值",required = true)
    private Double minValue;
    /**
     * 最大值
     */
    @ApiModelProperty(value = "最大值",required = true)
    private Double maxValue;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    private String tenantCode;
}
