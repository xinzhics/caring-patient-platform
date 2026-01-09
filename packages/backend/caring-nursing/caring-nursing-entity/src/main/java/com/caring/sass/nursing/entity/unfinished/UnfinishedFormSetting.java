package com.caring.sass.nursing.entity.unfinished;

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
 * 患者管理-未完成表单跟踪设置
 * </p>
 *
 * @author 杨帅
 * @since 2024-05-27
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_unfinished_form_setting")
@ApiModel(value = "UnfinishedFormSetting", description = "患者管理-未完成表单跟踪设置")
@AllArgsConstructor
public class UnfinishedFormSetting extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final String MEDICINE_PUSH_NO = "no";

    public static final String MEDICINE_PUSH_YES = "yes";

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    @TableField("plan_id")
    @Excel(name = "计划ID")
    private Long planId;

    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    @Length(max = 200, message = "计划名称长度不能超过200")
    @TableField(value = "plan_name", condition = LIKE)
    @Excel(name = "计划名称")
    private String planName;

    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 提醒时间(1天，2天，3天，5天，7天，其他)
     */
    @ApiModelProperty(value = "提醒时间(1天，2天，3天，5天，7天，其他)")
    @Length(max = 40, message = "提醒时间(1天，2天，3天，5天，7天，其他)长度不能超过40")
    @TableField(value = "reminder_time", condition = LIKE)
    @Excel(name = "提醒时间(1天，2天，3天，5天，7天，其他)")
    private String reminderTime;

    /**
     * 提醒天数(1,2,3,5,7)
     */
    @ApiModelProperty(value = "提醒天数(1,2,3,5,7)")
    @TableField("reminder_day")
    @Excel(name = "提醒天数(1,2,3,5,7)")
    private Integer reminderDay;

    /**
     * 用药推送
     */
    @ApiModelProperty(value = "开启用药推送跟踪 yes, 关闭用药推送跟踪 no, 其他计划的跟踪 穿null")
    @TableField(value = "medicine_push", condition = EQUAL)
    @Excel(name = "用药推送")
    private String medicinePush;


    @ApiModelProperty(value = "计划类型")
    @TableField(value = "plan_type", condition = EQUAL)
    private Integer planType;



}
