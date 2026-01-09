package com.caring.sass.nursing.entity.form;

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
 * 表单设置的成绩规则
 * </p>
 *
 * @author 杨帅
 * @since 2023-10-11
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form_score_rule")
@ApiModel(value = "FormScoreRule", description = "表单设置的成绩规则")
@AllArgsConstructor
public class FormScoreRule extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 结果计算方式(sum_score, average_score, sum_average_score)
     */
    @ApiModelProperty(value = "结果计算方式(sum_score, average_score, sum_average_score)")
    @Length(max = 255, message = "结果计算方式(sum_score, average_score, sum_average_score)长度不能超过255")
    @TableField(value = "form_result_count_way", condition = LIKE)
    @Excel(name = "结果计算方式(sum_score, average_score, sum_average_score)")
    private String formResultCountWay;

    /**
     * 展示总分
     */
    @ApiModelProperty(value = "展示总分")
    @TableField("show_result_sum")
    @Excel(name = "展示总分")
    private Integer showResultSum;

    /**
     * 展示分组之和
     */
    @ApiModelProperty(value = "展示分组之和")
    @TableField("show_group_sum")
    @Excel(name = "展示分组之和")
    private Integer showGroupSum;

    /**
     * 展示平均分
     */
    @ApiModelProperty(value = "展示平均分")
    @TableField("show_average")
    @Excel(name = "展示平均分")
    private Integer showAverage;

    /**
     * 所属表单ID
     */
    @ApiModelProperty(value = "所属表单ID")
    @TableField("form_id")
    @Excel(name = "所属表单ID")
    private Long formId;




}
