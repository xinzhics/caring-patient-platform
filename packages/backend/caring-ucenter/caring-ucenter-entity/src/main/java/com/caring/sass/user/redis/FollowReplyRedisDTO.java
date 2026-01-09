package com.caring.sass.user.redis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalTime;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FollowReplyRedisDTO", description = "关注后回复和关注未注册回复redis存储")
public class FollowReplyRedisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 功能开关(open, close)
     */
    @ApiModelProperty(value = "功能开关(open, close)")
    private String functionSwitch;
    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    private String replyContent;
    /**
     * 回复类型(text,cms)
     */
    @ApiModelProperty(value = "回复类型(text,cms)")
    private String replyType;
    /**
     * 回复形式(system，medical_assistance 医助)
     */
    @ApiModelProperty(value = "回复形式(system，medical_assistance 医助)")
    private String replyForm;

    @ApiModelProperty(value = "触发条件 0 当天 正数 后N天。 负数是前N天")
    private Integer triggeringConditions;

    @ApiModelProperty(value = "回复的时间")
    private LocalTime replyTime;

}
