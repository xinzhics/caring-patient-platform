package com.caring.sass.msgs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 患者系统消息备注表
 * </p>
 *
 * @author 杨帅
 * @since 2024-12-26
 */

@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_patient_system_message_remark")
@ApiModel(value = "PatientSystemMessageRemark", description = "患者系统消息备注表")
@AllArgsConstructor
public class PatientSystemMessageRemark extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @ApiModelProperty(value = "消息ID")
    @NotNull(message = "消息ID不能为空")
    @TableField("message_id")
    @Excel(name = "消息ID")
    private Long messageId;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "患者ID")
    @NotNull(message = "患者ID不能为空")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    @NotNull(message = "医生名称不能为空")
    @TableField("doctor_name")
    @Excel(name = "医生名称")
    private String doctorName;

    /**
     * 医生评论的内容
     */
    @ApiModelProperty(value = "医生评论的内容")
    @Length(max = 500, message = "医生评论的内容长度不能超过500")
    @TableField(value = "comment_content", condition = LIKE)
    @Excel(name = "医生评论的内容")
    private String commentContent;


}
