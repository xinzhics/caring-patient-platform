package com.caring.sass.nursing.dto.form;

import com.caring.sass.nursing.constant.MonitorQueryDateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 血糖表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SugarFormResultPageDTO", description = "血糖表")
public class SugarFormResultPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    @Length(max = 32, message = "会员ID长度不能超过32")
    private Long patientId;

    /**
     * 类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)
     */
    @ApiModelProperty(value = "类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)")
    private Integer type;

    private MonitorQueryDateDTO monitorQueryDate;

}
