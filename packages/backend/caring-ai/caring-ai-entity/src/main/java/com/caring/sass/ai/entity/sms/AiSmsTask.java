package com.caring.sass.ai.entity.sms;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * AI短信验证码表
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-12
 */
@Data
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_sms_task")
@ApiModel(value = "AiSmsTask", description = "AI短信验证码表")
@AllArgsConstructor
public class AiSmsTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    @Length(max = 10, message = "长度不能超过10")
    @TableField(value = "status", condition = LIKE)
    @Excel(name = "")
    private String status;

    /**
     * 接收者手机号
     */
    @ApiModelProperty(value = "接收者手机号")
    @Length(max = 20, message = "接收者手机号长度不能超过20")
    @TableField(value = "receiver", condition = LIKE)
    @Excel(name = "接收者手机号")
    private String receiver;

    @ApiModelProperty(value = "")
    @Length(max = 500, message = "长度不能超过500")
    @TableField(value = "template_params", condition = LIKE)
    @Excel(name = "")
    private String templateParams;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间")
    @TableField("send_time")
    @Excel(name = "发送时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "")
    @Length(max = 500, message = "长度不能超过500")
    @TableField(value = "content", condition = LIKE)
    @Excel(name = "")
    private String content;

    @ApiModelProperty(value = "回调ID")
    @Length(max = 500, message = "长度不能超过500")
    @TableField(value = "biz_id", condition = EQUAL)
    private String bizId;




}
