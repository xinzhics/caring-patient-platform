package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 关注后回复和关注未注册回复
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-06
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_follow_reply")
@ApiModel(value = "FollowReply", description = "关注后回复和关注未注册回复")
@AllArgsConstructor
public class FollowReply extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 功能开关(open, close)
     */
    @ApiModelProperty(value = "功能开关(open, close)")
    @Length(max = 255, message = "功能开关(open, close)长度不能超过255")
    @TableField(value = "reply_switch", condition = LIKE)
    @Excel(name = "功能开关(open, close)")
    private String functionSwitch;

    /**
     * 回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )
     */
    @ApiModelProperty(value = "回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )")
    @Length(max = 255, message = "回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )长度不能超过255")
    @TableField(value = "reply_category", condition = EQUAL)
    @Excel(name = "回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )")
    private String replyCategory;

    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    @Length(max = 600, message = "回复内容长度不能超过600")
    @TableField(value = "reply_content", condition = LIKE)
    @Excel(name = "回复内容")
    private String replyContent;

    /**
     * 回复类型(text,cms)
     */
    @ApiModelProperty(value = "回复类型(text,cms)")
    @Length(max = 255, message = "回复类型(text,cms)长度不能超过255")
    @TableField(value = "reply_type", condition = LIKE)
    @Excel(name = "回复类型(text,cms)")
    private String replyType;

    /**
     * 回复形式(system，medical_assistance 医助)
     */
    @ApiModelProperty(value = "回复形式(system，medical_assistance 医助)")
    @Length(max = 255, message = "回复形式(system，medical_assistance 医助)长度不能超过255")
    @TableField(value = "reply_form", condition = LIKE)
    @Excel(name = "回复形式(system，medical_assistance 医助)")
    private String replyForm;

    @ApiModelProperty(value = "触发条件 0 当天 正数 后N天。 负数是前N天")
    @TableField("triggering_conditions")
    private Integer triggeringConditions;

    /**
     * 回复的时间
     */
    @ApiModelProperty(value = "回复的时间")
    @TableField("reply_time")
    @Excel(name = "回复的时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalTime replyTime;

    @ApiModelProperty(value = "租户")
    @TableField("tenant_code")
    private String tenantCode;

}
