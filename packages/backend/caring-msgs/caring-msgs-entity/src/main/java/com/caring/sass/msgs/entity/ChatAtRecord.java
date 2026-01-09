package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName ChatAtRecord
 * @Description
 * @Author yangShuai
 * @Date 2021/12/2 9:35
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat_at_record")
@ApiModel(value = "ChatAtRecord", description = "消息中被@的记录")
public class ChatAtRecord extends Entity<Long> {

    @ApiModelProperty(value = "消息ID")
    @TableField(value = "chat_id", condition = EQUAL)
    private Long chatId;

    @ApiModelProperty(value = "消息所属的群组ID(患者的IM账号)", name = "receiverImAccount", dataType = "String")
    @TableField(value = "receiver_im_account", condition = EQUAL)
    private String receiverImAccount;

    @ApiModelProperty(value = "所属消息记录列表的ID", name = "receiverImAccount", dataType = "String")
    @TableField(value = "chat_user_new_msg_id", condition = EQUAL)
    private Long chatUserNewMsgId;

    @ApiModelProperty(value = "被at人员的ID")
    @TableField(value = "at_user_id", condition = EQUAL)
    private Long atUserId;

    @ApiModelProperty(value = "被at人员的角色")
    @TableField(value = "user_type", condition = EQUAL)
    private String userType;



}
