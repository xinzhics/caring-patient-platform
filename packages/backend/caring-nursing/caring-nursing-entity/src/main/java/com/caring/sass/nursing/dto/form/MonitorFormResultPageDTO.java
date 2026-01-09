package com.caring.sass.nursing.dto.form;

import com.caring.sass.nursing.constant.MonitorQueryDateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @ClassName MonitorFormResultPageDTO
 * @Description
 * @Author yangShuai
 * @Date 2022/2/16 15:13
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MonitorFormResultPageDTO", description = "表单填写结果表")
public class MonitorFormResultPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同")
    @Length(max = 32, message = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同长度不能超过32")
    private String businessId;

    /**
     * 填写人Id
     */
    @ApiModelProperty(value = "填写人Id")
    private Long userId;

    @ApiModelProperty(value = "时间范围")
    private MonitorQueryDateDTO monitorQueryDate;

    @ApiModelProperty(value = "单日曲线查询时间")
    private LocalDate oneDayDate;

    @ApiModelProperty(value = "自定义的开始时间")
    private LocalDate customizeStartDate;

    @ApiModelProperty(value = "自定义的结束时间")
    private LocalDate customizeEndDate;

    @ApiModelProperty(value = "必须返回字段")
    private Boolean needContent = false;
}
