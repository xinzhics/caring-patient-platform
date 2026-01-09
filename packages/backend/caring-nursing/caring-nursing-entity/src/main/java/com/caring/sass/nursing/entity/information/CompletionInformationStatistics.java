package com.caring.sass.nursing.entity.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;
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
import java.util.List;

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
 * 信息完整度统计
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
@TableName("t_nursing_completion_information_statistics")
@ApiModel(value = "CompletionInformationStatistics", description = "信息完整度统计")
@AllArgsConstructor
public class CompletionInformationStatistics extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @TableField("nursing_id")
    @Excel(name = "医助ID")
    private Long nursingId;

    /**
     * 患者总数
     */
    @ApiModelProperty(value = "患者总数")
    @TableField("patient_total")
    @Excel(name = "患者总数")
    private Integer patientTotal;

    /**
     * 记录日期
     */
    @ApiModelProperty(value = "记录日期")
    @TableField("statistics_date")
    @Excel(name = "记录日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate statisticsDate;

    @ApiModelProperty(value = "区间统计的详情")
    @TableField(exist = false)
    private List<CompletionInformationStatisticsDetail> statisticsDetailList;

    @Builder
    public CompletionInformationStatistics(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long nursingId, Integer patientTotal, LocalDate statisticsDate) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.nursingId = nursingId;
        this.patientTotal = patientTotal;
        this.statisticsDate = statisticsDate;
    }

}
