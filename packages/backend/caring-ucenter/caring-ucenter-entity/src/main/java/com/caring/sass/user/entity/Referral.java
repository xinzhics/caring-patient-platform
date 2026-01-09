package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * 患者转诊
 * </p>
 *
 * @author leizhi
 * @since 2021-08-27
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_referral")
@ApiModel(value = "Referral", description = "患者转诊")
@AllArgsConstructor
public class Referral extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    @TableField("patient_id")
    @Excel(name = "患者id")
    private Long patientId;

    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @Length(max = 255, message = "患者头像长度不能超过255")
    @TableField(value = "patient_avatar", condition = LIKE)
    @Excel(name = "患者头像")
    private String patientAvatar;

    /**
     * 患者姓名
     */
    @ApiModelProperty(value = "患者姓名")
    @Length(max = 255, message = "患者姓名长度不能超过255")
    @TableField(value = "patient_name", condition = LIKE)
    @Excel(name = "患者姓名")
    private String patientName;

    /**
     * 患者性别，0男，1女
     */
    @ApiModelProperty(value = "患者性别，0男，1女")
    @TableField("patient_sex")
    @Excel(name = "患者性别，0男，1女", replace = {"是_true", "否_false", "_null"})
    private Integer patientSex;

    /**
     * 患者年龄
     */
    @ApiModelProperty(value = "患者年龄")
    @TableField("patient_age")
    @Excel(name = "患者年龄")
    private Integer patientAge;

    @ApiModelProperty(value = "发起医助对患者的备注")
    @TableField("launch_nursing_patient_remark")
    private String launchNursingPatientRemark;

    @ApiModelProperty(value = "发起医生对患者的备注")
    @TableField("launch_doctor_patient_remark")
    private String launchDoctorPatientRemark;

    /**
     * 发起医生id
     */
    @ApiModelProperty(value = "发起医生id")
    @TableField("launch_doctor_id")
    @Excel(name = "发起医生id")
    private Long launchDoctorId;

    /**
     * 发起医生姓名
     */
    @ApiModelProperty(value = "发起医生姓名")
    @Length(max = 255, message = "发起医生姓名长度不能超过255")
    @TableField(value = "launch_doctor_name", condition = LIKE)
    @Excel(name = "发起医生姓名")
    private String launchDoctorName;

    /**
     * 接收医生id
     */
    @ApiModelProperty(value = "接收医生id")
    @TableField("accept_doctor_id")
    @Excel(name = "接收医生id")
    private Long acceptDoctorId;

    /**
     * 接收医生医助id
     */
    @ApiModelProperty(value = "接收医生医助id")
    @TableField("accept_service_id")
    @Excel(name = "接收医生医助id")
    private Long acceptServiceId;

    /**
     * 发起医生医助的id
     */
    @ApiModelProperty(value = "发起医生医助的id")
    @TableField("launch_service_id")
    @Excel(name = "发起医生医助的id")
    private Long launchServiceId;

    /**
     * 接收医生姓名
     */
    @ApiModelProperty(value = "接收医生姓名")
    @Length(max = 255, message = "接收医生姓名长度不能超过255")
    @TableField(value = "accept_doctor_name", condition = LIKE)
    @Excel(name = "接收医生姓名")
    private String acceptDoctorName;

    /**
     * 转诊状态：0未接收、1已接收、2已取消
     */
    @ApiModelProperty(value = "转诊状态：0未接收、1已接收、2已取消")
    @TableField("referral_status")
    @Excel(name = "转诊状态：0未接收、1已接收、2已取消")
    private Integer referralStatus;

    /**
     * 转诊性质：0单次转诊、1长期转诊
     */
    @ApiModelProperty(value = "转诊性质：0单次转诊、1长期转诊")
    @TableField("referral_category")
    @Excel(name = "转诊性质：0单次转诊、1长期转诊", replace = {"是_true", "否_false", "_null"})
    private Integer referralCategory;

    @ApiModelProperty(value = "接收转诊时间")
    @TableField(value = "accept_time")
    private LocalDateTime acceptTime;

    @ApiModelProperty(value = "发起转诊时间")
    @TableField(value = "launch_time")
    private LocalDateTime launchTime;

    /**
     * 接收医生医院
     */
    @ApiModelProperty(value = "接收医生医院")
    @TableField(value = "accept_doctor_hospital_name", condition = LIKE)
    @Excel(name = "接收医生医院")
    private String acceptDoctorHospitalName;

    /**
     * 转诊二维码路径
     */
    @ApiModelProperty(value = "转诊二维码路径")
    @TableField(value = "qr_url", condition = LIKE)
    @Excel(name = "转诊二维码路径")
    private String qrUrl;

    @ApiModelProperty("医生或医助对患者的备注")
    @TableField(exist = false)
    private String remark;

    @Builder
    public Referral(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                    Long patientId, String patientAvatar, String patientName, Integer patientSex, Integer patientAge,
                    Long launchDoctorId, String launchDoctorName, Long acceptDoctorId, String acceptDoctorName,
                    Integer referralStatus, Integer referralCategory, LocalDateTime acceptTime,
                    LocalDateTime launchTime, String qrUrl, String acceptDoctorHospitalName) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientId = patientId;
        this.patientAvatar = patientAvatar;
        this.patientName = patientName;
        this.patientSex = patientSex;
        this.patientAge = patientAge;
        this.launchDoctorId = launchDoctorId;
        this.launchDoctorName = launchDoctorName;
        this.acceptDoctorId = acceptDoctorId;
        this.acceptDoctorName = acceptDoctorName;
        this.referralStatus = referralStatus;
        this.referralCategory = referralCategory;
        this.acceptTime = acceptTime;
        this.launchTime = launchTime;
        this.qrUrl = qrUrl;
        this.acceptDoctorHospitalName = acceptDoctorHospitalName;
    }

}
