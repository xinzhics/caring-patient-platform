package com.caring.sass.nursing.dto.drugs;

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
 * 患者每天的用药量记录（一天生成一次）
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
@ApiModel(value = "PatientDayDrugsSaveDTO", description = "患者每天的用药量记录（一天生成一次）")
public class PatientDayDrugsSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    private Long patientId;
    /**
     * 每天用药总量
     */
    @ApiModelProperty(value = "每天用药总量")
    private Float drugsCountOfDay;
    /**
     * 每天已用总量
     */
    @ApiModelProperty(value = "每天已用总量")
    private Float takeDrugsCountOfDay;
    /**
     * 已打卡次数
     */
    @ApiModelProperty(value = "已打卡次数")
    private Integer checkinedNumber;
    /**
     * 总打卡次数
     */
    @ApiModelProperty(value = "总打卡次数")
    private Integer checkinNumberTotal;
    /**
     * 0:未打卡  1：部分打卡   2已打卡
     */
    @ApiModelProperty(value = "0:未打卡  1：部分打卡   2已打卡")
    private Integer status;
    @ApiModelProperty(value = "")
    private Integer dayCompliance;
    /**
     * 医生Id
     */
    @ApiModelProperty(value = "医生Id")
    private Long doctorId;
    /**
     * 医助Id
     */
    @ApiModelProperty(value = "医助Id")
    private Long serviceAdvisorId;

}
