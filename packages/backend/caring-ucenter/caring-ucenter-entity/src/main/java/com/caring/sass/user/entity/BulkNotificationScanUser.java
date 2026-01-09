package com.caring.sass.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @ClassName BulkNotification
 * @Description 群发通知
 * @Author yangShuai
 * @Date 2021/11/2 9:57
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_bulk_notification_scan_user")
@ApiModel(value = "BulkNotificationScanUser", description = "通知的扫码用户")
@AllArgsConstructor
public class BulkNotificationScanUser extends Entity<Long> {

    @ApiModelProperty(value = "openId")
    @TableField(value = "open_id")
    private String openId;

    @ApiModelProperty(value = "微信昵称")
    @TableField(value = "nick_name")
    private String nickname;

    @ApiModelProperty(value = "wxAppId")
    @TableField(value = "wx_app_id")
    private String wxAppId;


}
