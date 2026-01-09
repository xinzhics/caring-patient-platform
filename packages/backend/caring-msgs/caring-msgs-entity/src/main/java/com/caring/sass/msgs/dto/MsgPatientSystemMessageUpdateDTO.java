package com.caring.sass.msgs.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
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
 * 患者系统消息
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MsgPatientSystemMessageUpdateDTO", description = "患者系统消息")
public class MsgPatientSystemMessageUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)
     */
    @ApiModelProperty(value = "功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)")
    @Length(max = 255, message = "功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)长度不能超过255")
    private String functionType;
    /**
     * 点击消息跳转的地址
     */
    @ApiModelProperty(value = "点击消息跳转的地址")
    @Length(max = 255, message = "点击消息跳转的地址长度不能超过255")
    private String jumpUrl;
    /**
     * 跳转类型(网页链接)
     */
    @ApiModelProperty(value = "跳转类型(网页链接)")
    @Length(max = 255, message = "跳转类型(网页链接)长度不能超过255")
    private String jumpType;
    /**
     * 接收患者ID
     */
    @ApiModelProperty(value = "接收患者ID")
    @NotNull(message = "接收患者ID不能为空")
    private Long patientId;
    /**
     * 状态 1 已读， 0 未读
     */
    @ApiModelProperty(value = "状态 1 已读， 0 未读")
    private Integer readStatus;
    /**
     * 消息推送时间
     */
    @ApiModelProperty(value = "消息推送时间")
    private LocalDateTime pushTime;
    /**
     * 消息推送人
     */
    @ApiModelProperty(value = "消息推送人")
    @Length(max = 50, message = "消息推送人长度不能超过50")
    private String pushPerson;
    /**
     * 系统消息推送的主要文本内容
     */
    @ApiModelProperty(value = "系统消息推送的主要文本内容")
    @Length(max = 300, message = "系统消息推送的主要文本内容长度不能超过300")
    private String pushContent;
}
