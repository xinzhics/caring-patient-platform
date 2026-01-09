package com.caring.sass.nursing.entity.form;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
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
 * TODO:: 加密后删除
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form_result")
@ApiModel(value = "FormResultQuery", description = "表单填写结果表 用于查询未加密数据使用")
@AllArgsConstructor
public class FormResultQuery extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 对微信应消息回执中的Id
     */
    @ApiModelProperty(value = "对微信应消息回执中的Id")
    @Length(max = 32, message = "对微信应消息回执中的Id长度不能超过32")
    @TableField(value = "message_id", condition = LIKE)
    @Excel(name = "对微信应消息回执中的Id")
    private String messageId;

    @ApiModelProperty(value = "随访从待办中进入后提交表单时需要上传")
    @TableField(exist = false)
    private Long planDetailTimeId;

    /**
     * 业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同
     */
    @ApiModelProperty(value = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同")
    @Length(max = 32, message = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同长度不能超过32")
    @TableField(value = "business_id", condition = LIKE)
    @Excel(name = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同")
    private String businessId;

    /**
     * 表单Id
     */
    @ApiModelProperty(value = "表单Id")
    @NotNull(message = "表单Id不能为空")
    @TableField(value = "form_id", condition = EQUAL)
    @Excel(name = "表单Id")
    private Long formId;

    /**
     * 填写人Id
     */
    @ApiModelProperty(value = "填写人Id")
    @NotNull(message = "填写人Id不能为空")
    @TableField(value = "user_id", condition = EQUAL)
    @Excel(name = "填写人Id")
    private Long userId;

    /**
     * 填写结果数据
     */
    @ApiModelProperty(value = "填写结果数据")

    @TableField("json_content")
    @Excel(name = "填写结果数据")
    private String jsonContent;

    /**
     * 对应CustomForm中的此属性
     */
    @ApiModelProperty(value = "对应CustomForm中的此属性")
    @Length(max = 255, message = "对应CustomForm中的此属性长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "对应CustomForm中的此属性")
    private String name;

    /**
     * 类型
     * #{HEALTH_RECORD=疾病信息;BASE_INFO=基本信息}
     */
    @ApiModelProperty(value = "类型")
    @TableField("category")
    @Excel(name = "类型", width = 20, replace = {"疾病信息_HEALTH_RECORD", "基本信息_BASE_INFO", "_null"})
    private FormEnum category;

    /**
     * 一题一页，1是，2否
     */
    @ApiModelProperty(value = "一题一页，1是，2否")
    @TableField(value = "one_question_page", condition = EQUAL)
    private Integer oneQuestionPage;

    /**
     * 趋势图
     */
    @ApiModelProperty(value = "趋势图，1 是， null 0 否")
    @TableField(value = "show_trend", condition = EQUAL)
    @Excel(name = "趋势图")
    private Integer showTrend;

    @ApiModelProperty(value = "基本信息疾病信息一题一页模式已经填写的进度 完成时传 -1")
    @TableField(value = "fill_in_index", condition = EQUAL)
    private Integer fillInIndex;

    @TableField(exist = false)
    private List<?> fieldList;

    @ApiModelProperty(value = "不更新患者的信息完整度")
    @TableField(exist = false)
    private Boolean noUpdatePatientInformation;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "暂时只控制疾病信息 删除标记 0 未删，1 删除")
    @TableField("delete_mark")
    private Integer deleteMark;


    @ApiModelProperty(value = "是否来自历史修改记录")
    @TableField("form_history")
    private Integer formHistory;

    @ApiModelProperty(value = "评分问卷")
    @TableField("score_questionnaire")
    private Integer scoreQuestionnaire;

    @ApiModelProperty(value = "校验数据异常状态0 表示无状态。 1表示正常， 2表示异常")
    @TableField("form_error_result")
    private Integer formErrorResult;

    @ApiModelProperty(value = "数据反馈的结果JSON, null不用显示")
    @TableField("data_feed_back")
    private String dataFeedBack;

    @ApiModelProperty(value = "首次提交时间")
    @TableField("first_submit_time")
    private LocalDateTime firstSubmitTime;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

    @ApiModelProperty(value = "是否显示总分")
    @TableField(exist = false)
    private Integer showFormResultSumScore;

    @ApiModelProperty(value = "总分 或 总分和+平均分")
    @TableField(exist = false)
    private Float formResultSumScore;

    @ApiModelProperty(value = "是否显示平均分")
    @TableField(exist = false)
    private Integer showFormResultAverageScore;

    @ApiModelProperty(value = "平均分")
    @TableField(exist = false)
    private Float formResultAverageScore;

    @ApiModelProperty(value = "IM推荐功能回执 此字段为1 表示提交的表单是因为im的推荐功能提交的，其中的messageId是chat对象的id")
    @TableField(exist = false)
    private Integer imRecommendReceipt;

}
