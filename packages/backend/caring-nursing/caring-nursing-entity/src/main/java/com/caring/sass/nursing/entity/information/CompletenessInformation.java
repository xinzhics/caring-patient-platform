package com.caring.sass.nursing.entity.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者信息完整度概览表
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_completeness_information")
@ApiModel(value = "CompletenessInformation", description = "患者信息完整度概览表")
@AllArgsConstructor
public class CompletenessInformation extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 最后填写时间
     */
    @ApiModelProperty(value = "最后填写时间")
    @TableField("last_write_time")
    @Excel(name = "最后填写时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime lastWriteTime;

    /**
     * 患者信息完整度
     */
    @ApiModelProperty(value = "患者信息完整度")
    @TableField("completion")
    @Excel(name = "患者信息完整度")
    private Integer completion;

    /**
     * 患者信息是否完整
     */
    @ApiModelProperty(value = "患者信息是否完整")
    @TableField("complete")
    @Excel(name = "患者信息是否完整")
    private Integer complete;

    @ApiModelProperty(value = "患者名")
    @TableField(exist = false)
    private String patientName;

    @ApiModelProperty(value = "患者头像")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty(value = "医生ID")
    @TableField(exist = false)
    private Long doctorId;

    @ApiModelProperty(value = "医助ID")
    @TableField(exist = false)
    private Long nursingId;

    @ApiModelProperty(value = "患者所属医生")
    @TableField(exist = false)
    private String doctorName;

    @ApiModelProperty(value = "患者个人信息")
    @TableField(exist = false)
    private List<PatientInformationField> patientInformationFields;


    @ApiModelProperty(value = "患者个人未完善信息")
    @TableField(exist = false)
    private List<PatientInformationField> incompleteInformationFields;
}
