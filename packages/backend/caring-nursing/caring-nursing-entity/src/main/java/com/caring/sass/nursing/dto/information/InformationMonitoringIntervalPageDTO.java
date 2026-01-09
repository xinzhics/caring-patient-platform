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
@ApiModel(value = "InformationMonitoringIntervalPageDTO", description = "信息完整度区间设置")
public class InformationMonitoringIntervalPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 区间名称
     */
    @ApiModelProperty(value = "区间名称")
    @Length(max = 255, message = "区间名称长度不能超过255")
    private String name;
    /**
     * 最小值
     */
    @ApiModelProperty(value = "最小值")
    private Double minValue;
    /**
     * 最大值
     */
    @ApiModelProperty(value = "最大值")
    private Double maxValue;

}
