package com.caring.sass.wx.entity.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 微信附加信息配置
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 * 除了自动回复的开关状态，关键字的状态，其他字段都没有实际作用
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_config_additional")
@ApiModel(value = "ConfigAdditional", description = "微信附加信息配置")
@AllArgsConstructor
public class ConfigAdditional extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 公众号AppId
     */
    @ApiModelProperty(value = "公众号AppId")
    @Length(max = 32, message = "公众号AppId长度不能超过32")
    @TableField(value = "wx_app_id", condition = LIKE)
    @Excel(name = "公众号AppId")
    private String wxAppId;

    /**
     * 消息处理的回调地址
     */
    @ApiModelProperty(value = "消息处理的回调地址")
    @Length(max = 200, message = "消息处理的回调地址长度不能超过200")
    @TableField(value = "message_callback_handler_url", condition = LIKE)
    @Excel(name = "消息处理的回调地址")
    private String messageCallbackHandlerUrl;

    /**
     * 图像消息回调地址
     */
    @ApiModelProperty(value = "图像消息回调地址")
    @Length(max = 200, message = "图像消息回调地址长度不能超过200")
    @TableField(value = "image_message_handler_url", condition = LIKE)
    @Excel(name = "图像消息回调地址")
    private String imageMessageHandlerUrl;

    /**
     * 链接消息回调地址
     */
    @ApiModelProperty(value = "链接消息回调地址")
    @Length(max = 200, message = "链接消息回调地址长度不能超过200")
    @TableField(value = "link_message_handler_url", condition = LIKE)
    @Excel(name = "链接消息回调地址")
    private String linkMessageHandlerUrl;

    /**
     * 位置消息回调地址
     */
    @ApiModelProperty(value = "位置消息回调地址")
    @Length(max = 200, message = "位置消息回调地址长度不能超过200")
    @TableField(value = "location_message_handler_url", condition = LIKE)
    @Excel(name = "位置消息回调地址")
    private String locationMessageHandlerUrl;

    /**
     * 文本输入消息回调地址
     */
    @ApiModelProperty(value = "文本输入消息回调地址")
    @Length(max = 200, message = "文本输入消息回调地址长度不能超过200")
    @TableField(value = "text_message_handler_url", condition = LIKE)
    @Excel(name = "文本输入消息回调地址")
    private String textMessageHandlerUrl;

    /**
     * 发送视频消息回调地址
     */
    @ApiModelProperty(value = "发送视频消息回调地址")
    @Length(max = 200, message = "发送视频消息回调地址长度不能超过200")
    @TableField(value = "video_message_handler_url", condition = LIKE)
    @Excel(name = "发送视频消息回调地址")
    private String videoMessageHandlerUrl;

    /**
     * 发送语音消息回调地址
     */
    @ApiModelProperty(value = "发送语音消息回调地址")
    @Length(max = 200, message = "发送语音消息回调地址长度不能超过200")
    @TableField(value = "voice_message_handler_url", condition = LIKE)
    @Excel(name = "发送语音消息回调地址")
    private String voiceMessageHandlerUrl;

    /**
     * 语音识别消息回调地址
     */
    @ApiModelProperty(value = "语音识别消息回调地址")
    @Length(max = 200, message = "语音识别消息回调地址长度不能超过200")
    @TableField(value = "voice_reconige_message_handler_url", condition = LIKE)
    @Excel(name = "语音识别消息回调地址")
    private String voiceReconigeMessageHandlerUrl;

    /**
     * 订阅事件回调地址
     */
    @ApiModelProperty(value = "订阅事件回调地址")
    @Length(max = 200, message = "订阅事件回调地址长度不能超过200")
    @TableField(value = "subscribe_event_handler_url", condition = LIKE)
    @Excel(name = "订阅事件回调地址")
    private String subscribeEventHandlerUrl;

    /**
     * 点击事件回调地址
     */
    @ApiModelProperty(value = "点击事件回调地址")
    @Length(max = 200, message = "点击事件回调地址长度不能超过200")
    @TableField(value = "click_event_handler_url", condition = LIKE)
    @Excel(name = "点击事件回调地址")
    private String clickEventHandlerUrl;

    /**
     * 位置变更时间回调地址
     */
    @ApiModelProperty(value = "位置变更时间回调地址")
    @Length(max = 200, message = "位置变更时间回调地址长度不能超过200")
    @TableField(value = "location_event_handler_url", condition = LIKE)
    @Excel(name = "位置变更时间回调地址")
    private String locationEventHandlerUrl;

    /**
     * 扫码事件回调地址
     */
    @ApiModelProperty(value = "扫码事件回调地址")
    @Length(max = 200, message = "扫码事件回调地址长度不能超过200")
    @TableField(value = "scan_event_handler_url", condition = LIKE)
    @Excel(name = "扫码事件回调地址")
    private String scanEventHandlerUrl;

    /**
     * 模板任务完成事件回调地址
     */
    @ApiModelProperty(value = "模板任务完成事件回调地址")
    @Length(max = 200, message = "模板任务完成事件回调地址长度不能超过200")
    @TableField(value = "template_job_finish_event_handler_url", condition = LIKE)
    @Excel(name = "模板任务完成事件回调地址")
    private String templateJobFinishEventHandlerUrl;

    /**
     * 取消订阅事件回调地址
     */
    @ApiModelProperty(value = "取消订阅事件回调地址")
    @Length(max = 200, message = "取消订阅事件回调地址长度不能超过200")
    @TableField(value = "unsubscribe_event_handler_url", condition = LIKE)
    @Excel(name = "取消订阅事件回调地址")
    private String unsubscribeEventHandlerUrl;

    /**
     * 观察事件回调地址
     */
    @ApiModelProperty(value = "观察事件回调地址")
    @Length(max = 200, message = "观察事件回调地址长度不能超过200")
    @TableField(value = "view_event_handler_url", condition = LIKE)
    @Excel(name = "观察事件回调地址")
    private String viewEventHandlerUrl;

    /**
     * 医生微信端登录url
     */
    @ApiModelProperty(value = "医生微信端登录url")
    @Length(max = 200, message = "医生微信端登录url长度不能超过200")
    @TableField(value = "doctor_weixin_login_url", condition = LIKE)
    @Excel(name = "医生微信端登录url")
    private String doctorWeixinLoginUrl;

    /**
     * 患者打卡url
     */
    @ApiModelProperty(value = "患者打卡url")
    @Length(max = 255, message = "患者打卡url长度不能超过255")
    @TableField(value = "patient_clock_url", condition = LIKE)
    @Excel(name = "患者打卡url")
    private String patientClockUrl;

    /**
     * 疼痛日志url
     */
    @ApiModelProperty(value = "疼痛日志url")
    @Length(max = 255, message = "疼痛日志url长度不能超过255")
    @TableField(value = "patient_pain_log_url", condition = LIKE)
    @Excel(name = "疼痛日志url")
    private String patientPainLogUrl;

    /**
     * 检验数据url
     */
    @ApiModelProperty(value = "检验数据url")
    @Length(max = 255, message = "检验数据url长度不能超过255")
    @TableField(value = "patient_check_data_url", condition = LIKE)
    @Excel(name = "检验数据url")
    private String patientCheckDataUrl;

    /**
     * 预约跳转url
     */
    @ApiModelProperty(value = "预约跳转url")
    @Length(max = 255, message = "预约跳转url长度不能超过255")
    @TableField(value = "appointment_url", condition = LIKE)
    @Excel(name = "预约跳转url")
    private String appointmentUrl;

    /**
     * 入组url
     */
    @ApiModelProperty(value = "入组url")
    @Length(max = 255, message = "入组url长度不能超过255")
    @TableField(value = "patient_group_url", condition = LIKE)
    @Excel(name = "入组url")
    private String patientGroupUrl;

    /**
     * 医生进入聊天页url
     */
    @ApiModelProperty(value = "医生进入聊天页url")
    @Length(max = 255, message = "医生进入聊天页url长度不能超过255")
    @TableField(value = "doctor_chat", condition = LIKE)
    @Excel(name = "医生进入聊天页url")
    private String doctorChat;

    /**
     * 患者进入聊天页url
     */
    @ApiModelProperty(value = "患者进入聊天页url")
    @Length(max = 255, message = "患者进入聊天页url长度不能超过255")
    @TableField(value = "patient_chat", condition = LIKE)
    @Excel(name = "患者进入聊天页url")
    private String patientChat;

    /**
     * 血压监测
     */
    @ApiModelProperty(value = "血压监测")
    @Length(max = 200, message = "血压监测长度不能超过200")
    @TableField(value = "blood_pressure_url", condition = LIKE)
    @Excel(name = "血压监测")
    private String bloodPressureUrl;

    /**
     * 血糖监测
     */
    @ApiModelProperty(value = "血糖监测")
    @Length(max = 200, message = "血糖监测长度不能超过200")
    @TableField(value = "blood_glucose_url", condition = LIKE)
    @Excel(name = "血糖监测")
    private String bloodGlucoseUrl;

    /**
     * 复查提醒
     */
    @ApiModelProperty(value = "复查提醒")
    @Length(max = 200, message = "复查提醒长度不能超过200")
    @TableField(value = "review_reminder_url", condition = LIKE)
    @Excel(name = "复查提醒")
    private String reviewReminderUrl;

    /**
     * 健康日志
     */
    @ApiModelProperty(value = "健康日志")
    @Length(max = 200, message = "健康日志长度不能超过200")
    @TableField(value = "health_log_url", condition = LIKE)
    @Excel(name = "健康日志")
    private String healthLogUrl;

    /**
     * 学习计划
     */
    @ApiModelProperty(value = "学习计划")
    @Length(max = 200, message = "学习计划长度不能超过200")
    @TableField(value = "study_plan_url", condition = LIKE)
    @Excel(name = "学习计划")
    private String studyPlanUrl;

    /**
     * cms地址
     */
    @ApiModelProperty(value = "cms地址")
    @Length(max = 200, message = "cms地址长度不能超过200")
    @TableField(value = "cms_url", condition = EQUAL)
    @Excel(name = "cms地址")
    private String cmsUrl;


    /**
     * 营养食谱
     */
    @ApiModelProperty(value = "营养食谱")
    @Length(max = 200, message = "营养食谱长度不能超过200")
    @TableField(value = "nutrition_recipes_url", condition = LIKE)
    @Excel(name = "营养食谱")
    private String nutritionRecipesUrl;



    @ApiModelProperty(value = "关键字回复是否开启 1 开启， 2 关闭")
    @TableField(value = "key_word_status", condition = EQUAL)
    @Excel(name = "关键字开启")
    private Integer keyWordStatus;


    @ApiModelProperty(value = "自动回复是否开启 1 开启， 2 关闭")
    @TableField(value = "automatic_reply", condition = EQUAL)
    @Excel(name = "自动回复是否开启")
    private Integer automaticReply;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    @NotEmpty(message = "租户编号不能为空")
    @Length(max = 10, message = "租户编号长度不能超过10")
    @TableField(value = "tenant_code", condition = LIKE)
    private String tenantCode;
}
