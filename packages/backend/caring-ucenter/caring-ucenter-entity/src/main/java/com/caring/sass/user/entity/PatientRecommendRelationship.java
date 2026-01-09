package com.caring.sass.user.entity;

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
 * 运营配置-患者推荐关系
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
@TableName("u_user_patient_recommend_relationship")
@ApiModel(value = "PatientRecommendRelationship", description = "运营配置-患者推荐关系")
@AllArgsConstructor
public class PatientRecommendRelationship extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 推荐人患者ID
     */
    @ApiModelProperty(value = "推荐人患者ID")
    @TableField("patient_id")
    @Excel(name = "推荐人患者ID")
    private Long patientId;

    /**
     * 推荐人患者名称
     */
    @ApiModelProperty(value = "推荐人患者名称")
    @Length(max = 40, message = "推荐人患者名称长度不能超过40")
    @TableField(value = "patient_name", condition = LIKE)
    @Excel(name = "推荐人患者名称")
    private String patientName;

    /**
     * 推荐人二维码医生类型 (1 推荐人原医生， 2 指定医生)
     */
    @ApiModelProperty(value = "推荐人二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    @TableField("qr_doctor_type")
    @Excel(name = "推荐人二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    private Integer qrDoctorType;

    /**
     * 推荐人二维码医生ID
     */
    @ApiModelProperty(value = "推荐人二维码医生ID")
    @TableField("doctor_id")
    @Excel(name = "推荐人二维码医生ID")
    private Long doctorId;

    /**
     * 推荐人二维码医生名称
     */
    @ApiModelProperty(value = "推荐人二维码医生名称")
    @Length(max = 40, message = "推荐人二维码医生名称长度不能超过40")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "推荐人二维码医生名称")
    private String doctorName;

    /**
     * 受邀人患者ID
     */
    @ApiModelProperty(value = "受邀人患者ID")
    @TableField("passive_patient_id")
    @Excel(name = "受邀人患者ID")
    private Long passivePatientId;

    /**
     * 受邀人患者名称
     */
    @ApiModelProperty(value = "受邀人患者名称")
    @Length(max = 40, message = "受邀人患者名称长度不能超过40")
    @TableField(value = "passive_patient_name", condition = LIKE)
    @Excel(name = "受邀人患者名称")
    private String passivePatientName;

    /**
     * 受邀人患者注册状态 (1 未注册， 2 已注册)
     */
    @ApiModelProperty(value = "受邀人患者注册状态 (1 未注册， 2 已注册)")
    @TableField("passive_patient_register")
    @Excel(name = "受邀人患者注册状态 (1 未注册， 2 已注册)")
    private Integer passivePatientRegister;

    /**
     * 受邀人患者注册时间
     */
    @ApiModelProperty(value = "受邀人患者注册时间")
    @TableField("passive_patient_register_time")
    @Excel(name = "受邀人患者注册时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime passivePatientRegisterTime;

    /**
     * 受邀人扫码时间
     */
    @ApiModelProperty(value = "受邀人扫码时间")
    @TableField("scan_code_time")
    @Excel(name = "受邀人扫码时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime scanCodeTime;


    @Builder
    public PatientRecommendRelationship(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long patientId, String patientName, Integer qrDoctorType, Long doctorId, String doctorName, 
                    Long passivePatientId, String passivePatientName, Integer passivePatientRegister, LocalDateTime passivePatientRegisterTime, LocalDateTime scanCodeTime) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientId = patientId;
        this.patientName = patientName;
        this.qrDoctorType = qrDoctorType;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.passivePatientId = passivePatientId;
        this.passivePatientName = passivePatientName;
        this.passivePatientRegister = passivePatientRegister;
        this.passivePatientRegisterTime = passivePatientRegisterTime;
        this.scanCodeTime = scanCodeTime;
    }

}
