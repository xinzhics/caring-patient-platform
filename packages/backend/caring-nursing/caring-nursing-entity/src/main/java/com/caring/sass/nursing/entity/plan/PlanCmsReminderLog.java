package com.caring.sass.nursing.entity.plan;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName PlanCmsReminderLog
 * @Description
 * @Author yangShuai
 * @Date 2021/12/20 9:26
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_plan_cms_reminder_log")
@ApiModel(value = "PlanCmsReminderLog", description = "cms推送记录")
@AllArgsConstructor
public class PlanCmsReminderLog extends Entity<Long> {


    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;

    @ApiModelProperty(value = "用户类型 patient doctor")
    @TableField(value = "user_type", condition = EQUAL)
    private String userType;

    @ApiModelProperty(value = "文章的缩略图")
    @TableField(value = "c_icon")
    private String icon;

    @ApiModelProperty(value = "文章的标题")
    @TableField(value = "cms_title", condition = LIKE)
    private String cmsTitle;

    @ApiModelProperty(value = "文章的ID")
    @TableField(value = "cms_id")
    private Long cmsId;

    @ApiModelProperty(value = "文章的链接")
    @TableField(value = "cms_link")
    private String cmsLink;

    @ApiModelProperty(value = "发送时间")
    @TableField(value = "send_time")
    private LocalDateTime sendTime;

}
