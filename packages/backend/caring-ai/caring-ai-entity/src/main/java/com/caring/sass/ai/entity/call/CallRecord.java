package com.caring.sass.ai.entity.call;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_call_record", autoResultMap = true)
@ApiModel(value = "CallRecord", description = "分身通话记录")
@AllArgsConstructor
public class CallRecord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户手机号")
    private String userMobile;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @Length(max = 255, message = "用户头像长度不能超过255")
    @TableField(value = "user_avatar", condition = LIKE)
    @Excel(name = "用户头像")
    private String userAvatar;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @Length(max = 255, message = "用户昵称长度不能超过255")
    @TableField(value = "user_name", condition = LIKE)
    @Excel(name = "用户昵称")
    private String userName;

    /**
     * 拨号用户id
     */
    @ApiModelProperty(value = "拨号用户id")
    @TableField("dial_user_id")
    @Excel(name = "拨号用户id")
    private Long dialUserId;

    /**
     * 拨号用户id
     */
    @ApiModelProperty(value = "拨号用户id")
    @TableField("blogger_user_id")
    @Excel(name = "拨号用户id")
    private Long bloggerUserId;

    /**
     * 通话开始时间
     */
    @ApiModelProperty(value = "通话开始时间")
    @TableField("call_start_time")
    @Excel(name = "通话开始时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime callStartTime;

    /**
     * 通话最后更新时间
     */
    @ApiModelProperty(value = "通话最后更新时间")
    @TableField("call_last_updated_time")
    @Excel(name = "通话最后更新时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime callLastUpdatedTime;

    /**
     * 通话结束时间
     */
    @ApiModelProperty(value = "通话结束时间")
    @TableField("call_end_time")
    @Excel(name = "通话结束时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime callEndTime;

    /**
     * 通话时长
     */
    @ApiModelProperty(value = "通话时长")
    @TableField("call_duration")
    @Excel(name = "通话时长")
    private Integer callDuration;


    @Builder
    public CallRecord(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String userMobile, String userAvatar, String userName, Long dialUserId, Long bloggerUserId, 
                    LocalDateTime callStartTime, LocalDateTime callLastUpdatedTime, LocalDateTime callEndTime, Integer callDuration) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userMobile = userMobile;
        this.userAvatar = userAvatar;
        this.userName = userName;
        this.dialUserId = dialUserId;
        this.bloggerUserId = bloggerUserId;
        this.callStartTime = callStartTime;
        this.callLastUpdatedTime = callLastUpdatedTime;
        this.callEndTime = callEndTime;
        this.callDuration = callDuration;
    }

}
