package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.msgs.enumeration.ChatGroupAssociationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @className: ChatGroupSendObject
 * @author: 杨帅
 * @date: 2024/1/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat_group_send_object")
@ApiModel(value = "ChatGroupSendObject", description = "群发消息对象")
public class ChatGroupSendObject extends Entity<Long> {


    @ApiModelProperty(value = "群发消息ID")
    @TableField(value = "chat_group_send_id", condition = EQUAL)
    private Long chatGroupSendId;

    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;

    @ApiModelProperty(value = "用户角色")
    @TableField(value = "user_role", condition = EQUAL)
    private String userRole;

    @ApiModelProperty(value = "群发对象关联ID")
    @TableField(value = "object_association_id", condition = EQUAL)
    private String objectAssociationId;

    @ApiModelProperty(value = "群发对象关联冗余name")
    @TableField(value = "object_association_name", condition = EQUAL)
    private String objectAssociationName;

    @ApiModelProperty(value = "群发对象关联类型")
    @TableField(value = "object_association_type", condition = EQUAL)
    private ChatGroupAssociationType objectAssociationType;


}
