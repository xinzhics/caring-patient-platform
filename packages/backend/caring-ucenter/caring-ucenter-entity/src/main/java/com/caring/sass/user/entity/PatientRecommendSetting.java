package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 运营配置-患者推荐配置
 * </p>
 *
 * @author lixiang
 * @since 2022-07-14
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_patient_recommend_setting")
@ApiModel(value = "PatientRecommendSetting", description = "运营配置-患者推荐配置")
@AllArgsConstructor
public class PatientRecommendSetting extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    /**
     * 二维码医生类型 (1 推荐人原医生， 2 指定医生)
     */
    @ApiModelProperty(value = "二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    @TableField("qr_doctor_type")
    @Excel(name = "二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    private Integer qrDoctorType;

    /**
     * 二维码医生ID
     */
    @ApiModelProperty(value = "二维码医生ID")
    @TableField("doctor_id")
    @Excel(name = "二维码医生ID")
    private Long doctorId;

    /**
     * 二维码医生名称
     */
    @ApiModelProperty(value = "二维码医生名称")
    @Length(max = 40, message = "二维码医生名称长度不能超过40")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "二维码医生名称")
    private String doctorName;

    /**
     * 活动规则介绍开关 (1 关闭， 2 开启)
     */
    @ApiModelProperty(value = "活动规则介绍开关 (1 关闭， 2 开启)")
    @TableField("activity_rule_switch")
    @Excel(name = "活动规则介绍开关 (1 关闭， 2 开启)")
    private Integer activityRuleSwitch;

    /**
     * 活动规则介绍H5链接
     */
    @ApiModelProperty(value = "活动规则介绍H5链接")
    @Length(max = 1000, message = "活动规则介绍H5链接长度不能超过1000")
    @TableField(value = "activity_rule_url", condition = LIKE)
    @Excel(name = "活动规则介绍H5链接")
    private String activityRuleUrl;

    /**
     * 活动海报链接
     */
    @ApiModelProperty(value = "活动海报链接")
    @Length(max = 1000, message = "活动海报链接长度不能超过1000")
    @TableField(value = "poster_url", condition = LIKE)
    @Excel(name = "活动海报链接")
    private String posterUrl;

    /**
     * 活动截止日期
     */
    @ApiModelProperty(value = "活动截止日期")
    @TableField("activity_end_date")
    @Excel(name = "活动截止日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate activityEndDate;


    @Builder
    public PatientRecommendSetting(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Integer qrDoctorType, Long doctorId, String doctorName, Integer activityRuleSwitch, String activityRuleUrl, 
                    String posterUrl, LocalDate activityEndDate) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.qrDoctorType = qrDoctorType;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.activityRuleSwitch = activityRuleSwitch;
        this.activityRuleUrl = activityRuleUrl;
        this.posterUrl = posterUrl;
        this.activityEndDate = activityEndDate;
    }

}
