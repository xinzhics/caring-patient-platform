package com.caring.sass.nursing.dto.traceInto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

@Data
public class PlanFormDTO {


    @ApiModelProperty("计划的ID")
    Long planId;

    @ApiModelProperty("计划的表单ID")
    Long formId;

    @ApiModelProperty("计划名称")
    String planName;

    Integer planType;

    @ApiModelProperty("已配置")
    Boolean setting;

    @ApiModelProperty("表单结果ID")
    Long formResultId;

    @ApiModelProperty("是否用药 0 不是， 1是")
    Integer medicationStatus;

    @ApiModelProperty(value = "开启用药推送跟踪 yes, 关闭用药推送跟踪 no, 其他计划的跟踪 穿null")
    private String medicinePush;

    @ApiModelProperty(value = "提醒时间(1天，2天，3天，5天，7天，其他)")
    private String reminderTime;

    @ApiModelProperty(value = "提醒天数(1,2,3,5,7)")
    private Integer reminderDay;

    @ApiModelProperty("表单结果创建时间")
    LocalDateTime createTime;

    @ApiModelProperty("字段")
    List<FormFieldInfo> fieldInfos;

}