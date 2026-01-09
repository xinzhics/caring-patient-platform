package com.caring.sass.msgs.entity;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE_RIGHT;

/**
 * @ClassName ChatUserNewMsg
 * @Description 存储医生收到的最新一条消息
 * @Author yangShuai
 * @Date 2020/12/23 16:42
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat_user_new")
@Builder
@ApiModel(value = "Chat", description = "医生专员的消息列表")
public class ChatUserNewMsg extends Entity<Long> {

    @ApiModelProperty(value = "患者", name = "patient_id", dataType = "String")
    @TableField(value = "patient_id", condition = EQUAL)
    protected Long patientId;

    @ApiModelProperty(value = "患者名字", name = "patientName", dataType = "String")
    @TableField(value = "patient_name", condition = LIKE_RIGHT)
    protected String patientName;

    @ApiModelProperty(value = "患者备注", name = "patientRemark", dataType = "String")
    @TableField(value = "patient_remark", condition = EQUAL)
    protected String patientRemark;

    @ApiModelProperty(value = "患者头像", name = "patientAvatar", dataType = "String")
    @TableField(value = "patient_avatar")
    private String patientAvatar;

    @ApiModelProperty(value = "chat对象的 receiverImAccount (患者的IM账号)", name = "receiverImAccount", dataType = "String")
    @TableField(value = "receiver_im_account", condition = EQUAL)
    protected String receiverImAccount;

    @ApiModelProperty(value = "接收者ID(医生或者专员)", name = "requestId", dataType = "String")
    @TableField(value = "request_id", condition = EQUAL)
    protected String requestId;

    @ApiModelProperty(value = "接收人角色(医生或者专员)", name = "requestRoleType", dataType = "String")
    @TableField(value = "request_role_type", condition = EQUAL)
    protected String requestRoleType;

    @ApiModelProperty(value = "最新的消息Id")
    @TableField(value = "chat_id", condition = EQUAL)
    protected Long chatId;

    @TableField(exist = false)
    private Chat chat;

    @ApiModelProperty(value = "未读消息数量")
    @TableField(exist = false)
    private Integer noReadTotal;

    @ApiModelProperty(value = "@我的记录")
    @TableField(exist = false)
    private List<ChatAtRecord> chatAtRecords;

    @ApiModelProperty(value = "专员是否退出聊天小组, 0 未退出，1 退出")
    @TableField(exist = false)
    private Integer nursingExitChat = 0;

    @ApiModelProperty(value = "医生是否退出聊天小组, 0 未退出，1 退出")
    @TableField(exist = false)
    private Integer doctorExitChat = 0;

    /**
     * 会员状态（0:关注  1：正常  2：取关 )
     * 会员状态（0:未注册  1：已注册  2：取关 )
     */
    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关)")
    @TableField(exist = false)
    private Integer status;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别，0男，1女")
    @TableField(exist = false)
    private Integer sex;

    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @TableField(exist = false)
    private String birthday;

    @ApiModelProperty(value = "诊断类型名称")
    @TableField(exist = false)
    private String diagnosisName;

    public void addChatAtRecords(ChatAtRecord chatAtRecord) {
        if (CollUtil.isEmpty(chatAtRecords)) {
            chatAtRecords = new ArrayList<>();
        }
        chatAtRecords.add(chatAtRecord);
    }
}
