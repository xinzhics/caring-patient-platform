package com.caring.sass.nursing.entity.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 管理历史
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@Builder

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_management_history")
@ApiModel(value = "ManagementHistory", description = "管理历史")
@AllArgsConstructor
public class ManagementHistory extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 信息完整度
     */
    @ApiModelProperty(value = "信息完整度")
    @Length(max = 255, message = "信息完整度长度不能超过255")
    @TableField(value = "history_type", condition = LIKE)
    @Excel(name = "信息完整度")
    private String historyType;

    /**
     * 患者数量
     */
    @ApiModelProperty(value = "患者数量")
    @TableField("patient_number")
    @Excel(name = "患者数量")
    private Integer patientNumber;

    /**
     * 医助的ID
     */
    @ApiModelProperty(value = "医助的ID")
    @TableField("nursing_id")
    @Excel(name = "医助的ID")
    private Long nursingId;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @TableField("org_id")
    @Excel(name = "机构ID")
    private Long orgId;

    /**
     *
     * 发送类型：1->单发，2->群发
     */
    @ApiModelProperty(value = "发送发送：1->单发，2->群发")
    @TableField("send_type")
    private Integer sendType;

    @ApiModelProperty(value = "信息完整度详情")
    @TableField(exist = false)
    private List<ManagementHistoryDetail> historyDetails;



}
