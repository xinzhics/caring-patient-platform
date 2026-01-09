package com.caring.sass.ai.dto.call;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 分身通话记录
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CallRecordSaveDTO", description = "分身通话记录")
public class CallRecordSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    private String userMobile;
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @Length(max = 255, message = "用户头像长度不能超过255")
    private String userAvatar;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @Length(max = 255, message = "用户昵称长度不能超过255")
    private String userName;
    /**
     * 拨号用户id
     */
    @ApiModelProperty(value = "拨号用户id")
    private Long dialUserId;
    /**
     * 拨号用户id
     */
    @ApiModelProperty(value = "接通用户id")
    private Long bloggerUserId;
    /**
     * 通话开始时间
     */
    @ApiModelProperty(value = "通话开始时间")
    private LocalDateTime callStartTime;
    /**
     * 通话最后更新时间
     */
    @ApiModelProperty(value = "通话最后更新时间")
    private LocalDateTime callLastUpdatedTime;
    /**
     * 通话结束时间
     */
    @ApiModelProperty(value = "通话结束时间")
    private LocalDateTime callEndTime;
    /**
     * 通话时长
     */
    @ApiModelProperty(value = "通话时长")
    private Integer callDuration;

}
