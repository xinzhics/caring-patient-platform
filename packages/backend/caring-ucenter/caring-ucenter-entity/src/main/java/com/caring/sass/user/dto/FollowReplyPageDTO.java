package com.caring.sass.user.dto;

import java.time.LocalDateTime;
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
@ApiModel(value = "FollowReplyPageDTO", description = "关注后回复和关注未注册回复")
public class FollowReplyPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 功能开关(open, close)
     */
    @ApiModelProperty(value = "功能开关(open, close)")
    @Length(max = 255, message = "功能开关(open, close)长度不能超过255")
    private String functionSwitch;
    /**
     * 回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )
     */
    @ApiModelProperty(value = "回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )")
    @Length(max = 255, message = "回复类别(Reply_after_following 关注后,  unregistered_reply 未注册回复 )长度不能超过255")
    private String replyCategory;
    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    @Length(max = 600, message = "回复内容长度不能超过600")
    private String replyContent;
    /**
     * 回复类型(text,cms)
     */
    @ApiModelProperty(value = "回复类型(text,cms)")
    @Length(max = 255, message = "回复类型(text,cms)长度不能超过255")
    private String replyType;
    /**
     * 回复形式(system，medical_assistance 医助)
     */
    @ApiModelProperty(value = "回复形式(system，medical_assistance 医助)")
    @Length(max = 255, message = "回复形式(system，medical_assistance 医助)长度不能超过255")
    private String replyForm;
    /**
     * 回复的时间
     */
    @ApiModelProperty(value = "回复的时间")
    private LocalTime replyTime;

}
