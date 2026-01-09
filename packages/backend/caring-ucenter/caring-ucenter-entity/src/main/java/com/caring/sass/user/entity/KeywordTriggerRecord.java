package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 关键字触发日期
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
@TableName("u_user_keyword_trigger_record")
@ApiModel(value = "KeywordTriggerRecord", description = "关键字触发日期")
@AllArgsConstructor
public class KeywordTriggerRecord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 关键字ID
     */
    @ApiModelProperty(value = "关键字ID")
    @TableField("keyword_id")
    @Excel(name = "关键字ID")
    private Long keywordId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 触发日期
     */
    @ApiModelProperty(value = "触发日期")
    @TableField("trigger_date")
    private LocalDate triggerDate;

    @ApiModelProperty(value = "触发时间")
    @TableField("trigger_date_time")
    private LocalDateTime triggerDateTime;


}
