package com.caring.sass.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.user.dao.DoctorAuditMapper;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.DoctorAudit;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorAuditService;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName DoctorAuditServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/2/22 14:07
 * @Version 1.0
 */
@Service
@Slf4j
public class DoctorAuditServiceImpl extends SuperServiceImpl<DoctorAuditMapper, DoctorAudit> implements DoctorAuditService {

    @Autowired
    WeiXinService weiXinService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    TenantApi tenantApi;


    @Override
    public void desensitization(List<DoctorAudit> records) {

        if (records.isEmpty()) {
            return;
        }

        String userType = BaseContextHandler.getUserType();
        if (UserType.ADMIN.equals(userType)) {
            return;
        }

        String tenant = BaseContextHandler.getTenant();
        R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
        if (securitySettings.getIsSuccess()) {
            Boolean data = securitySettings.getData();
            if (data == null || !data) {
                return;
            }
        } else {
            return;
        }

        for (DoctorAudit record : records) {
            // 对 换着的 姓名。 联系方式 脱敏
            record.setName(SensitiveInfoUtils.desensitizeName(record.getName()));
            record.setMobile(SensitiveInfoUtils.desensitizePhone(record.getMobile()));
        }
    }


    @Override
    public boolean updateById(DoctorAudit model) {

        // 发送 微信公众号 提醒模板
        boolean update = super.updateById(model);
        if ("pass_through".equals(model.getAuditStatus())) {
            DoctorAudit audit = getById(model.getId());

            JSONObject extraInfo = new JSONObject();
            extraInfo.put("Specialties", audit.getSpecialties());
            extraInfo.put("Introduction", audit.getIntroduction());
            // 根据审核后的信息，创建一个医生
            Doctor.DoctorBuilder builder = Doctor.builder();
            Doctor doctor = builder.avatar(audit.getAvatar())
                    .name(audit.getName())
                    .nickName(audit.getNickName())
                    .wxAppId(audit.getWxAppId())
                    .nursingId(audit.getNursingId())
                    .nursingName(audit.getNursingName())
                    .mobile(audit.getMobile())
                    .hospitalId(audit.getHospitalId())
                    .hospitalName(audit.getHospitalName())
                    .deptartmentName(audit.getDeptartmentName())
                    .title(audit.getTitle())
                    .extraInfo(extraInfo.toJSONString())
                    .wxStatus(0)
                    .build();
            if (StrUtil.isNotBlank(audit.getPassword())) {
                doctor.setPasswordUpdated(CommonStatus.YES);
                doctor.setPassword(audit.getPassword());
            } else {
                String passwordMd5 = SecureUtil.md5("doctor" + audit.getMobile().substring(audit.getMobile().length() - 4));
                doctor.setPassword(passwordMd5);
                doctor.setPasswordUpdated(CommonStatus.NO);
            }
            doctorService.save(doctor);

            if (StrUtil.isNotEmpty(audit.getOpenId())) {
                // 发送微信公众号信息
                weiXinService.sendDoctorAuditTemplateMessage(audit.getWxAppId(), audit.getName(), TemplateMessageIndefiner.CONSULTATION_RESPONSE, audit.getOpenId());
            }
        }
        return update;

    }


}
