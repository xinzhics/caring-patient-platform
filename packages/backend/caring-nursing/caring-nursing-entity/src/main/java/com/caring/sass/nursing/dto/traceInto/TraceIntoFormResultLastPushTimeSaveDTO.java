package com.caring.sass.nursing.dto.traceInto;

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
 * 选项跟踪患者最新上传时间记录表
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TraceIntoFormResultLastPushTimeSaveDTO", description = "选项跟踪患者最新上传时间记录表")
public class TraceIntoFormResultLastPushTimeSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "随访计划ID")
    @NotNull(message = "随访计划ID不能为空")
    private Long planId;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @NotNull(message = "医助ID不能为空")
    private Long nursingId;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @NotNull(message = "表单ID不能为空")
    private Long formId;
    /**
     * 表单结果ID
     */
    @ApiModelProperty(value = "表单结果ID")
    @NotNull(message = "表单结果ID不能为空")
    private Long formResultId;
    /**
     * 表单结果的上传时间
     */
    @ApiModelProperty(value = "表单结果的上传时间")
    @NotNull(message = "表单结果的上传时间不能为空")
    private LocalDateTime pushTime;
    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    @NotNull(message = "处理时间不能为空")
    private LocalDateTime handleTime;
    /**
     * 处理状态（0 未处理， 1 已处理）
     */
    @ApiModelProperty(value = "处理状态（0 未处理， 1 已处理）")
    @NotNull(message = "处理状态（0 未处理， 1 已处理）不能为空")
    private Integer handleStatus;
    /**
     * 清空状态（0 未处理， 1 已处理）
     */
    @ApiModelProperty(value = "清空状态（0 未处理， 1 已处理）")
    @NotNull(message = "清空状态（0 未处理， 1 已处理）不能为空")
    private Integer clearStatus;

}
