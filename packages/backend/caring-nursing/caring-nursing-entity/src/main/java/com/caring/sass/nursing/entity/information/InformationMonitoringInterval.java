package com.caring.sass.nursing.entity.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
 * 信息完整度区间设置
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_information_monitoring_interval")
@ApiModel(value = "InformationMonitoringInterval", description = "信息完整度区间设置")
@AllArgsConstructor
public class InformationMonitoringInterval extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 区间名称
     */
    @ApiModelProperty(value = "区间名称")
    @Length(max = 255, message = "区间名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "区间名称")
    private String name;

    /**
     * 最小值
     */
    @ApiModelProperty(value = "最小值")
    @TableField("min_value")
    @Excel(name = "最小值")
    private Double minValue;

    /**
     * 最大值
     */
    @ApiModelProperty(value = "最大值")
    @TableField("max_value")
    @Excel(name = "最大值")
    private Double maxValue;

    @ApiModelProperty(value = "患者人数")
    @TableField(exist = false)
    private int patientNumber;




}
