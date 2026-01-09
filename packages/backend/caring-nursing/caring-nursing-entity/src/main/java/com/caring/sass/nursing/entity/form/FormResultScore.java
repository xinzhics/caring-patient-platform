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
 * 表单结果的成绩计算
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
@TableName("t_custom_form_result_score")
@ApiModel(value = "FormResultScore", description = "表单结果的成绩计算")
@AllArgsConstructor
public class FormResultScore extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 总分
     */
    @ApiModelProperty(value = "总分")
    @TableField("form_result_sum_score")
    @Excel(name = "总分")
    private Float formResultSumScore;

    /**
     * 平均分
     */
    @ApiModelProperty(value = "平均分")
    @TableField("form_result_average_score")
    @Excel(name = "平均分")
    private Float formResultAverageScore;

    /**
     * 总分和+平均分
     */
    @ApiModelProperty(value = "总分和+平均分")
    @TableField("form_result_sum_average_score")
    @Excel(name = "总分和+平均分")
    private Float formResultSumAverageScore;

    /**
     * 各分组成绩json数据对象
     */
    @ApiModelProperty(value = "各分组成绩json数据对象")
    @TableField("form_field_group_sum_info")
    @Excel(name = "各分组成绩json数据对象")
    private String formFieldGroupSumInfo;


    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    private Long formId;

    @ApiModelProperty(value = "表单结果ID")
    @TableField("form_result_id")
    private Long formResultId;

    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    private Long patientId;


    @ApiModelProperty(value = "评分问卷解析")
    @TableField(exist = false)
    private String scoreQuestionnaireAnalysis;

    @ApiModelProperty(value = "评分问卷解析")
    @TableField(exist = false)
    private Boolean showScoreQuestionnaireAnalysis;


    @ApiModelProperty(value = "计分规则")
    @TableField(exist = false)
    private FormScoreRule scoreRule;


    @ApiModelProperty(value = "分组设置")
    @TableField(exist = false)
    private List<FormFieldsGroup> fieldsGroups;

}
