package com.caring.sass.tenant.dto;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.tenant.dto.router.H5RouterPatientDto;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.entity.router.H5Ui;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 返回给患者微信前端的项目信息。包括了 H5router和H5UI 和 注册引导
 */
@Data
public class WxTenantInfoDto extends Tenant {

    private List<H5Router> routerData;

    @ApiModelProperty(name = "患者个人中心的整体设置")
    private H5RouterPatientDto routerPatientDto;

    private Map<String, H5Ui> styleDate;

    private JSONObject regGuide;

    @ApiModelProperty(name = "患者基本信息表单填写方式 1 一题一页， 2 或 null 表单")
    private Integer patientCompleteFormStatus;
}
