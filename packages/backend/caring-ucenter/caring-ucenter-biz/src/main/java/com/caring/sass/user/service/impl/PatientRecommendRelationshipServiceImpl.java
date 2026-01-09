package com.caring.sass.user.service.impl;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.user.constant.QrDoctorTypeEnum;
import com.caring.sass.user.dao.PatientRecommendRelationshipMapper;
import com.caring.sass.user.dto.GeneratePatientInvitationQRcodeDTO;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.entity.PatientRecommendRelationship;
import com.caring.sass.user.entity.PatientRecommendSetting;
import com.caring.sass.user.service.PatientRecommendRelationshipService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.user.service.PatientRecommendSettingService;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.CreateFollowerPermanentQrCode;
import com.caring.sass.wx.dto.config.QrCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 运营配置-患者推荐关系
 * </p>
 *
 * @author lixiang
 * @date 2022-07-14
 */
@Slf4j
@Service

public class PatientRecommendRelationshipServiceImpl extends SuperServiceImpl<PatientRecommendRelationshipMapper, PatientRecommendRelationship> implements PatientRecommendRelationshipService {
    @Autowired
    private WeiXinApi weiXinApi;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PatientRecommendSettingService patientRecommendSettingService;
    /**
     * 推荐人生成邀请二维码
     * @param patientId
     * @return
     */
    @Override
    public GeneratePatientInvitationQRcodeDTO generatePatientInvitationQRcode(Long patientId) {
        String tenantCode = BaseContextHandler.getTenant();
        BaseContextHandler.setTenant(tenantCode);
        GeneratePatientInvitationQRcodeDTO result = new GeneratePatientInvitationQRcodeDTO();
        LbqWrapper<PatientRecommendSetting> lbqWrapper = new LbqWrapper();
        PatientRecommendSetting patientRecommendSetting = patientRecommendSettingService.getOne(lbqWrapper);
        if (ObjectUtils.isEmpty(patientRecommendSetting)){
            BizAssert.notNull(null, "未设置患者推荐配置！");
        }
        if (localDateIsAfter(LocalDate.now(),patientRecommendSetting.getActivityEndDate())){
            throw new BizException(9999,"活动已过期！");
        }
        BeanUtils.copyProperties(patientRecommendSetting,result);
        result.setId(patientRecommendSetting.getId());
        //当前项目、当前患者已经生产二维码则在缓存中直接取
        if (redisTemplate.hasKey(SaasRedisBusinessKey.getTenantCodePatientInvitationQrcode()+":"+tenantCode+":"+patientId+":"+Base64.encode(JSON.toJSONString(patientRecommendSetting)))){
            BeanUtils.copyProperties(JSON.parseObject(redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodePatientInvitationQrcode()+":"+tenantCode+":"+patientId+":"+Base64.encode(JSON.toJSONString(patientRecommendSetting))), QrCodeDto.class),result);
            return result;
        }
        //当前患者第一次生成二维码
        CreateFollowerPermanentQrCode form = new CreateFollowerPermanentQrCode();
//        form.setWxAppId(wxAppId);
        form.setParams("patient_invitation_"+patientId);
        form.setExpireSeconds(getExpireSeconds(LocalDate.now()));
        R<QrCodeDto> qrCode = weiXinApi.createFollowerPermanentQrCode(form);
        if (qrCode.getIsSuccess() && Objects.nonNull(qrCode.getData())) {
            redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodePatientInvitationQrcode()+":"+tenantCode+":"+patientId+":"+Base64.encode(JSON.toJSONString(patientRecommendSetting)), JSON.toJSONString(qrCode.getData()),Long.valueOf(form.getExpireSeconds()),TimeUnit.SECONDS);
            BeanUtils.copyProperties(qrCode.getData(),result);
            return result;
        }
        BizAssert.notNull(null, "获取推荐人生成邀请二维码失败！"+qrCode.getMsg());
        return null;
    }


    /**
     * 创建患者推荐关系
     * @param inviterPatient
     * @param subscribe
     * @param patientRecommendSetting
     * @param tenantCode
     */
    @Override
    public void createPatientRecommendRelationship(Patient inviterPatient, Patient subscribe,Long createTime,PatientRecommendSetting patientRecommendSetting, String tenantCode) {
        PatientRecommendRelationship patientRecommendRelationship = new PatientRecommendRelationship();
        patientRecommendRelationship.setPatientId(inviterPatient.getId());
        patientRecommendRelationship.setPatientName(inviterPatient.getName());
        patientRecommendRelationship.setQrDoctorType(patientRecommendSetting.getQrDoctorType());
        if (QrDoctorTypeEnum.SOURCE_DOCTOR.getCode()==patientRecommendSetting.getQrDoctorType()){
            patientRecommendRelationship.setDoctorId(inviterPatient.getDoctorId());
            patientRecommendRelationship.setDoctorName(inviterPatient.getDoctorName());
        }else {
            patientRecommendRelationship.setDoctorId(patientRecommendSetting.getDoctorId());
            patientRecommendRelationship.setDoctorName(patientRecommendSetting.getDoctorName());
        }
        patientRecommendRelationship.setPassivePatientId(subscribe.getId());
        patientRecommendRelationship.setPassivePatientName(subscribe.getName());
        log.error("创建患者推荐关系                           createTime = "+createTime);
        patientRecommendRelationship.setScanCodeTime(Instant.ofEpochMilli(createTime*1000).atZone(ZoneOffset.ofHours(8)).toLocalDateTime());
        log.error("创建患者推荐关系                           scanCodeTime = "+patientRecommendRelationship.toString());
        this.save(patientRecommendRelationship);
    }
    /**
     * 获取二维码有效时间 单位：秒
     * @param activityEndDate 失效日期
     * @return
     */
    private Integer getExpireSeconds(LocalDate activityEndDate){
        Date activityEndOfDay = DateUtil.parse(activityEndDate.toString()+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
        return Long.valueOf(DateUtil.between(new Date(), activityEndOfDay, DateUnit.SECOND)).intValue();
    }

    /**
     * 比较第一个日期是否大于第二个日期
     * @param firstDate 第一个日期
     * @param secondDate 第二个日期
     * @return true-大于;false-不大于
     */
    private boolean localDateIsAfter(LocalDate firstDate, LocalDate secondDate) {
        return firstDate.isAfter(secondDate);
    }
}
