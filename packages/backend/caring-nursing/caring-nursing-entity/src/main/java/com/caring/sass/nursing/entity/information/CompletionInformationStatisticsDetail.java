package com.caring.sass.nursing.entity.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * 信息完整度区间统计详细表
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_completion_information_statistics_detail")
@ApiModel(value = "CompletionInformationStatisticsDetail", description = "信息完整度区间统计详细表")
@AllArgsConstructor
public class CompletionInformationStatisticsDetail extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @ApiModelProperty(value = "统计ID")
    @TableField("statistics_id")
    @Excel(name = "统计ID")
    private Long statisticsId;

    /**
     * 区间ID
     */
    @ApiModelProperty(value = "区间ID")
    @TableField("interval_id")
    @Excel(name = "区间ID")
    private Long intervalId;

    /**
     * 区间名称
     */
    @ApiModelProperty(value = "区间名称")
    @Length(max = 20, message = "区间名称长度不能超过20")
    @TableField(value = "interval_name", condition = LIKE)
    @Excel(name = "区间名称")
    private String intervalName;

    /**
     * 区间最小值
     */
    @ApiModelProperty(value = "区间最小值")
    @TableField("interval_min_value")
    @Excel(name = "区间最小值")
    private Double intervalMinValue;

    /**
     * 区间最大值
     */
    @ApiModelProperty(value = "区间最大值")
    @TableField("interval_max_value")
    @Excel(name = "区间最大值")
    private Double intervalMaxValue;

    /**
     * 区间占比
     */
    @ApiModelProperty(value = "区间占比")
    @TableField("interval_proportion")
    @Excel(name = "区间占比")
    private Integer intervalProportion;

    /**
     * 患者人数
     */
    @ApiModelProperty(value = "患者人数")
    @TableField("patient_number")
    @Excel(name = "患者人数")
    private Integer patientNumber;


    @Builder
    public CompletionInformationStatisticsDetail(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long statisticsId, Long intervalId, String intervalName, Double intervalMinValue, Double intervalMaxValue, 
                    Integer intervalProportion, Integer patientNumber) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.statisticsId = statisticsId;
        this.intervalId = intervalId;
        this.intervalName = intervalName;
        this.intervalMinValue = intervalMinValue;
        this.intervalMaxValue = intervalMaxValue;
        this.intervalProportion = intervalProportion;
        this.patientNumber = patientNumber;
    }

}
