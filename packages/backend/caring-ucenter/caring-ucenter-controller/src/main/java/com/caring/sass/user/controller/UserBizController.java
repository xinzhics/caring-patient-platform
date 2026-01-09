package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.dto.UserBizDto;
import com.caring.sass.user.dto.UserBizInfo;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.redis.DoctorTimeOutConsultationNotice;
import com.caring.sass.user.util.qrCode.QrCodeUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.BindUserTagsForm;
import com.caring.sass.wx.dto.config.OfficialAccountMigrationDTO;
import com.caring.sass.wx.dto.config.QrCodeDto;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName UserBizContorller
 * @Description
 * @Author yangShuai
 * @Date 2022/4/13 9:38
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/userBiz")
@Api(value = "UserBizController", tags = "用户业务")
public class UserBizController {

    @Autowired
    PatientMapper patientMapper;


    @Autowired
    DoctorMapper doctorMapper;


    @Autowired
    NursingStaffService nursingStaffService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    WeiXinApi weiXinApi;


    @ApiOperation("查询用户的名称")
    @PostMapping("/info/query")
    public R<Map<String, Map<Long, String>>> queryUserInfo(@RequestBody UserBizDto userBizDto) {
        Map<String, List<UserBizInfo>> listMap = userBizDto.getMap();
        Map<String, Map<Long, String>> mapMap = new HashMap<>();
        List<UserBizInfo> patientUserBizInfo = listMap.get(UserType.PATIENT);
        if (CollUtil.isNotEmpty(patientUserBizInfo)) {
            Set<Long> longList = patientUserBizInfo.stream().map(UserBizInfo::getUserId).collect(Collectors.toSet());
            List<Patient> mapList = patientMapper.selectList(Wraps.<Patient>lbQ().select(SuperEntity::getId, Patient::getName)
                    .in(SuperEntity::getId, longList));
            if (CollUtil.isNotEmpty(mapList)) {
                Map<Long, String> patientNameMap = mapList.stream().collect(Collectors.toMap(SuperEntity::getId, Patient::getName));
                mapMap.put(UserType.PATIENT, patientNameMap);
            }
        }

        List<UserBizInfo> doctorUserBizInfos = listMap.get(UserType.DOCTOR);
        if (CollUtil.isNotEmpty(doctorUserBizInfos)) {
            Set<Long> longList = doctorUserBizInfos.stream().map(UserBizInfo::getUserId).collect(Collectors.toSet());
            List<Doctor> mapList = doctorMapper.selectList(Wraps.<Doctor>lbQ().select(SuperEntity::getId, Doctor::getName)
                    .in(SuperEntity::getId, longList));
            if (CollUtil.isNotEmpty(mapList)) {
                Map<Long, String> stringMap = mapList.stream().collect(Collectors.toMap(SuperEntity::getId, Doctor::getName));
                mapMap.put(UserType.DOCTOR, stringMap);
            }
        }

        List<UserBizInfo> nursingStaffUserBizInfos = listMap.get(UserType.NURSING_STAFF);
        if (CollUtil.isNotEmpty(nursingStaffUserBizInfos)) {
            Set<Long> longList = nursingStaffUserBizInfos.stream().map(UserBizInfo::getUserId).collect(Collectors.toSet());
            List<NursingStaff> mapList = nursingStaffService.list(Wraps.<NursingStaff>lbQ().select(SuperEntity::getId, NursingStaff::getName)
                    .in(SuperEntity::getId, longList));
            if (CollUtil.isNotEmpty(mapList)) {
                Map<Long, String> stringMap = mapList.stream().collect(Collectors.toMap(SuperEntity::getId, NursingStaff::getName));
                mapMap.put(UserType.NURSING_STAFF, stringMap);
            }
        }
        return R.success(mapMap);
    }


    @ApiOperation("查询单人用户的名称")
    @PostMapping("/info/get")
    public R<UserBizInfo> queryUserInfo(@RequestBody UserBizInfo userBizInfo) {
        String userType = userBizInfo.getUserType();
        switch (userType) {
            case UserType.PATIENT : {
                Patient patient = patientMapper.selectOne(Wraps.<Patient>lbQ()
                        .eq(SuperEntity::getId, userBizInfo.getUserId())
                        .select(SuperEntity::getId, Patient::getName));
                if (Objects.nonNull(patient)) {
                    userBizInfo.setName(patient.getName());
                }
                break;
            }
            case UserType.DOCTOR : {
                Doctor doctor = doctorMapper.selectOne(Wraps.<Doctor>lbQ()
                        .eq(SuperEntity::getId, userBizInfo.getUserId())
                        .select(SuperEntity::getId, Doctor::getName));
                if (Objects.nonNull(doctor)) {
                    userBizInfo.setName(doctor.getName());
                }
                break;
            }
            case UserType.NURSING_STAFF : {
                NursingStaff nursingStaff = nursingStaffService.getOne(Wraps.<NursingStaff>lbQ()
                        .eq(SuperEntity::getId, userBizInfo.getUserId())
                        .select(SuperEntity::getId, NursingStaff::getName));
                if (Objects.nonNull(nursingStaff)) {
                    userBizInfo.setName(nursingStaff.getName());
                }
                break;
            }
            default:
        }
        return R.success(userBizInfo);

    }


    @Autowired
    DoctorTimeOutConsultationNotice doctorTimeOutConsultationNotice;


    @ApiOperation("test")
    @GetMapping("/test")
    public R<String> nursingTimeOutConsultationNotice() {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusMinutes(-30);
        doctorTimeOutConsultationNotice.nursingHandlePatientMessage(now);
        return R.success("");
    }

    @ApiOperation("重新生成医生的二维码和名片")
    @GetMapping("/official/resetDoctorTenantNursingQrcode")
    public R<Boolean> resetDoctorTenantNursingQrcode(@RequestParam String wxAppId) {
        // 查询所有的医生信息。给医生生成 患者关注的二维码
        DoctorService doctorService = SpringUtils.getBean(DoctorService.class);
        if (Objects.isNull(doctorService)) {
            throw new BizException("doctorService is null");
        }

        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        if (!tenantR.getIsSuccess()) {
            throw new BizException("项目不存在");
        }
        Tenant tenant = tenantR.getData();
        String qrCodeUrl = "";
        if (tenant.isPersonalServiceNumber()) {
            Config config = new Config();
            config.setAppId(wxAppId);
            R<Config> xinApiConfig = weiXinApi.getConfig(config);
            Boolean success = xinApiConfig.getIsSuccess();
            if (success) {
                Config configData = xinApiConfig.getData();
                if (Objects.isNull(configData)) {
                    throw new BizException("公众号不存在");
                }
                qrCodeUrl = configData.getWxQrCodeUrl();
            }

        }
        List<Doctor> allDoctorList = doctorMapper.selectList(Wraps.<Doctor>lbQ()
                .select(SuperEntity::getId, Doctor::getBuildIn, Doctor::getQrCode, Doctor::getHospitalName, Doctor::getName, Doctor::getTitle, Doctor::getAvatar, Doctor::getEnglishQrCode));
        for (Doctor doctor : allDoctorList) {
            if (doctor.getBuildIn().equals(1)) {
                continue;
            }
            String dtoUrl;
            if (tenant.isCertificationServiceNumber()) {
                try {
                    QrCodeDto qrCodeDto = doctorService.generatePatientFocusQrCode(wxAppId, doctor.getId());
                    QrCodeDto englishQrCodeDto = doctorService.englishGeneratePatientFocusQrCode(wxAppId, doctor.getId());
                    dtoUrl = qrCodeDto.getUrl();
                    doctor.setQrCode(dtoUrl);
                    doctor.setEnglishQrCode(englishQrCodeDto.getUrl());
                    doctorMapper.updateById(doctor);
                } catch (Exception e) {
                    log.error("officialAccountMigration doctor update qrcode error doctorId {}", doctor.getId());
                    continue;
                }
            } else if (tenant.isPersonalServiceNumber()) {
                dtoUrl = qrCodeUrl;
                doctor.setQrCode(qrCodeUrl);
                doctor.setEnglishQrCode(qrCodeUrl);
            } else {
                continue;
            }

            boolean updateStatus = false;
            try {
                doctor.setBusinessCardQrCode(QrCodeUtils.patientSubscribeCode(dtoUrl, doctor.getAvatar(), doctor.getName(), tenant.getName(), doctor.getHospitalName(), tenant.getLogo()));
                updateStatus = true;
            } catch (Exception e) {
                log.error("officialAccountMigration doctor update BusinessCardQrCode error doctorId {}", doctor.getId());
            }
            try {
                doctor.setDownLoadQrcode(QrCodeUtils.patientSubscribeCode_2(dtoUrl, doctor.getAvatar(), doctor.getName(), tenant.getName(), doctor.getHospitalName(), doctor.getTitle()));
                updateStatus = true;
            } catch (Exception e) {
                log.error("officialAccountMigration doctor update downloadQrCode error doctorId {}", doctor.getId());
            }
            try {
                doctor.setEnglishBusinessCardQrCode(QrCodeUtils.patientSubscribeCode_3(doctor.getEnglishQrCode(), doctor.getAvatar(), doctor.getName(), tenant.getName(), doctor.getHospitalName(), doctor.getTitle()));
                updateStatus = true;
            } catch (Exception e) {
                log.error("officialAccountMigration doctor update downloadQrCode error doctorId {}", doctor.getId());
            }
            if (updateStatus) {
                doctorMapper.updateById(doctor);
            }
        }
        return R.success(true);
    }

    @ApiOperation("更新患者医生的OpenId, 重新生成医生的二维码，项目的医生激活码")
    @GetMapping("/official/account/migration")
    public R<Boolean> officialAccountMigration(@RequestParam("oldAppId") String oldAppId) {

        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        if (!tenantR.getIsSuccess()) {
            throw new BizException("项目不存在");
        }
        Tenant data = tenantR.getData();
        if (data.isPersonalServiceNumber()) {
            // 查询所有的医生信息。给医生生成 患者关注的二维码
            R<Config> xinApiConfig = weiXinApi.getConfig(Config.builder().appId(data.getWxAppId()).build());
            Config configData = xinApiConfig.getData();
            List<Doctor> allDoctorList = doctorMapper.selectList(Wraps.<Doctor>lbQ()
                    .select(SuperEntity::getId, Doctor::getBuildIn, Doctor::getQrCode, Doctor::getName, Doctor::getHospitalName, Doctor::getTitle, Doctor::getEnglishQrCode));
            for (Doctor doctor : allDoctorList) {
                try {
                    String dtoUrl = configData.getWxQrCodeUrl();
                    doctor.setQrCode(dtoUrl);
                    doctor.setEnglishQrCode(dtoUrl);
                    try {
                        doctor.setBusinessCardQrCode(QrCodeUtils.patientSubscribeCode(dtoUrl, doctor.getAvatar(), doctor.getName(), data.getName(), doctor.getHospitalName(), data.getLogo()));
                    } catch (Exception e) {
                        log.error("officialAccountMigration doctor update qrcode error doctorId {}", doctor.getId());
                    }
                    try {
                        doctor.setDownLoadQrcode(QrCodeUtils.patientSubscribeCode_2(dtoUrl, doctor.getAvatar(), doctor.getName(), data.getName(), doctor.getHospitalName(), doctor.getTitle()));
                    } catch (Exception e) {
                        log.error("officialAccountMigration doctor update download qrcode error doctorId {}", doctor.getId());
                    }
                    try {
                        doctor.setEnglishBusinessCardQrCode(QrCodeUtils.patientSubscribeCode_3(doctor.getEnglishQrCode(), doctor.getAvatar(), doctor.getName(), data.getName(), doctor.getHospitalName(), doctor.getTitle()));
                    } catch (Exception e) {
                        log.error("officialAccountMigration doctor update download qrcode error doctorId {}", doctor.getId());
                    }
                    doctorMapper.updateById(doctor);
                } catch (Exception e) {
                    log.error("officialAccountMigration doctor update qrcode error doctorId {}", doctor.getId());
                }
            }
            return R.success(true);
        }
        DoctorService doctorService = SpringUtils.getBean(DoctorService.class);
        if (Objects.isNull(doctorService)) {
            throw new BizException("doctorService is null");
        }
        // 查询还在关注的 患者 openIds
        List<Patient> patients = patientMapper.selectList(Wraps.<Patient>lbQ().select(SuperEntity::getId, Patient::getOpenId, Patient::getStatus).ne(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE));

        // 查询关注的医生的openId
        List<Doctor> doctorList = doctorMapper.selectList(Wraps.<Doctor>lbQ().select(SuperEntity::getId, Doctor::getOpenId)
                .eq(Doctor::getWxStatus, 1));

        List<String> openIds = new ArrayList<>();
        // 拿到患者 医生的openId。
        List<String> patientOpenIds = patients.stream().map(Patient::getOpenId).collect(Collectors.toList());
        List<String> doctorOpenIds = doctorList.stream().map(Doctor::getOpenId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(patientOpenIds)) {
            openIds.addAll(patientOpenIds);
        }
        if (CollUtil.isNotEmpty(doctorOpenIds)) {
            openIds.addAll(doctorOpenIds);
        }
        Tenant tenant = data;
        @Length(max = 50, message = "微信AppId50") String wxAppId = tenant.getWxAppId();
        List<String> newPatientOpenIds = new ArrayList<>();
        List<String> newDoctorOpenIds = new ArrayList<>();
        // 要转换的openId 不为空时，发送给微信服务，查询openId在新公众号对应的 新openId
        if (CollUtil.isNotEmpty(openIds)) {
            // 请求微信服务。查询openId 在新公众号的 openIds ，并返回结果。
            OfficialAccountMigrationDTO dto = new OfficialAccountMigrationDTO();
            dto.setOldAppId(oldAppId);
            dto.setOpenIds(openIds);
            R<Map<String, String>> accountMigration = weiXinApi.officialAccountMigration(dto);
            Map<String, String> migrationData = accountMigration.getData();
            if (CollUtil.isNotEmpty(migrationData)) {
                if (CollUtil.isNotEmpty(patients)) {
                    for (Patient patient : patients) {
                        if (StrUtil.isNotEmpty(patient.getOpenId())) {
                            String openId = migrationData.get(patient.getOpenId());
                            if (StrUtil.isNotEmpty(openId)) {
                                newPatientOpenIds.add(openId);
                                patient.setOpenId(openId);
                                patient.setWxAppId(wxAppId);
                                patientMapper.updateById(patient);
                            }
                        }
                    }
                }

                if (CollUtil.isNotEmpty(doctorList)) {
                    for (Doctor doctor : doctorList) {
                        if (StrUtil.isNotEmpty(doctor.getOpenId())) {
                            String openId = migrationData.get(doctor.getOpenId());
                            if (StrUtil.isNotEmpty(openId)) {
                                newDoctorOpenIds.add(openId);
                                doctor.setOpenId(openId);
                                doctor.setWxAppId(wxAppId);
                                doctorMapper.updateById(doctor);
                            }
                        }
                    }
                }
            }
        }
        // 将患者通过新的openId。 给他设置患者的标签
        if (CollUtil.isNotEmpty(newPatientOpenIds)) {
            List<List<String>> subList = ListUtils.subList(newPatientOpenIds, 45);
            for (List<String> stringList : subList) {
                String join = String.join(",", stringList);
                BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
                bindUserTagsForm.setWxAppId(wxAppId);
                bindUserTagsForm.setOpenIds(join);
                bindUserTagsForm.setTagId(TagsEnum.PATIENT_TAG.getValue());
                weiXinApi.bindUserTags(bindUserTagsForm);
            }
        }
        // 给医生通过他的新openId 设置医生标签
        if (CollUtil.isNotEmpty(newDoctorOpenIds)) {
            List<List<String>> subList = ListUtils.subList(newDoctorOpenIds, 45);
            for (List<String> stringList : subList) {
                String join = String.join(",", stringList);
                BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
                bindUserTagsForm.setWxAppId(wxAppId);
                bindUserTagsForm.setOpenIds(join);
                bindUserTagsForm.setTagId(TagsEnum.DOCTOR_TAGS.getValue());
                weiXinApi.bindUserTags(bindUserTagsForm);
            }
        }

        // 查询所有的医生信息。给医生生成 患者关注的二维码
        List<Doctor> allDoctorList = doctorMapper.selectList(Wraps.<Doctor>lbQ()
                .select(SuperEntity::getId, Doctor::getBuildIn, Doctor::getQrCode, Doctor::getName, Doctor::getHospitalName, Doctor::getTitle, Doctor::getEnglishQrCode));
        for (Doctor doctor : allDoctorList) {
            if (StrUtil.isEmpty(doctor.getQrCode())) {
                continue;
            }
            try {
                QrCodeDto qrCodeDto = doctorService.generatePatientFocusQrCode(wxAppId, doctor.getId());
                QrCodeDto englishQrCodeDto = doctorService.englishGeneratePatientFocusQrCode(wxAppId, doctor.getId());
                String dtoUrl = qrCodeDto.getUrl();
                doctor.setQrCode(dtoUrl);
                doctor.setEnglishQrCode(englishQrCodeDto.getUrl());
                try {
                    doctor.setBusinessCardQrCode(QrCodeUtils.patientSubscribeCode(dtoUrl, doctor.getAvatar(), doctor.getName(), tenant.getName(), doctor.getHospitalName(), tenant.getLogo()));
                } catch (Exception e) {
                    log.error("officialAccountMigration doctor update qrcode error doctorId {}", doctor.getId());
                }
                try {
                    doctor.setDownLoadQrcode(QrCodeUtils.patientSubscribeCode_2(dtoUrl, doctor.getAvatar(), doctor.getName(), tenant.getName(), doctor.getHospitalName(), doctor.getTitle()));
                } catch (Exception e) {
                    log.error("officialAccountMigration doctor update download qrcode error doctorId {}", doctor.getId());
                }
                try {
                    doctor.setEnglishBusinessCardQrCode(QrCodeUtils.patientSubscribeCode_3(doctor.getEnglishQrCode(), doctor.getAvatar(), doctor.getName(), tenant.getName(), doctor.getHospitalName(), doctor.getTitle()));
                } catch (Exception e) {
                    log.error("officialAccountMigration doctor update download qrcode error doctorId {}", doctor.getId());
                }
                doctorMapper.updateById(doctor);
            } catch (Exception e) {
                log.error("officialAccountMigration doctor update qrcode error doctorId {}", doctor.getId());
            }
        }

        R<Boolean> doctorQrCode = tenantApi.clearAndCreatedDoctorQrCode(tenant.getCode());
        if (!doctorQrCode.getIsSuccess()) {
            log.error("clearAndCreatedDoctorQrCode error");
        }
        return R.success(true);
    }


}
