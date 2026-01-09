package com.caring.sass.msgs.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者系统消息备注表
 * </p>
 *
 * @author 杨帅
 * @since 2024-12-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientSystemMessageRemarkSaveDTO", description = "患者系统消息备注表")
public class PatientSystemMessageRemarkSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @ApiModelProperty(value = "消息ID")
    @NotNull(message = "消息ID不能为空")
    private Long messageId;
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @ApiModelProperty(value = "患者ID")
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    @NotNull(message = "医生名称不能为空")
    private String doctorName;
    /**
     * 医生评论的内容
     */
    @ApiModelProperty(value = "医生评论的内容")
    @Length(max = 500, message = "医生评论的内容长度不能超过500")
    private String commentContent;

}
