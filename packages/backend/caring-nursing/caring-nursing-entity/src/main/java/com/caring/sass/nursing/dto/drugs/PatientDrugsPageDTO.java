package com.caring.sass.nursing.dto.drugs;

import java.time.LocalDateTime;
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
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientDrugsPageDTO", description = "患者添加的用药")
public class PatientDrugsPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    private Long patientId;
    /**
     * 药品id
     */
    @ApiModelProperty(value = "药品id")
    private Long drugsId;
    /**
     * 每天用药次数
     */
    @ApiModelProperty(value = "每天用药次数")
    private Integer numberOfDay;
    /**
     * 每天已推送打卡次数
     */
    @ApiModelProperty(value = "每天已推送打卡次数")
    private Integer checkinNum;
    /**
     * 用药时间:如（08:00,09:00）
     */
    @ApiModelProperty(value = "用药时间:如（08:00,09:00）")
    @Length(max = 255, message = "用药时间:如（08:00,09:00）长度不能超过255")
    private String drugsTime;
    /**
     * 用药周期(0  代表无限期  1：选择截止日期)
     */
    @ApiModelProperty(value = "用药周期(0  代表无限期  1：选择截止日期)")
    private Integer cycle;
    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private LocalDate endTime;
    /**
     * 每次剂量
     */
    @ApiModelProperty(value = "每次剂量")
    private Float dose;
    /**
     * 0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）
     */
    @ApiModelProperty(value = "0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）")
    private Integer status;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    @Length(max = 20, message = "单位长度不能超过20")
    private String unit;
    /**
     * 周期天数
     */
    @ApiModelProperty(value = "周期天数")
    private Integer cycleDay;
    /**
     * 药品图标
     */
    @ApiModelProperty(value = "药品图标")
    @Length(max = 200, message = "药品图标长度不能超过200")
    private String medicineIcon;
    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    private String medicineName;
    /**
     * 盒数
     */
    @ApiModelProperty(value = "盒数")
    private Integer numberOfBoxes;

}
