package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SassDateUtis;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.dto.ChatClearHistory;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dao.DoctorCustomGroupPatientMapper;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.entity.Referral;
import com.caring.sass.user.service.*;
import com.caring.sass.user.service.impl.WeiXinService;
import com.caring.sass.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 患者转诊
 * </p>
 *
 * @author leizhi
 * @date 2021-08-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/referral")
@Api(value = "Referral", tags = "患者转诊")
//@PreAuth(replace = "referral:")
public class ReferralController extends SuperController<ReferralService, Long, Referral, ReferralPageDTO, ReferralSaveDTO, ReferralUpdateDTO> {

    private final PatientService patientService;

    private final DoctorService doctorService;

    private final WeiXinService weiXinService;

    private final TenantApi tenantApi;

    private final FileUploadApi fileUploadApi;

    private final CustomGroupingService customGroupingService;

    private final DoctorCustomGroupService doctorCustomGroupService;

    @Autowired
    ImApi imApi;

    public ReferralController(PatientService patientService, DoctorService doctorService,
                              WeiXinService weiXinService, TenantApi tenantApi, FileUploadApi fileUploadApi,
                              CustomGroupingService customGroupingService, DoctorCustomGroupService doctorCustomGroupService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.weiXinService = weiXinService;
        this.tenantApi = tenantApi;
        this.fileUploadApi = fileUploadApi;
        this.customGroupingService = customGroupingService;
        this.doctorCustomGroupService = doctorCustomGroupService;
    }


    @Override
    public void handlerResult(IPage<Referral> page) {

        List<Referral> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        String userType = BaseContextHandler.getUserType();
        if (UserType.ORGAN_ADMIN.equals(userType) || UserType.ADMIN_OPERATION.equals(userType)) {
            String tenant = BaseContextHandler.getTenant();
            R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
            if (securitySettings.getIsSuccess()) {
                Boolean data = securitySettings.getData();
                if (data != null && data) {
                    for (Referral record : records) {
                        record.setPatientName(SensitiveInfoUtils.desensitizeName(record.getPatientName()));
                        record.setAcceptDoctorName(SensitiveInfoUtils.desensitizeName(record.getAcceptDoctorName()));
                        record.setLaunchDoctorName(SensitiveInfoUtils.desensitizeName(record.getLaunchDoctorName()));
                    }
                }
            }
        }

    }

    /**
     * 批量查询
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @Override
    @ApiOperation(value = "批量查询", notes = "批量查询")
    @PostMapping("/query")
    public R<List<Referral>> query(@RequestBody Referral data) {
        QueryWrap<Referral> wrapper = Wraps.q(data);
        wrapper.orderByDesc("create_time");
        return success(getBaseService().list(wrapper));
    }

    @Override
    public QueryWrap<Referral> handlerWrapper(Referral model, PageParams<ReferralPageDTO> params) {
        QueryWrap<Referral> wrapper = model == null ? Wraps.q() : Wraps.q(model);
        Long serviceId = null;
        ReferralPageDTO pageDTO = params.getModel();
        if (pageDTO != null) {
            serviceId = pageDTO.getServiceId();
        }
        if (Objects.nonNull(serviceId)) {
            wrapper.or().eq("launch_service_id", serviceId)
                    .or().eq("accept_service_id", serviceId);
        }

        if (CollUtil.isNotEmpty(params.getMap())) {
            Map<String, String> map = params.getMap();
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, getEntityClass()), DateUtils.getStartTime(value));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, getEntityClass()), DateUtils.getEndTime(value));
                }
            }
        }
        return wrapper;
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Referral> referralList = list.stream().map((map) -> {
            Referral referral = Referral.builder().build();
            //TODO 请在这里完成转换
            return referral;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(referralList));
    }

    @ApiOperation("查询转诊状态")
    @GetMapping("/getReferralStatus")
    public R<Boolean> getReferralStatus(@RequestParam("launchDoctorId") @NotNull(message = "发起医生id") Long launchDoctorId,
                                        @RequestParam("patientId") @NotNull(message = "患者id") Long patientId) {
        // 是否可以转诊
        boolean canReferral = baseService.count(Wraps.<Referral>lbQ()
                .eq(Referral::getPatientId, patientId)
                .eq(Referral::getReferralStatus, 0)
                .eq(Referral::getLaunchDoctorId, launchDoctorId)) > 0;
        return R.success(canReferral);
    }

    @Override
    public R<Referral> handlerUpdate(ReferralUpdateDTO model) {
        // 取消操作 需要判断是否已经被接收
        if (Objects.equals(model.getReferralStatus(), 2)) {
            Long referralId = model.getId();
            Referral referral = baseService.getById(referralId);
            boolean hasAccept = Objects.isNull(referral) || Objects.equals(referral.getReferralStatus(), 1);
            if (hasAccept) {
                return R.fail("患者已被接收，无法取消");
            }
        }
        return R.successDef();
    }

    @ApiOperation("查询转诊卡详情")
    @GetMapping("/getReferralCard")
    public R<ReferralCardVo> getReferralCard(@RequestParam("launchDoctorId") @NotNull(message = "发起医生id") Long launchDoctorId,
                                             @RequestParam("patientId") @NotNull(message = "患者id") Long patientId) {
        // 是否可以转诊
        Patient patient = patientService.getById(patientId);
        Doctor doctor = doctorService.getById(launchDoctorId);
        ReferralCardVo vo = ReferralCardVo.builder()
                .patientAge(SassDateUtis.getAge(patient.getBirthday()))
                .patientAvatar(patient.getAvatar())
                .patientSex(patient.getSex())
                .patientName(patient.getName())
                .doctorName(doctor.getName())
                .build();
        return R.success(vo);
    }

    @ApiOperation("接收转诊")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "转诊id", dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "acceptDoctorId", value = "接收医生id", dataType = "long", paramType = "query")
    })
    @PutMapping("/acceptReferral/{id}")
    public R<Boolean> acceptReferral(@PathVariable("id") @NotNull(message = "转诊id不能为空") Long id,
                                     @RequestParam("acceptDoctorId") @NotNull(message = "接收医生id不能为空") Long acceptDoctorId) {
        Referral referral = baseService.getById(id);
        if (Objects.isNull(referral)) {
            return R.fail("转诊记录不存在");
        }

        // 转诊状态：0未接收、1已接收、2已取消
        Integer status = referral.getReferralStatus();
        if (Objects.equals(1, status)) {
            return R.fail("转诊记录已接收");
        }
        if (Objects.equals(2, status)) {
            return R.fail("转诊记录已取消");
        }
        Long doctorId = referral.getAcceptDoctorId();
        if (!Objects.equals(doctorId, acceptDoctorId)) {
            return R.fail("接收医生不正确");
        }

        // 转诊性质：0单次转诊、1长期转诊
        Integer category = referral.getReferralCategory();
        referral.setReferralStatus(1);
        referral.setAcceptTime(LocalDateTime.now());
        baseService.updateById(referral);
        // 长期转诊需要更换医生id
        boolean alwaysR = Objects.equals(category, 1);
        if (alwaysR) {
            Long patientId = referral.getPatientId();
            patientService.changeDoctor(patientId, doctorId);
//            Patient patient = patientService.getById(patientId);
//            if (Objects.isNull(patient)) {
//                return R.fail("转诊会员已删除");
//            }
//            Long patientDoctorId = patient.getDoctorId();
//            Doctor doctor = doctorService.getById(doctorId);
//            boolean clearNursingPatientChat = false;
//            Long nursingId = null;
//            if (Objects.nonNull(doctor)) {
//                // 要将患者转移到 医生所在的单位下面。
//                patient.setServiceAdvisorName(doctor.getNursingName());
//                // 患者原先的医助和现在医生的医助不是同一人， 清除医助对患者的备注
//                if (!patient.getServiceAdvisorId().equals(doctor.getNursingId())) {
//                    patient.setRemark("");
//                }
//                patient.setDoctorRemark("");
//                if (!patient.getServiceAdvisorId().equals(doctor.getNursingId())) {
//                    clearNursingPatientChat = true;
//                    nursingId = patient.getServiceAdvisorId();
//                }
//                patient.setServiceAdvisorId(doctor.getNursingId());
//                patient.setOrganId(doctor.getOrganId());
//                patient.setClassCode(doctor.getClassCode());
//                patient.setOrganCode(doctor.getOrganCode());
//                patient.setOrganName(doctor.getOrganName());
//                patient.setCreateTime(LocalDateTime.now());
//            }
//            patient.setDoctorId(doctorId);
//            patient.setDoctorName(referral.getAcceptDoctorName());
//            patientService.updateById(patient);
            // 清除医生和患者的消息未读
//            ChatClearHistory chatClearHistory = new ChatClearHistory();
//            chatClearHistory.setUserId(patientDoctorId);
//            chatClearHistory.setRoleType(UserType.UCENTER_DOCTOR);
//            List<String> groupIds = new ArrayList<>();
//            groupIds.add(patient.getImAccount());
//            chatClearHistory.setGroupIds(groupIds);
//            imApi.clearChatNoReadHistory(chatClearHistory);
//            imApi.removeChatUserNewMsg(patient.getImAccount(), patientDoctorId.toString(), UserType.UCENTER_DOCTOR);
//            // 清除医助和患者的消息未读
//            if (clearNursingPatientChat && Objects.nonNull(nursingId)) {
//                chatClearHistory = new ChatClearHistory();
//                chatClearHistory.setUserId(nursingId);
//                chatClearHistory.setRoleType(UserType.UCENTER_NURSING_STAFF);
//                chatClearHistory.setGroupIds(groupIds);
//                imApi.clearChatNoReadHistory(chatClearHistory);
//                imApi.removeChatUserNewMsg(patient.getImAccount(), nursingId.toString(), UserType.UCENTER_NURSING_STAFF);
//            }
//            customGroupingService.removeByPatientId(patientId);
//            doctorCustomGroupService.removePatient(patientId, patientDoctorId);
        }
        return R.success();
    }


    @ApiOperation("发起转诊")
    @PostMapping("/launchReferral")
    public R<Boolean> launchReferral(@RequestBody @Validated LaunchReferralDTO launchReferralDTO) {
        ReferralSaveDTO referralSaveDTO = BeanUtil.toBean(launchReferralDTO, ReferralSaveDTO.class);
        Long launchDoctorId = launchReferralDTO.getLaunchDoctorId();
        Long patientId = launchReferralDTO.getPatientId();
        Long acceptDoctorId = launchReferralDTO.getAcceptDoctorId();
        Patient patient = patientService.getById(patientId);
        if (Objects.isNull(patient)) {
            return R.fail("患者不存在");
        }
        if (!Objects.equals(patient.getDoctorId(), launchDoctorId)) {
            return R.fail("只能转诊自己名下患者");
        }
        if (Objects.equals(acceptDoctorId, launchDoctorId)) {
            return R.fail("发起医生不可与接收医生相同！");
        }
        // 是否可以转诊
        boolean canReferral = baseService.count(Wraps.<Referral>lbQ()
                .eq(Referral::getPatientId, patientId)
                .eq(Referral::getReferralStatus, 0)
                .eq(Referral::getLaunchDoctorId, launchDoctorId)) > 0;
        String patientName = patient.getName();
        if (canReferral) {
            String msg = String.format("患者%s存在未完成的转诊，完成后才可以再次发起转诊", patientName);
            return R.fail(msg);
        }

        Doctor launchDoctor = doctorService.getById(launchDoctorId);
        if (Objects.isNull(launchDoctor)) {
            return R.fail("医生不存在");
        }
        Doctor acceptDoctor = doctorService.getById(acceptDoctorId);
        referralSaveDTO.setLaunchDoctorName(launchDoctor.getName());
        referralSaveDTO.setLaunchServiceId(launchDoctor.getNursingId());
        referralSaveDTO.setPatientName(patientName);
        referralSaveDTO.setPatientAvatar(patient.getAvatar());
        referralSaveDTO.setPatientSex(patient.getSex());
        String birth = patient.getBirthday();
        referralSaveDTO.setPatientAge(SassDateUtis.getAge(birth));
        referralSaveDTO.setAcceptDoctorId(acceptDoctorId);
        referralSaveDTO.setAcceptServiceId(acceptDoctor.getNursingId());
        referralSaveDTO.setAcceptDoctorName(acceptDoctor.getName());
        referralSaveDTO.setAcceptDoctorHospitalName(acceptDoctor.getHospitalName());
        referralSaveDTO.setReferralStatus(0);
        referralSaveDTO.setLaunchTime(LocalDateTime.now());
        Referral r = BeanUtil.toBean(referralSaveDTO, Referral.class);
        r.setLaunchNursingPatientRemark(patient.getRemark());
        r.setLaunchDoctorPatientRemark(patient.getDoctorRemark());
        baseService.save(r);
        String str1 = Objects.equals(referralSaveDTO.getReferralCategory(), 1) ? "长期转诊" : "单次转诊";
        Long referralId = r.getId();

        Boolean weiXinTemplate = weiXinService.noSendWeiXinTemplate(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.REFERRAL_SERVICE);
        if (!weiXinTemplate) {
            weiXinService.sendReferral(str1, referralSaveDTO.getLaunchDoctorName(), r, patient.getOpenId(), patientName, patient.getId(), acceptDoctor.getDepartmentId(), patient.getMobile());
        }

        //生成转诊的二维码  二维码内容   /doctor/scanningReferral/index?id=1&acceptDoctorId=xx
        try {
            R<Tenant> tR = tenantApi.getByCode(BaseContextHandler.getTenant());
            Tenant tenant = tR.getData();
            boolean b = tR.getIsError() || Objects.isNull(tenant);
            if (b) {
                throw new Exception("查询项目详细失败");
            }
            String qrContent = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getCode()), String.format(H5Router.DOCTOR_REFERRAL_CARD_URL, referralId, acceptDoctorId));
            byte[] out2 = QrCodeUtil.generatePng(qrContent, QrConfig.create().setWidth(300).setHeight(300));
            String qrName = "referralQrCode_" + RandomUtil.randomString(6);
            MockMultipartFile mockMultipartFile = new MockMultipartFile(qrName, qrName + ".png", ContentType.APPLICATION_OCTET_STREAM.toString(), out2);
            log.info("生成二维码完成，开始上传中...");
            R<com.caring.sass.file.entity.File> fileR = fileUploadApi.upload(0L, mockMultipartFile);
            log.info("生成二维码完成，并上传成功");
            String url = fileR.getData().getUrl();
            r.setQrUrl(url);
            baseService.updateById(r);
        } catch (Exception e) {
            log.error("生成二维码异常", e);
        }
        return R.success();
    }


}
