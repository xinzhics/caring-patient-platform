package com.caring.sass.nursing.dto.unfinished;

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
 * 患者管理-未完成表单跟踪设置
 * </p>
 *
 * @author 杨帅
 * @since 2024-05-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "UnfinishedFormSettingUpdateDTO", description = "患者管理-未完成表单跟踪设置")
public class UnfinishedFormSettingUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    @Length(max = 200, message = "计划名称长度不能超过200")
    private String planName;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    private Long formId;
    /**
     * 提醒时间(1天，2天，3天，5天，7天，其他)
     */
    @ApiModelProperty(value = "提醒时间(1天，2天，3天，5天，7天，其他)")
    @Length(max = 40, message = "提醒时间(1天，2天，3天，5天，7天，其他)长度不能超过40")
    private String reminderTime;
    /**
     * 提醒天数(1,2,3,5,7)
     */
    @ApiModelProperty(value = "提醒天数(1,2,3,5,7)")
    private Integer reminderDay;
    /**
     * 用药推送
     */
    @ApiModelProperty(value = "开启用药推送跟踪 yes, 关闭用药推送跟踪 no, 其他计划的跟踪 穿null")
    private String medicinePush;

    @ApiModelProperty(value = "保持值不变，传回来")
    private Integer planType;
}
