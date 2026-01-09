package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.MediaType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE_RIGHT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat")
@ApiModel(value = "Chat", description = "环信消息(三人组)")
public class Chat extends Entity<Long> {

    @ApiModelProperty(value = "发送者环信账号", name = "senderImAccount", dataType = "String")
    @TableField(value = "sender_im_account", condition = EQUAL)
    protected String senderImAccount;

    @ApiModelProperty(value = "发送者ID", name = "senderId", dataType = "String")
    @TableField(value = "sender_id", condition = EQUAL)
    protected String senderId;

    @ApiModelProperty(value = "发送者名字", name = "senderName", dataType = "String")
    @TableField(value = "sender_name", condition = EQUAL)
    protected String senderName;

    @ApiModelProperty(value = "发送者角色", name = "senderRoleType", dataType = "String")
    @TableField(value = "sender_role_type", condition = EQUAL)
    protected String senderRoleType;

    @ApiModelProperty(value = "发送者头像", name = "senderAvatar", dataType = "String")
    @TableField(value = "sender_avatar", condition = EQUAL)
    protected String senderAvatar;

    /**
     * 以患者的 im账号为中心建立的一个聊天组
     */
    @ApiModelProperty(value = "消息所属的群组ID(患者的IM账号)", name = "receiverImAccount", dataType = "String")
    @TableField(value = "receiver_im_account", condition = EQUAL)
    protected String receiverImAccount;

    @ApiModelProperty(value = "接收者ID", name = "receiverId", dataType = "String")
    @TableField(value = "receiver_id", condition = EQUAL)
    protected String receiverId;

    @Deprecated
    @ApiModelProperty(value = "接收者名字 ", name = "receiverName", dataType = "String")
    @TableField(value = "receiver_name", condition = EQUAL)
    protected String receiverName;

    @Deprecated
    @ApiModelProperty(value = "接受者头像", name = "receiverAvatar", dataType = "String")
    @TableField(value = "receiver_avatar", condition = EQUAL)
    protected String receiverAvatar;

    @Deprecated
    @ApiModelProperty(value = "接收人角色", name = "receiverRoleType", dataType = "String")
    @TableField(value = "receiver_role_type", condition = EQUAL)
    protected String receiverRoleType;

    /**
     * 弃用了
     */
    @ApiModelProperty(value = " 0:未读     1：已读", name = "status", dataType = "Integer")
    @TableField(value = "status", condition = EQUAL)
    protected Integer status;

    @ApiModelProperty(value = "内容", name = "content", dataType = "String")
    @TableField(value = "content", condition = LIKE_RIGHT)
    protected String content;

    @ApiModelProperty(value = "MediaType类型", name = "type", dataType = "String")
    @TableField(value = "type_", condition = EQUAL)
    protected MediaType type;


    @ApiModelProperty(value = "推荐功能", name = "recommendationFunction", dataType = "String")
    @TableField(value = "recommendation_function", condition = EQUAL)
    protected String recommendationFunction;

    @ApiModelProperty(value = "推荐功能的参数", name = "recommendationFunctionParams", dataType = "String")
    @TableField(value = "recommendation_function_params", condition = EQUAL)
    protected String recommendationFunctionParams;

    /**
     * 查询历史记录时。 患者是否可见。 1 可见 0 不可见 关注和取关消息是 0. 其他消息是1
     * 关注，取关的提醒对患者 不可见
     */
    @ApiModelProperty(value = "历史消息可见")
    @TableField(value = "history_visible", condition = EQUAL)
    private Integer historyVisible;

    @ApiModelProperty(value = "患者的诊断类型")
    @TableField(value = "diagnosis_name")
    private String diagnosisName;

    /**
     * 废弃了
     */
    @Deprecated
    @ApiModelProperty(value = "消息所属的小组Id")
    @TableField(value = "group_id")
    private Long groupId;

    @ApiModelProperty(value = "消息所属的医生Id")
    @TableField(value = "doctor_id")
    private Long doctorId;

    @ApiModelProperty("撤回状态 1: 为撤回。0 为正常显示")
    @TableField(value =  "withdraw_chat_status")
    private Integer withdrawChatStatus;

    @ApiModelProperty("撤回人员的ID， 这条消息的发送人id")
    @TableField(value = "withdraw_chat_user_id")
    private Long withdrawChatUserId;

    @ApiModelProperty("删除这条消息的人员的ID的json数组集合")
    @TableField(value = "delete_this_message_user_id_json_array_string")
    private String deleteThisMessageUserIdJsonArrayString;

    @ApiModelProperty(value = "消息所属的专员Id")
    @TableField(exist = false)
    private Long nursingId;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private String patientAvatar;

    @TableField(exist = false)
    private String patientId;

    // 专员对患者的备注
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty("医生对患者的备注")
    @TableField(exist = false)
    private String doctorRemark;

    @ApiModelProperty("租户")
    @TableField(exist = false)
    private String tenantCode;

    @ApiModelProperty("接收人的列表")
    @TableField(exist = false)
    private List<ChatSendRead> sendReads;

    @ApiModelProperty("被@的人员的信息（传人员的id， 角色）")
    @TableField(exist = false)
    private List<ChatAtRecord> chatAtRecords;

    @ApiModelProperty("1：系统消息。系统消息不产生未读记录。不更新 医生医助消息列表中最新消息。不发送环信提醒")
    @TableField(exist = false)
    private Integer systemMessage;

    @ApiModelProperty("注册的系统消息，需要更换医助医生的列表")
    @TableField(exist = false)
    private Integer registerMessage;

    @ApiModelProperty("医助医生的群发消息，群发消息不清楚发送人的未读记录。")
    @TableField(exist = false)
    private Integer groupMessage;

    @ApiModelProperty("是否是AI托管发送的消息")
    @TableField(value = "ai_hosted_send")
    private Boolean aiHostedSend;

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getCreateTime() {
        return super.getCreateTime();
    }

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getUpdateTime() {
        return super.getUpdateTime();
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public SuperEntity<Long> setId(Long id) {
        return super.setId(id);
    }
}
