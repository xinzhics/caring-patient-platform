package com.caring.sass.tenant.utils;

import com.caring.sass.base.R;
import com.caring.sass.cms.ChannelApi;
import com.caring.sass.nursing.api.*;
import com.caring.sass.nursing.dto.form.CopyFormDTO;
import com.caring.sass.nursing.dto.form.CopyPlanDTO;
import com.caring.sass.tenant.dao.TenantConfigurationScheduleMapper;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.LibraryTenantService;
import com.caring.sass.tenant.service.TenantConfigurationScheduleService;
import com.caring.sass.tenant.service.router.H5RouterService;
import com.caring.sass.tenant.service.router.H5UiService;
import com.caring.sass.tenant.service.router.PatientManageMenuService;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.TemplateMsgApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目复制工具
 * <p>
 * 1.cms 栏目、文章
 * 2.项目 apk打包配置
 * 3.微信 微信注册引导
 * 4.护理计划 表单、标签、标签属性、用药信息、模板管理、护理计划
 *
 * @author xinzh
 */
@Slf4j
@Component
public class TenantCopyUtil {

    private final ChannelApi channelApi;

    private final AppConfigService appConfigService;

    private final GuideApi guideApi;

    private final FormApi formApi;

    private final CustomDrugsCategoryApi customDrugsCategoryApi;

    private final CustomCommendDrugsApi customCommendDrugsApi;

    private final TagApi tagApi;

    private final TemplateMsgApi templateMsgApi;

    private final PlanApi planApi;

    private final H5UiService h5UiService;

    private final H5RouterService h5RouterService;

    private final StatisticsTaskApi statisticsTaskApi;

    private final LibraryTenantService libraryTenantService;

    private final TenantConfigurationScheduleService tenantConfigurationScheduleService;

    private final PatientManageMenuService patientManageMenuService;

    public TenantCopyUtil(ChannelApi channelApi, AppConfigService appConfigService,
                          GuideApi guideApi, FormApi formApi, CustomDrugsCategoryApi customDrugsCategoryApi,
                          CustomCommendDrugsApi customCommendDrugsApi, TemplateMsgApi templateMsgApi,
                          LibraryTenantService libraryTenantService,
                          PatientManageMenuService patientManageMenuService,
                          TenantConfigurationScheduleService tenantConfigurationScheduleService,
                          PlanApi planApi, TagApi tagApi, H5UiService h5UiService, H5RouterService h5RouterService,
                          StatisticsTaskApi statisticsTaskApi) {
        this.channelApi = channelApi;
        this.appConfigService = appConfigService;
        this.guideApi = guideApi;
        this.formApi = formApi;
        this.customDrugsCategoryApi = customDrugsCategoryApi;
        this.customCommendDrugsApi = customCommendDrugsApi;
        this.templateMsgApi = templateMsgApi;
        this.planApi = planApi;
        this.tagApi = tagApi;
        this.h5UiService = h5UiService;
        this.h5RouterService = h5RouterService;
        this.patientManageMenuService = patientManageMenuService;
        this.tenantConfigurationScheduleService = tenantConfigurationScheduleService;
        this.libraryTenantService = libraryTenantService;
        this.statisticsTaskApi = statisticsTaskApi;
    }

    public R<Boolean> copy(String fromTenantCode, String toTenantCode) {
        // 复制 项目的 患者端，医生端 H5Router 和 H5UI
        try {
            h5UiService.copy(fromTenantCode, toTenantCode);
            h5RouterService.copy(fromTenantCode, toTenantCode);
            libraryTenantService.copyLibraryTenant(fromTenantCode, toTenantCode);
            appConfigService.copyAppConfig(fromTenantCode, toTenantCode);

        } catch (Exception e) {
            log.error("copy tenant info error {} {} ", fromTenantCode, toTenantCode);
        }


        // 初始化项目基本信息，获取项目编码
        try {
            channelApi.copyChannelAndChannelContent(fromTenantCode, toTenantCode);
        } catch (Exception e) {
            log.error("copy cms channel info error {} {} ", fromTenantCode, toTenantCode);
        }
        try {
            guideApi.copyRegGuide(fromTenantCode, toTenantCode);
        } catch (Exception e) {
            log.error("copy wx regGuide info error {} {} ", fromTenantCode, toTenantCode);
        }

        Map<Long, Long> templateIdMaps = new HashMap<>();
        R<Map<Long, Long>> templateIdMapsR = templateMsgApi.copyTemplateMsgAndFields(fromTenantCode, toTenantCode);
        if (templateIdMapsR.getIsSuccess()) {
            templateIdMaps = templateIdMapsR.getData();
        }

        try {
            // 复制护理计划，并返回新旧护理关联id
            CopyPlanDTO copyPlanDTO = new CopyPlanDTO().setFromTenantCode(fromTenantCode)
                    .setToTenantCode(toTenantCode).setTemplateIdMaps(templateIdMaps);
            R<Map<Long, Long>> planIdMapsR = planApi.copyPlan(copyPlanDTO);
            if (planIdMapsR.getIsSuccess()) {
                Map<Long, Long> planIdMaps = planIdMapsR.getData();
                CopyFormDTO co = new CopyFormDTO().setFromTenantCode(fromTenantCode)
                        .setToTenantCode(toTenantCode).setPlanIdMaps(planIdMaps);
                formApi.copyForm(co);
            }
        } catch (Exception e) {
            log.error("copy nursing plan form error {} {} ", fromTenantCode, toTenantCode);
        }

        try {
            customCommendDrugsApi.copyRecommendDrugs(fromTenantCode, toTenantCode);
            customDrugsCategoryApi.copyDrugsCategory(fromTenantCode, toTenantCode);
            tagApi.copyTag(fromTenantCode, toTenantCode);
            statisticsTaskApi.copyTask(fromTenantCode, toTenantCode);
        } catch (Exception e) {
            log.error("copy nursing drug tag statisticsTask info error {} {} ", fromTenantCode, toTenantCode);

        }
        patientManageMenuService.copy(fromTenantCode, toTenantCode);
        tenantConfigurationScheduleService.copy(fromTenantCode, toTenantCode);

        return R.success();
    }
}