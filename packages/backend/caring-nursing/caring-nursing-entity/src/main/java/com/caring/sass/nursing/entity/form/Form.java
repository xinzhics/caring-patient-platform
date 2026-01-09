package com.caring.sass.nursing.entity.form;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.nursing.dto.form.FormScoreRuleSaveDTO;
import com.caring.sass.nursing.dto.form.FormScoreRuleUpdateDTO;
import com.caring.sass.nursing.enumeration.FormEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 自定义表单表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form")
@ApiModel(value = "Form", description = "自定义表单表")
@AllArgsConstructor
public class Form extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 100, message = "业务Id长度不能超过100")
    @TableField(value = "business_id", condition = LIKE)
    @Excel(name = "业务Id")
    private String businessId;

    /**
     * 一题一页，1是，2否
     */
    @ApiModelProperty(value = "一题一页，1是，2否")
    @TableField(value = "one_question_page", condition = EQUAL)
    @Excel(name = "一题一页")
    private Integer oneQuestionPage;

    /**
     * 趋势图
     */
    @ApiModelProperty(value = "趋势图，1 是， null 0 否")
    @TableField(value = "show_trend", condition = EQUAL)
    @Excel(name = "趋势图")
    private Integer showTrend;


    @ApiModelProperty(value = "单日曲线，1 是， null 0 否")
    @TableField(value = "one_day_curve", condition = EQUAL)
    private Integer oneDayCurve;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    @Length(max = 50, message = "表单名称长度不能超过50")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "表单名称")
    private String name;

    /**
     * 表单的数据字典描述
     */
    @ApiModelProperty(value = "表单的数据字典描述")
    @TableField("fields_json")
    @Excel(name = "表单的数据字典描述")
    private String fieldsJson;

    /**
     * 原护理计划或者detail的Id
     */
    @ApiModelProperty(value = "原护理计划或者detail的Id")
    @Length(max = 32, message = "原护理计划或者detail的Id长度不能超过32")
    @TableField(value = "source_business_id", condition = LIKE)
    @Excel(name = "原护理计划或者detail的Id")
    private String sourceBusinessId;

    @ApiModelProperty(value = "原先的formId")
    @TableField(value = "source_form_id", condition = EQUAL)
    @Excel(name = "原先的formId")
    private Long sourceFormId;

    /**
     * 类型
     * #{HEALTH_RECORD=疾病信息;BASE_INFO=基本信息}
     */
    @ApiModelProperty(value = "类型")
    @TableField("category")
    @Excel(name = "类型", width = 20, replace = {"疾病信息_HEALTH_RECORD", "基本信息_BASE_INFO", "_null"})
    private FormEnum category;

    @ApiModelProperty(value = "评分单选")
    @TableField("score_questionnaire")
    private Integer scoreQuestionnaire;

    @ApiModelProperty(value = "评分问卷解析")
    @TableField("score_questionnaire_analysis")
    private String scoreQuestionnaireAnalysis;

    @ApiModelProperty(value = "评分问卷解析")
    @TableField("show_score_questionnaire_analysis")
    private Boolean showScoreQuestionnaireAnalysis;


    @ApiModelProperty(value = "每日提交上限 0 表示无限制")
    @TableField("daily_submission_limit")
    private Integer dailySubmissionLimit;


    @ApiModelProperty("新增计分规则")
    @TableField(exist = false)
    private FormScoreRuleUpdateDTO formScoreRuleUpdateDTO;


    @ApiModelProperty("删除的题目分组ID")
    @TableField(exist = false)
    private List<Long> deleteFieldGroupIds;



}
