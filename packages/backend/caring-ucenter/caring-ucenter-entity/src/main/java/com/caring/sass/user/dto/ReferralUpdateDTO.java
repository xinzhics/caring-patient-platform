package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;
import java.time.LocalDateTime;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ReferralUpdateDTO", description = "患者转诊")
public class ReferralUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    private Long patientId;
    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @Length(max = 255, message = "患者头像长度不能超过255")
    private String patientAvatar;
    /**
     * 患者姓名
     */
    @ApiModelProperty(value = "患者姓名")
    @Length(max = 255, message = "患者姓名长度不能超过255")
    private String patientName;
    /**
     * 患者性别，0男，1女
     */
    @ApiModelProperty(value = "患者性别，0男，1女")
    private Integer patientSex;
    /**
     * 患者年龄
     */
    @ApiModelProperty(value = "患者年龄")
    private Integer patientAge;
    /**
     * 发起医生id
     */
    @ApiModelProperty(value = "发起医生id")
    private Long launchDoctorId;
    /**
     * 发起医生姓名
     */
    @ApiModelProperty(value = "发起医生姓名")
    @Length(max = 255, message = "发起医生姓名长度不能超过255")
    private String launchDoctorName;
    /**
     * 接收医生id
     */
    @ApiModelProperty(value = "接收医生id")
    private Long acceptDoctorId;
    /**
     * 接收医生姓名
     */
    @ApiModelProperty(value = "接收医生姓名")
    @Length(max = 255, message = "接收医生姓名长度不能超过255")
    private String acceptDoctorName;
    /**
     * 转诊状态：0未接收、1已接收、2已取消
     */
    @ApiModelProperty(value = "转诊状态：0未接收、1已接收、2已取消")
    private Integer referralStatus;
    /**
     * 转诊性质：0单次转诊、1长期转诊
     */
    @ApiModelProperty(value = "转诊性质：0单次转诊、1长期转诊")
    private Integer referralCategory;

    /**
     * 接收医生专员id
     */
    @ApiModelProperty(value = "接收医生专员id")
    private Long acceptServiceId;

    /**
     * 发起医生专员的id
     */
    @ApiModelProperty(value = "发起医生专员的id")
    private Long launchServiceId;


    @ApiModelProperty(value = "接收转诊时间")
    private LocalDateTime acceptTime;

    @ApiModelProperty(value = "发起转诊时间")
    private LocalDateTime launchTime;


    /**
     * 接收医生医院
     */
    @ApiModelProperty(value = "接收医生医院")
    private String acceptDoctorHospitalName;

    /**
     * 转诊二维码路径
     */
    @ApiModelProperty(value = "转诊二维码路径")
    private String qrUrl;
}
