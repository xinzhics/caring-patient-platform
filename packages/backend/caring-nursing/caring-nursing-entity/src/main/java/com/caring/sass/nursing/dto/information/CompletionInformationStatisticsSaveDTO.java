package com.caring.sass.nursing.dto.information;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 信息完整度统计
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
@ApiModel(value = "CompletionInformationStatisticsSaveDTO", description = "信息完整度统计")
public class CompletionInformationStatisticsSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    private Long nursingId;
    /**
     * 患者总数
     */
    @ApiModelProperty(value = "患者总数")
    private Integer patientTotal;
    /**
     * 记录日期
     */
    @ApiModelProperty(value = "记录日期")
    private LocalDateTime statisticsDate;

}
