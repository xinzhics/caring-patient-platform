package com.caring.sass.nursing.dto.drugs;

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
 * 患者管理-用药预警-预警条件表
 * </p>
 *
 * @author yangshuai
 * @since 2022-06-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DrugsConditionMonitoringUpdateDTO", description = "患者管理-用药预警-预警条件表")
public class DrugsConditionMonitoringUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 药品ID
     */
    @ApiModelProperty(value = "药品ID")
    private Long drugsId;
    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    private String drugsName;
    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @Length(max = 2000, message = "规格长度不能超过2000")
    private String spec;
    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商")
    @Length(max = 100, message = "厂商长度不能超过100")
    private String manufactor;
    /**
     * 提醒时间（余药不足前X天）
     */
    @ApiModelProperty(value = "提醒时间（余药不足前X天）")
    private Long reminderTime;
    /**
     * 购药地址
     */
    @ApiModelProperty(value = "购药地址")
    @Length(max = 1000, message = "购药地址长度不能超过1000")
    private String buyingMedicineUrl;
    /**
     * 模板消息ID
     */
    @ApiModelProperty(value = "模板消息ID")
    private Long templateMsgId;
}
