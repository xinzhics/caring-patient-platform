package com.caring.sass.nursing.dto.information;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "CompletionInformationStatisticsUpdateDTO", description = "信息完整度统计")
public class CompletionInformationStatisticsUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
