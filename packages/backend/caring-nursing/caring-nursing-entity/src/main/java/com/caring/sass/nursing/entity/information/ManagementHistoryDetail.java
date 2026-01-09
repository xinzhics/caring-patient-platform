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

/**
 * <p>
 * 实体类
 * 管理历史详细记录
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_management_history_detail")
@ApiModel(value = "ManagementHistoryDetail", description = "管理历史详细记录")
@AllArgsConstructor
public class ManagementHistoryDetail extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者数量
     */
    @ApiModelProperty(value = "患者数量")
    @TableField("patient_number")
    @Deprecated
    @Excel(name = "患者数量")
    private Integer patientNumber;

    /**
     * 管理历史ID
     */
    @ApiModelProperty(value = "管理历史ID")
    @TableField("history_id")
    @Excel(name = "管理历史ID")
    private Long historyId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 患者姓名
     */
    @ApiModelProperty(value = "患者ID")
    @TableField(exist = false)
    private String patientName;

    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @TableField(exist = false)
    private String avatar;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = " 医生姓名")
    @TableField(exist = false)
    private String doctorName;


    @Builder
    public ManagementHistoryDetail(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Integer patientNumber, Long historyId, Long patientId, Long doctorId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientNumber = patientNumber;
        this.historyId = historyId;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

}
