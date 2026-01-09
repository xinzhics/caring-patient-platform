package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
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
 * 运营配置-患者推荐配置
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
@ApiModel(value = "PatientRecommendSettingUpdateDTO", description = "运营配置-患者推荐配置")
public class PatientRecommendSettingUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 二维码医生类型 (1 推荐人原医生， 2 指定医生)
     */
    @ApiModelProperty(value = "二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    private Integer qrDoctorType;
    /**
     * 二维码医生ID
     */
    @ApiModelProperty(value = "二维码医生ID")
    private Long doctorId;
    /**
     * 二维码医生名称
     */
    @ApiModelProperty(value = "二维码医生名称")
    @Length(max = 40, message = "二维码医生名称长度不能超过40")
    private String doctorName;
    /**
     * 活动规则介绍开关 (1 关闭， 2 开启)
     */
    @ApiModelProperty(value = "活动规则介绍开关 (1 关闭， 2 开启)")
    private Integer activityRuleSwitch;
    /**
     * 活动规则介绍H5链接
     */
    @ApiModelProperty(value = "活动规则介绍H5链接")
    @Length(max = 1000, message = "活动规则介绍H5链接长度不能超过1000")
    private String activityRuleUrl;
    /**
     * 活动海报链接
     */
    @ApiModelProperty(value = "活动海报链接")
    @Length(max = 1000, message = "活动海报链接长度不能超过1000")
    private String posterUrl;
    /**
     * 活动截止日期
     */
    @ApiModelProperty(value = "活动截止日期")
    private LocalDate activityEndDate;
}
