package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 关键字规则触发日期
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
@TableName("u_user_keyword_reply_trigger_record")
@ApiModel(value = "KeywordReplyTriggerRecord", description = "关键字规则触发日期")
@AllArgsConstructor
public class KeywordReplyTriggerRecord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 关键字ID
     */
    @ApiModelProperty(value = "关键字回复规则ID")
    @TableField("keyword_reply_id")
    private Long keywordReplyId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;


    @ApiModelProperty(value = "触发日期")
    @TableField("trigger_date")
    private LocalDate triggerDate;

    @ApiModelProperty(value = "触发时间")
    @TableField("trigger_date_time")
    private LocalDateTime triggerDateTime;


}
