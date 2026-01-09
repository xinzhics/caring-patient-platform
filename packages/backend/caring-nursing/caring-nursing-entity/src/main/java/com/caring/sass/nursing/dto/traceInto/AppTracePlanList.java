package com.caring.sass.nursing.dto.traceInto;

import com.caring.sass.nursing.enumeration.PlanEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: AppTracePlanList
 * @author: 杨帅
 * app 异常选项跟踪 - 随访计划列表页面
 * @date: 2023/8/10
 */
@Data
@Builder
@AllArgsConstructor
public class AppTracePlanList {


    @ApiModelProperty("随访计划ID")
    private Long planId;

    @ApiModelProperty("表单id")
    private Long formId;

    @ApiModelProperty("随访计划名称")
    private String planName;

    @ApiModelProperty("未完成跟踪配置ID， 查询必传条件")
    private Long unFinishedSettingId;

    @ApiModelProperty("计划类型 复查提醒 3， 健康日志 5， 用药提醒 4，  自定义护理计划 null")
    private PlanEnum planType;

    @ApiModelProperty("未处理患者数量统计")
    private Integer noHandlePatientNumber;

    @ApiModelProperty("已处理患者数量")
    private Integer handleNumber;

    @ApiModelProperty("未处理患者数量")
    private Integer noHandleNumber;


    public AppTracePlanList() {
    }

    public AppTracePlanList(Long formId, Long planId, String name, PlanEnum planEnum, Integer count) {
        this.setFormId(formId);
        this.setPlanId(planId);
        this.setPlanName(name);
        this.setPlanType(planEnum);
        this.setNoHandlePatientNumber(count);
    }
}
