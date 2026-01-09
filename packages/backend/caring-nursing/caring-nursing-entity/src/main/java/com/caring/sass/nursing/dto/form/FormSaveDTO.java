package com.caring.sass.nursing.dto.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.enumeration.FormEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FormSaveDTO", description = "自定义表单表")
public class FormSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 100, message = "业务Id长度不能超过100")
    private String businessId;

    /**
     * 一题一页，1是，2否
     */
    @ApiModelProperty(value = "一题一页，1是，2否")
    private Integer oneQuestionPage;

    /**
     * 趋势图
     */
    @ApiModelProperty(value = "趋势图，1 是， null 0 否")
    private Integer showTrend;

    @ApiModelProperty(value = "单日曲线，1 是， null 0 否")
    private Integer oneDayCurve;
    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    @Length(max = 50, message = "表单名称长度不能超过50")
    private String name;
    /**
     * 表单的数据字典描述
     */
    @ApiModelProperty(value = "表单的数据字典描述")
    private String fieldsJson;
    /**
     * 原护理计划或者detail的Id
     */
    @ApiModelProperty(value = "原护理计划或者detail的Id")
    @Length(max = 32, message = "原护理计划或者detail的Id长度不能超过32")
    private String sourceBusinessId;

    /**
     * 类型
     * #{HEALTH_RECORD=疾病信息;BASE_INFO=基本信息}
     */
    @NotNull(message = "表单类型不能为空")
    @ApiModelProperty(value = "类型")
    private FormEnum category;

    @ApiModelProperty("新增计分规则")
    private FormScoreRuleUpdateDTO formScoreRuleUpdateDTO;

    @ApiModelProperty(value = "评分问卷解析")
    @TableField("score_questionnaire_analysis")
    private String scoreQuestionnaireAnalysis;

    @ApiModelProperty(value = "评分问卷解析")
    @TableField("show_score_questionnaire_analysis")
    private Boolean showScoreQuestionnaireAnalysis;



    @ApiModelProperty("删除的题目分组ID")
    private List<Long> deleteFieldGroupIds;


    @ApiModelProperty(value = "每日提交上限")
    private Integer dailySubmissionLimit;

}
