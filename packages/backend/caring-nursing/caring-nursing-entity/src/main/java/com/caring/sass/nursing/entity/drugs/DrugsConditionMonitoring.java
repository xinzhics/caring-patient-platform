package com.caring.sass.nursing.entity.drugs;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_drugs_condition_monitoring")
@ApiModel(value = "DrugsConditionMonitoring", description = "患者管理-用药预警-预警条件表")
@AllArgsConstructor
public class DrugsConditionMonitoring extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 药品ID
     */
    @ApiModelProperty(value = "药品ID")
    @TableField("drugs_id")
    @Excel(name = "药品ID")
    private Long drugsId;

    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    @TableField(value = "drugs_name", condition = LIKE)
    @Excel(name = "药品名称")
    private String drugsName;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @Length(max = 2000, message = "规格长度不能超过2000")
    @TableField(value = "spec", condition = LIKE)
    @Excel(name = "规格")
    private String spec;

    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商")
    @Length(max = 100, message = "厂商长度不能超过100")
    @TableField(value = "manufactor", condition = LIKE)
    @Excel(name = "厂商")
    private String manufactor;

    /**
     * 提醒时间（余药不足前X天）
     */
    @ApiModelProperty(value = "提醒时间（余药不足前X天）")
    @TableField("reminder_time")
    @Excel(name = "提醒时间（余药不足前X天）")
    private Long reminderTime;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码")
    @TableField("tenant_code")
    @Excel(name = "租户编码")
    private String tenantCode;

    /**
     * 购药地址
     */
    @ApiModelProperty(value = "购药地址")
    @Length(max = 1000, message = "购药地址长度不能超过1000")
    @TableField(value = "buying_medicine_url", condition = LIKE)
    @Excel(name = "购药地址")
    private String buyingMedicineUrl;

    /**
     * 模板消息ID
     */
    @ApiModelProperty(value = "模板消息ID")
    @TableField("template_msg_id")
    @Excel(name = "模板消息ID")
    private Long templateMsgId;


    @Builder
    public DrugsConditionMonitoring(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long drugsId, String drugsName, String spec, String manufactor, Long reminderTime, 
                    String buyingMedicineUrl, Long templateMsgId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.drugsId = drugsId;
        this.drugsName = drugsName;
        this.spec = spec;
        this.manufactor = manufactor;
        this.reminderTime = reminderTime;
        this.buyingMedicineUrl = buyingMedicineUrl;
        this.templateMsgId = templateMsgId;
    }

}
