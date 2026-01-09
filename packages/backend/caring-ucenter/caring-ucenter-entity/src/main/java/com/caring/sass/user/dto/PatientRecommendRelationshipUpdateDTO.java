package com.caring.sass.user.dto;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientRecommendRelationshipUpdateDTO", description = "运营配置-患者推荐关系")
public class PatientRecommendRelationshipUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 推荐人患者ID
     */
    @ApiModelProperty(value = "推荐人患者ID")
    private Long patientId;
    /**
     * 推荐人患者名称
     */
    @ApiModelProperty(value = "推荐人患者名称")
    @Length(max = 40, message = "推荐人患者名称长度不能超过40")
    private String patientName;
    /**
     * 推荐人二维码医生类型 (1 推荐人原医生， 2 指定医生)
     */
    @ApiModelProperty(value = "推荐人二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    private Integer qrDoctorType;
    /**
     * 推荐人二维码医生ID
     */
    @ApiModelProperty(value = "推荐人二维码医生ID")
    private Long doctorId;
    /**
     * 推荐人二维码医生名称
     */
    @ApiModelProperty(value = "推荐人二维码医生名称")
    @Length(max = 40, message = "推荐人二维码医生名称长度不能超过40")
    private String doctorName;
    /**
     * 受邀人患者ID
     */
    @ApiModelProperty(value = "受邀人患者ID")
    private Long passivePatientId;
    /**
     * 受邀人患者名称
     */
    @ApiModelProperty(value = "受邀人患者名称")
    @Length(max = 40, message = "受邀人患者名称长度不能超过40")
    private String passivePatientName;
    /**
     * 受邀人患者注册状态 (1 未注册， 2 已注册)
     */
    @ApiModelProperty(value = "受邀人患者注册状态 (1 未注册， 2 已注册)")
    private Integer passivePatientRegister;
    /**
     * 受邀人患者注册时间
     */
    @ApiModelProperty(value = "受邀人患者注册时间")
    private LocalDateTime passivePatientRegisterTime;
    /**
     * 受邀人扫码时间
     */
    @ApiModelProperty(value = "受邀人扫码时间")
    private LocalDateTime scanCodeTime;
}
