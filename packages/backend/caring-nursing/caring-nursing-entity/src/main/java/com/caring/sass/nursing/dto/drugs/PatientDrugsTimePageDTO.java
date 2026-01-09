package com.caring.sass.nursing.dto.drugs;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
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
 * 每次推送生成一条记录，（记录药量，药品）
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
@ApiModel(value = "PatientDrugsTimePageDTO", description = "每次推送生成一条记录，（记录药量，药品）")
public class PatientDrugsTimePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者药品id
     */
    @ApiModelProperty(value = "患者药品id")
    @Length(max = 255, message = "患者药品id长度不能超过255")
    private String drugsId;
    /**
     * 用药时间
     */
    @ApiModelProperty(value = "用药时间")
    private LocalDateTime drugsTime;
    /**
     * 状态(1:已打卡 2：未打卡)
     */
    @ApiModelProperty(value = "状态(1:已打卡 2：未打卡)")
    private Integer status;
    /**
     * 消耗量
     */
    @ApiModelProperty(value = "消耗量")
    private Float drugsDose;
    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    @Length(max = 50, message = "患者id长度不能超过50")
    private String patientId;
    /**
     * 药品名称，冗余字段
     */
    @ApiModelProperty(value = "药品名称，冗余字段")
    @Length(max = 100, message = "药品名称，冗余字段长度不能超过100")
    private String medicineName;
    /**
     * 药品图片，冗余字段
     */
    @ApiModelProperty(value = "药品图片，冗余字段")
    @Length(max = 255, message = "药品图片，冗余字段长度不能超过255")
    private String medicineIcon;
    /**
     * 吃药单位
     */
    @ApiModelProperty(value = "吃药单位")
    @Length(max = 20, message = "吃药单位长度不能超过20")
    private String unit;

}
