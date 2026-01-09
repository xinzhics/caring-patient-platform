package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "StatisticsTaskPageDTO", description = "统计任务")
@AllArgsConstructor
public class StatisticsTaskPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "统计名称")
    @Length(max = 50, message = "统计名称长度不能超过50")
    private String statisticsName;


}
