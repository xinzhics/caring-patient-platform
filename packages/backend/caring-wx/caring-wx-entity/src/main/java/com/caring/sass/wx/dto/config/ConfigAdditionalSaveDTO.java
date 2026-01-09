package com.caring.sass.wx.dto.config;

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
 * 微信附加信息配置
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
@ApiModel(value = "ConfigAdditionalSaveDTO", description = "微信附加信息配置")
public class ConfigAdditionalSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公众号AppId
     */
    @ApiModelProperty(value = "公众号AppId")
    @Length(max = 32, message = "公众号AppId长度不能超过32")
    private String wxAppId;
    /**
     * 消息处理的回调地址
     */
    @ApiModelProperty(value = "消息处理的回调地址")
    @Length(max = 200, message = "消息处理的回调地址长度不能超过200")
    private String messageCallbackHandlerUrl;
    /**
     * 图像消息回调地址
     */
    @ApiModelProperty(value = "图像消息回调地址")
    @Length(max = 200, message = "图像消息回调地址长度不能超过200")
    private String imageMessageHandlerUrl;
    /**
     * 链接消息回调地址
     */
    @ApiModelProperty(value = "链接消息回调地址")
    @Length(max = 200, message = "链接消息回调地址长度不能超过200")
    private String linkMessageHandlerUrl;
    /**
     * 位置消息回调地址
     */
    @ApiModelProperty(value = "位置消息回调地址")
    @Length(max = 200, message = "位置消息回调地址长度不能超过200")
    private String locationMessageHandlerUrl;
    /**
     * 文本输入消息回调地址
     */
    @ApiModelProperty(value = "文本输入消息回调地址")
    @Length(max = 200, message = "文本输入消息回调地址长度不能超过200")
    private String textMessageHandlerUrl;
    /**
     * 发送视频消息回调地址
     */
    @ApiModelProperty(value = "发送视频消息回调地址")
    @Length(max = 200, message = "发送视频消息回调地址长度不能超过200")
    private String videoMessageHandlerUrl;
    /**
     * 发送语音消息回调地址
     */
    @ApiModelProperty(value = "发送语音消息回调地址")
    @Length(max = 200, message = "发送语音消息回调地址长度不能超过200")
    private String voiceMessageHandlerUrl;
    /**
     * 语音识别消息回调地址
     */
    @ApiModelProperty(value = "语音识别消息回调地址")
    @Length(max = 200, message = "语音识别消息回调地址长度不能超过200")
    private String voiceReconigeMessageHandlerUrl;
    /**
     * 订阅事件回调地址
     */
    @ApiModelProperty(value = "订阅事件回调地址")
    @Length(max = 200, message = "订阅事件回调地址长度不能超过200")
    private String subscribeEventHandlerUrl;
    /**
     * 点击事件回调地址
     */
    @ApiModelProperty(value = "点击事件回调地址")
    @Length(max = 200, message = "点击事件回调地址长度不能超过200")
    private String clickEventHandlerUrl;
    /**
     * 位置变更时间回调地址
     */
    @ApiModelProperty(value = "位置变更时间回调地址")
    @Length(max = 200, message = "位置变更时间回调地址长度不能超过200")
    private String locationEventHandlerUrl;
    /**
     * 扫码事件回调地址
     */
    @ApiModelProperty(value = "扫码事件回调地址")
    @Length(max = 200, message = "扫码事件回调地址长度不能超过200")
    private String scanEventHandlerUrl;
    /**
     * 模板任务完成事件回调地址
     */
    @ApiModelProperty(value = "模板任务完成事件回调地址")
    @Length(max = 200, message = "模板任务完成事件回调地址长度不能超过200")
    private String templateJobFinishEventHandlerUrl;
    /**
     * 取消订阅事件回调地址
     */
    @ApiModelProperty(value = "取消订阅事件回调地址")
    @Length(max = 200, message = "取消订阅事件回调地址长度不能超过200")
    private String unsubscribeEventHandlerUrl;
    /**
     * 观察事件回调地址
     */
    @ApiModelProperty(value = "观察事件回调地址")
    @Length(max = 200, message = "观察事件回调地址长度不能超过200")
    private String viewEventHandlerUrl;
    /**
     * 医生微信端登录url
     */
    @ApiModelProperty(value = "医生微信端登录url")
    @Length(max = 200, message = "医生微信端登录url长度不能超过200")
    private String doctorWeixinLoginUrl;
    /**
     * 患者打卡url
     */
    @ApiModelProperty(value = "患者打卡url")
    @Length(max = 255, message = "患者打卡url长度不能超过255")
    private String patientClockUrl;
    /**
     * 疼痛日志url
     */
    @ApiModelProperty(value = "疼痛日志url")
    @Length(max = 255, message = "疼痛日志url长度不能超过255")
    private String patientPainLogUrl;
    /**
     * 检验数据url
     */
    @ApiModelProperty(value = "检验数据url")
    @Length(max = 255, message = "检验数据url长度不能超过255")
    private String patientCheckDataUrl;
    /**
     * 预约跳转url
     */
    @ApiModelProperty(value = "预约跳转url")
    @Length(max = 255, message = "预约跳转url长度不能超过255")
    private String appointmentUrl;
    /**
     * 入组url
     */
    @ApiModelProperty(value = "入组url")
    @Length(max = 255, message = "入组url长度不能超过255")
    private String patientGroupUrl;
    /**
     * 医生进入聊天页url
     */
    @ApiModelProperty(value = "医生进入聊天页url")
    @Length(max = 255, message = "医生进入聊天页url长度不能超过255")
    private String doctorChat;
    /**
     * 患者进入聊天页url
     */
    @ApiModelProperty(value = "患者进入聊天页url")
    @Length(max = 255, message = "患者进入聊天页url长度不能超过255")
    private String patientChat;

    /**
     * 血压监测
     */
    @ApiModelProperty(value = "血压监测")
    @Length(max = 200, message = "血压监测长度不能超过200")
    private String bloodPressureUrl;
    /**
     * 血糖监测
     */
    @ApiModelProperty(value = "血糖监测")
    @Length(max = 200, message = "血糖监测长度不能超过200")
    private String bloodGlucoseUrl;
    /**
     * 复查提醒
     */
    @ApiModelProperty(value = "复查提醒")
    @Length(max = 200, message = "复查提醒长度不能超过200")
    private String reviewReminderUrl;
    /**
     * 健康日志
     */
    @ApiModelProperty(value = "健康日志")
    @Length(max = 200, message = "健康日志长度不能超过200")
    private String healthLogUrl;
    /**
     * 学习计划
     */
    @ApiModelProperty(value = "学习计划")
    @Length(max = 200, message = "学习计划长度不能超过200")
    private String studyPlanUrl;
    /**
     * 营养食谱
     */
    @ApiModelProperty(value = "营养食谱")
    @Length(max = 200, message = "营养食谱长度不能超过200")
    private String nutritionRecipesUrl;

    @ApiModelProperty(value = "租户编号")
    @NotEmpty(message = "租户编号不能为空")
    @Length(max = 10, message = "租户编号长度不能超过10")
    private String tenantCode;

}
