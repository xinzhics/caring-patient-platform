package com.caring.sass.nursing.dto.information;

import com.caring.sass.base.entity.SuperEntity;
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
 * 信息完整度区间统计详细表
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
@ApiModel(value = "CompletionInformationStatisticsDetailUpdateDTO", description = "信息完整度区间统计详细表")
public class CompletionInformationStatisticsDetailUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 统计ID
     */
    @ApiModelProperty(value = "统计ID")
    private Long statisticsId;
    /**
     * 区间ID
     */
    @ApiModelProperty(value = "区间ID")
    private Long intervalId;
    /**
     * 区间名称
     */
    @ApiModelProperty(value = "区间名称")
    @Length(max = 20, message = "区间名称长度不能超过20")
    private String intervalName;
    /**
     * 区间最小值
     */
    @ApiModelProperty(value = "区间最小值")
    private Double intervalMinValue;
    /**
     * 区间最大值
     */
    @ApiModelProperty(value = "区间最大值")
    private Double intervalMaxValue;
    /**
     * 区间占比
     */
    @ApiModelProperty(value = "区间占比")
    private Integer intervalProportion;
    /**
     * 患者人数
     */
    @ApiModelProperty(value = "患者人数")
    private Integer patientNumber;
}
