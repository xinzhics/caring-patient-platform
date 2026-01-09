package com.caring.sass.nursing.service.task.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.service.drugs.PatientDrugsService;
import com.caring.sass.nursing.service.task.SuperTask;
import com.caring.sass.nursing.service.task.TemplateMessageHelper;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.guide.RegGuide;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 推送购药提醒
 *
 * @author leizhi
 */
@Slf4j
@Service
public class PushBuyDrugsTask extends SuperTask {

    private final PatientDrugsService patientDrugsService;

    private final PatientApi patientApi;

    private final GuideApi guideApi;

    private final WeiXinApi weiXinApi;

    public PushBuyDrugsTask(PatientDrugsService patientDrugsService, PatientApi patientApi, GuideApi guideApi, WeiXinApi weiXinApi) {
        this.patientDrugsService = patientDrugsService;
        this.patientApi = patientApi;
        this.guideApi = guideApi;
        this.weiXinApi = weiXinApi;
    }

    public void execute(LocalDateTime now) {
        List<Tenant> allNormalTenant = getAllNormalTenant();
        for (Tenant tenant : allNormalTenant) {
            BaseContextHandler.setTenant(tenant.getCode());

            // 校验项目是否关闭购药推送
            R<RegGuide> guideR = guideApi.getGuide();
            if (guideR.getIsError() || Objects.isNull(guideR.getData())) {
                continue;
            }
            boolean closePush = Objects.equals(guideR.getData().getBuyDrugsUrlSwitch(), 1);
            if (closePush) {
                continue;
            }

            // 查询患者用药记录
            // 直接查询 当前时间需要推送的 患者
            LbqWrapper<PatientDrugs> patientDrugsLbqWrapper = Wraps.<PatientDrugs>lbQ()
                    .eq(PatientDrugs::getBuyDrugsReminderTime, LocalDate.now());
            List<PatientDrugs> patientDrugs = patientDrugsService.list(patientDrugsLbqWrapper);
            if (CollUtil.isEmpty(patientDrugs)) {
                continue;
            }

            for (PatientDrugs patientDrug : patientDrugs) {
                // 校验时间是否是当天
//                LocalDate buyDrugsReminderDate = patientDrug.getBuyDrugsReminderTime();
//                if (!whetherPushNow(buyDrugsReminderDate)) {
//                    continue;
//                }

                R<Patient> patientR = patientApi.get(patientDrug.getPatientId());
                // 推送购药提醒
                sendBuyDrugsReminder(now, patientR.getData(), patientDrug.getDrugsId());
            }
        }
    }

    /**
     * 是否推送 当天推送，不是当天不推送
     *
     * @param buyDrugsReminderDate 用药提醒日期
     * @return true推送、false不推送
     */
//    boolean whetherPushNow(LocalDate buyDrugsReminderDate) {
//        return Objects.equals(buyDrugsReminderDate, LocalDate.now());
//    }

    /**
     * 发送用药提醒
     *
     * @param buyTime 购药时间
     * @param patient 患者
     */
    public void sendBuyDrugsReminder(LocalDateTime buyTime, Patient patient, Long drugsId) {
        TemplateMsgDto t = TemplateMessageHelper.getTemplateMsgDto(TemplateMessageIndefiner.BUY_DRUGS_REMINDER);
        if (Objects.isNull(t)) {
            log.error("微信未配置购药提醒模板消息");
            return;
        }

        R<Tenant> tR = tenantApi.getByCode(BaseContextHandler.getTenant());
        Tenant tenant = tR.getData();
        boolean b = tR.getIsError() || Objects.isNull(tenant);
        if (b) {
            log.error("查询项目详细失败");
            return;
        }
        List<WxMpTemplateData> templateData = new ArrayList<>();
        // first 字段配置颜色属性 微信推送不生效
        if (t.getCommonCategory()) {
            templateData = CommonTemplateServiceWorkModel.buildWxMpTemplateData(patient.getName(), I18nUtils.getMessage(CommonTemplateServiceWorkModel.MEDICATION_WARNING_REMINDER));
        } else {
            templateData.add(new WxMpTemplateData("first", "您的药品快用完了，请及时补充，切勿延误治疗哦"));
            templateData.add(new WxMpTemplateData("keyword1", patient.getName()));
            templateData.add(new WxMpTemplateData("keyword2", DateUtil.format(buyTime, "yyyy年MM月dd日 HH时mm分")));
            templateData.add(new WxMpTemplateData("keyword3", "查看详情"));
        }
        String url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.BUY_DRUGS_URL, drugsId));
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage(
                patient.getOpenId(), t.getTemplateId(), url, null, null, templateData
        );
        SendTemplateMessageForm to = new SendTemplateMessageForm();
        to.setWxAppId(patient.getWxAppId());
        to.setTemplateMessage(wxMpTemplateMessage);
        R<String> message = weiXinApi.sendTemplateMessage(to);
        if (message.getIsError()) {
            log.error("推送购药提醒失败:{}", message.getData());
        }
    }


}
