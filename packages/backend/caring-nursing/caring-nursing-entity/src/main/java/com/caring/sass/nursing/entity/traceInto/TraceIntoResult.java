package com.caring.sass.nursing.entity.traceInto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 选项跟踪结果异常表
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_trace_into_result")
@ApiModel(value = "TraceIntoResult", description = "选项跟踪结果异常表")
@AllArgsConstructor
public class TraceIntoResult extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "随访计划ID")
    @NotNull(message = "随访计划ID不能为空")
    @TableField("plan_id")
    @Excel(name = "随访计划ID")
    private Long planId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @NotNull(message = "患者ID不能为空")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @NotNull(message = "医助ID不能为空")
    @TableField("nursing_id")
    @Excel(name = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @NotNull(message = "表单ID不能为空")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    @ApiModelProperty(value = "表单结果ID")
    @NotNull(message = "表单结果ID不能为空")
    @TableField("form_result_id")
    private Long formResultId;

    /**
     * 表单结果的上传时间
     */
    @ApiModelProperty(value = "表单结果的上传时间")
    @NotNull(message = "表单结果的上传时间不能为空")
    @TableField("push_time")
    @Excel(name = "表单结果的上传时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime pushTime;

    /**
     * 处理状态（0 未处理， 1 已处理）
     */
    @ApiModelProperty(value = "处理状态（0 未处理， 1 已处理）")
    @NotNull(message = "处理状态（0 未处理， 1 已处理）不能为空")
    @TableField("handle_status")
    @Excel(name = "处理状态（0 未处理， 1 已处理）")
    private Integer handleStatus;

    /**
     * 清空状态（0 未处理， 1 已处理）
     */
    @ApiModelProperty(value = "清空状态（0 未处理， 1 已处理）")
    @NotNull(message = "清空状态（0 未处理， 1 已处理）不能为空")
    @TableField("clear_status")
    @Excel(name = "清空状态（0 未处理， 1 已处理）")
    private Integer clearStatus;



    @Builder
    public TraceIntoResult(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long planId, Long patientId, Long nursingId, Long formId,
                    LocalDateTime pushTime, Integer handleStatus) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.planId = planId;
        this.patientId = patientId;
        this.nursingId = nursingId;
        this.formId = formId;
        this.pushTime = pushTime;
        this.handleStatus = handleStatus;
    }

}
