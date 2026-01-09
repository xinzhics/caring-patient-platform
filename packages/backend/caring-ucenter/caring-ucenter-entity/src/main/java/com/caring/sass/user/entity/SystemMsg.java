package com.caring.sass.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName SystemMsg
 * @Description
 * @Author yangShuai
 * @Date 2020/12/4 10:22
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_system_msg")
@ApiModel(value = "SystemMsg", description = "用户-系统消息")
@AllArgsConstructor
public class SystemMsg extends Entity<Long> {


    @ApiModelProperty(value = "通知内容")
    @TableField(value = "content", condition = EQUAL)
    String content;

    @ApiModelProperty(value = "消息状态 0 未读 1 已读")
    @TableField(value = "read_status", condition = EQUAL)
    Integer readStatus;

    @ApiModelProperty(value = "消息类型 subscribe, complete, nosubscribe, consultation")
    @TableField(value = "msg_type", condition = EQUAL)
    String msgType;

    @ApiModelProperty(value = "医助Id")
    @TableField(value = "user_id", condition = EQUAL)
    Long userId;

    @ApiModelProperty(value = "角色默认医助, 医生 doctor")
    @TableField(value = "user_role", condition = EQUAL)
    String userRole;


}
