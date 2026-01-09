package com.caring.sass.nursing.dto.unfinished;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 未完成推送的患者记录
 * </p>
 *
 * @author 杨帅
 * @since 2024-05-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "UnfinishedPatientRecordPageDTO", description = "未完成推送的患者记录")
public class UnfinishedPatientRecordPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;
    /**
     * 患者所属医助ID
     */
    @ApiModelProperty(value = "患者所属医助ID")
    private Long nursingId;
    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    @NotNull(message = "患者所属医生ID不能为空")
    private Long doctorId;
    /**
     */
    @ApiModelProperty(value = "查看状态 (0 未处理， 1 已处理)")
    private Integer seeStatus;
    /**
     */
    @ApiModelProperty(value = "处理状态 (0 未处理， 1 已处理)")
    private Integer handleStatus;
    /**
     */
    @ApiModelProperty(value = "清理状态 (0 未清理， 1 已清理)")
    private Integer clearStatus;
    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    private LocalDateTime handleTime;
    /**
     * 处理人id
     */
    @ApiModelProperty(value = "处理人id")
    private Long handleUser;
    /**
     * 未完成表单设置ID
     */
    @ApiModelProperty(value = "未完成表单设置ID")
    private Long unfinishedFormSettingId;
    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    private Long formId;
    /**
     * 1表单，2用药日历
     */
    @ApiModelProperty(value = "1表单，2用药日历")
    private Integer medicineOrForm;
    /**
     * 推送提醒的消息ID
     */
    @ApiModelProperty(value = "推送提醒的消息ID")
    private Long remindMessageId;
    /**
     * 推送提醒的时间
     */
    @ApiModelProperty(value = "推送提醒的时间")
    private LocalDateTime remindTime;

}
