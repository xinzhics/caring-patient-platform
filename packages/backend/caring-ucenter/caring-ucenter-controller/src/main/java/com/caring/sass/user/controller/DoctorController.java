package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.api.VerificationCodeApi;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.nursing.api.AppointmentApi;
import com.caring.sass.nursing.api.PlanApi;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.enumeration.VerificationCodeType;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.*;
import com.caring.sass.user.util.I18nUtils;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>
 * 前端控制器
 * 医生表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctor")
@Api(value = "Doctor", tags = "医生表")
//@PreAuth(replace = "doctor:")
public class DoctorController extends SuperController<DoctorService, Long, Doctor, DoctorPageDTO, DoctorSaveDTO, DoctorUpdateDTO> {


    private final PatientService patientService;

    private final TenantApi tenantApi;

    private final UserService userService;

    StatisticsService statisticsService;

    @Autowired
    ConsultationGroupService consultationGroupService;

    @Autowired
    ImApi imApi;

    @Autowired
    AppointmentApi appointmentApi;

    @Autowired
    H5RouterApi h5RouterApi;

    @Autowired
    PlanApi planApi;

    @Autowired
    ImRecommendationHeatService imRecommendationHeatService;

    @Autowired
    VerificationCodeApi verificationCodeApi;

    public DoctorController(PatientService patientService,
                            TenantApi tenantApi,
                            UserService userService,
                            StatisticsService statisticsService) {
        this.patientService = patientService;
        this.tenantApi = tenantApi;
        this.userService = userService;
        this.statisticsService = statisticsService;
    }


    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<Doctor> doctorList = list.stream().map((map) -> {
            Doctor doctor = Doctor.builder().build();
            //TODO 请在这里完成转换
            return doctor;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(doctorList));
    }

    @ApiOperation("通过Ids查询不限租户")
    @PutMapping("findByIdsNoTenant")
    public R<List<Doctor>> findByIdsNoTenant(@RequestBody List<Long> doctorIds) {

        List<Doctor> doctorList = baseService.findByIdsNoTenant(doctorIds);
        return R.success(doctorList);

    }


    @ApiOperation("通过手机号查询不限租户")
    @GetMapping("findByMobileNoTenant")
    public R<List<Doctor>> findByMobileNoTenant(@RequestParam String mobile) {

        List<Doctor> doctorList = baseService.findByMobileNoTenant(mobile);
        return R.success(doctorList);

    }


    @ApiOperation("获取患者二维码")
    @GetMapping("getPatientQrCode")
    public R<String> getPatientQrCode(@RequestParam String mobile,
                                      @RequestParam String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        Doctor doctor = baseService.getByMobile(mobile);
        if (doctor == null) {
            return R.success(null);
        }
        return R.success(doctor.getQrCode());

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
    public R<List<Doctor>> query(@RequestBody Doctor data) {
        QueryWrap<Doctor> wrapper = Wraps.q(data);
        List<Doctor> list = getBaseService().list(wrapper);
        for (Doctor doctor : list) {
            if (Objects.isNull(doctor.getAvatar())) {
                doctor.setAvatar(Doctor.defaultAvtar);
            }
        }
        return success(list);
    }

    @Override
    public R<Doctor> save(DoctorSaveDTO doctorSaveDTO) {
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(doctorSaveDTO, doctor);
        baseService.save(doctor);
        return R.success(doctor);
    }

    /**
     * @Author yangShuai
     * @Description 创建默认的医生
     * @Date 2020/10/14 10:13
     *
     * @return com.caring.sass.base.R<com.caring.sass.user.entity.Doctor>
     */
    @PostMapping("createDefaultDoctor")
    public R<Doctor> createDoctor(@RequestBody Doctor doctor) {
        doctor.setImMsgStatus(1);
        doctor.setImWxTemplateStatus(1);
        Doctor serviceDoctor = baseService.createDoctor(doctor);
        return R.success(serviceDoctor);
    }


    @Override
    public void handlerResult(IPage<Doctor> page) {
        List<Doctor> pageRecords = page.getRecords();
        for (Doctor doctor : pageRecords) {
            long patientNoStatus = patientService.countPatientNoStatus(doctor.getId(), UserType.UCENTER_DOCTOR);
            doctor.setCommendNum(patientNoStatus);
            doctor.setTotalPatientCount(patientNoStatus);
            long status = patientService.countPatientByStatus(Patient.PATIENT_SUBSCRIBE, doctor.getId(), UserType.UCENTER_DOCTOR);
            doctor.setFansCount(status);
        }
    }

    /**
     * @Author yangShuai
     * @Description 返回医生信息， 小组排名，推荐人数
     * @Date 2020/10/12 17:38
     *
     * @return com.caring.sass.base.R<com.caring.sass.user.entity.Doctor>
     */
    @Override
    public R<Doctor> get(@PathVariable Long id) {
        Doctor doctor = baseService.getById(id);
        if (Objects.nonNull(doctor)) {
            long patientNoStatus = patientService.countPatientNoStatus(doctor.getId(), UserType.UCENTER_DOCTOR);
            doctor.setCommendNum(patientNoStatus);
            Integer doctorRanking = baseService.getDoctorRanking(doctor.getGroupId(), id);
            doctor.setDoctorRanking(doctorRanking);
            int patientCount = patientService.count(Wraps.<Patient>lbQ().eq(Patient::getDoctorId, id));
            doctor.setTotalPatientCount(Convert.toLong(patientCount));
            Group group = baseService.getDoctorGroup(id);
            if (Objects.nonNull(group)) {
                doctor.setGroupName(group.getName());
            }
        }
        return R.success(doctor);
    }

    @ApiOperation("获取医生的小组")
    @GetMapping("getDoctorGroup")
    public R<Group> getDoctorGroup(@RequestParam("doctorId") Long doctorId) {

        Group group = baseService.getDoctorGroup(doctorId);
        return R.success(group);
    }

    @ApiOperation("删除一个医生")
    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteById(@PathVariable("id") Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        baseService.removeByIds(ids);
        return R.success();
    }


    @ApiOperation("删除医生和患者")
    @DeleteMapping("/doctorAndPatient/{id}")
    public R<Boolean> doctorAndPatient(@PathVariable("id") Long id) {
        baseService.doctorAndPatient(id);
        return R.success();
    }


    /**
     *
     * @param chatGroupSend
     * @return
     */
    @Deprecated
    @ApiOperation("群发消息并返回发送人数(暂未使用)")
    @PostMapping("/doctorSendGroupMsg")
    public R<Long> doctorSendGroupMsg(@RequestBody ChatGroupSend chatGroupSend) {
        Long patientNumber = baseService.doctorSendGroupMsg(chatGroupSend);
        return R.success(patientNumber);
    }


    /**
     * 医生群发消息，只发送给自己的患者。
     * @param chat
     * @return
     */
    @ApiOperation("发送一条消息给多人")
    @PostMapping("/anno/sendMoreChat")
    public R<List<Chat>> sendMoreChat(@RequestBody Chat chat) {
        baseService.sendMoreChatToWeiXin(chat);
        return R.success(new ArrayList<>());
    }

    @ApiOperation("发送一条消息给个人")
    @PostMapping("/anno/sendChat")
    public R<Chat> sendChat(@RequestBody Chat chat) {
        chat = baseService.sendChatToWeiXin(chat);
        return R.success(chat);
    }

    @ApiOperation("获取患者的聊天小组")
    @GetMapping("anno/getPatientImGroup")
    public R<List<ImGroupUser>> getPatientImGroup(@RequestParam("patientId") Long patientId) {

        List<ImGroupUser> imGroupUser = patientService.getImGroupUser(patientId);
        return R.success(imGroupUser);

    }

    /**
     * 接口不具备实际作用 2.4 删除
     * @param doctorId
     * @param tenantCode
     * @return
     */
    @Deprecated
    @ApiOperation("医生Im在线")
    @PutMapping("imOnline/{doctorId}")
    public R patientImOnline(@PathVariable("doctorId") Long doctorId,
                             @RequestParam(value = "tenantCode", required = false) String tenantCode) {

        if (!StringUtils.isEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        Doctor doctor = baseService.getById(doctorId);
        if (Objects.nonNull(doctor)) {
            doctor.setOnline(1);
            baseService.updateById(doctor);
        }
        return R.success();
    }

    @PutMapping("update")
    public R<Boolean> update(@RequestBody Doctor doctor) {
        if (doctor.getId() != null) {
            baseService.updateById(doctor);
        }
        return R.success(true);
    }

    @PutMapping("updateWithTenant/{tenantCode}")
    public R<Boolean> updateWithTenant(@RequestBody Doctor doctor, @PathVariable(value = "tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        if (doctor.getId() != null) {
            baseService.updateById(doctor);
        }
        return R.success(true);
    }

    /**
     * 通过手机号查找医生信息
     *
     * @param mobile 电话号码
     * @return 医生信息
     */
    @GetMapping("/findByMobile")
    public R<Doctor> findByMobile(@RequestParam(value = "mobile") @NotEmpty(message = "手机号不能为空") String mobile) {
        Doctor model = new Doctor();
        model.setMobile(mobile);
        QueryWrap<Doctor> wrap = Wraps.q(model);
        Doctor doctor = baseService.getOne(wrap);
        return R.success(doctor);
    }

    @ApiOperation("查询医生是否使用初始密码")
    @GetMapping("/anno/checkPasswordIsInit")
    public R<Boolean> checkPasswordIsInit(@RequestParam(value = "mobile") @NotEmpty(message = "手机号不能为空") String mobile) {

        Doctor model = new Doctor();
        model.setMobile(mobile);
        model.setPasswordUpdated(CommonStatus.NO);
        QueryWrap<Doctor> wrap = Wraps.q(model);
        int count = baseService.count(wrap);
        return R.success(count > 0);
    }


    @ApiOperation("修改密码")
    @PutMapping("updatePassword")
    public R<Boolean> updatePassword(@RequestBody @Validated DoctorUpdatePasswordDTO updatePassword) {

        Long id = updatePassword.getId();
        String password = updatePassword.getPassword();

        Long userId = BaseContextHandler.getUserId();
        if (!userId.equals(id)) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }

        Doctor user = new Doctor();
        user.setId(id);
        user.setPassword(SecureUtil.md5(password));
        user.setPasswordUpdated(CommonStatus.YES);
        baseService.updateById(user);
        return R.success(true);

    }


    @ApiOperation("重置密码")
    @PostMapping("anno/resetPassword")
    public R<Boolean> resetPassword(@RequestBody @Validated DoctorResetPasswordDTO userResetPassword) {

        String mobile = userResetPassword.getUserMobile();
        String smsCode = userResetPassword.getSmsCode();
        String password = userResetPassword.getPassword();

        mobile = mobile.trim();

        Doctor user = null;
        try {
            user = baseService.getOne(Wraps.<Doctor>lbQ()
                    .eq(Doctor::getMobile, EncryptionUtil.encrypt(mobile))
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setMobile(mobile);
        verificationCodeDTO.setCode(smsCode);
        verificationCodeDTO.setClearKey(true);
        verificationCodeDTO.setType(VerificationCodeType.RESET_PASSWORD);
        R<Boolean> verification = verificationCodeApi.verification(verificationCodeDTO);
        if (verification.getIsSuccess() && verification.getData()) {
            user.setPassword(SecureUtil.md5(password));
            user.setPasswordUpdated(CommonStatus.YES);
            baseService.updateById(user);
        }
        return R.success(true);

    }


    @ApiOperation("设置医生密码")
    @GetMapping("/setPassword")
    @Deprecated
    public R<Boolean> setPassword() {
        return R.success(true);
    }


    /**
     * 通过手机号查找医生信息
     *
     * @param mobile 电话号码
     * @return 医生信息
     */
    @ApiOperation("无授权验证手机号是否已经注册医生")
    @GetMapping("/anno/existByMobile")
    public R<Boolean> existByMobile(@RequestParam(value = "mobile") @NotEmpty(message = "手机号不能为空") String mobile) {
        Doctor model = new Doctor();
        model.setMobile(mobile);
        QueryWrap<Doctor> wrap = Wraps.q(model);
        int count = baseService.count(wrap);
        if (count > 0) {
            return R.success(true);
        }
        return R.success(false);
    }

    /**
     * 通过微信openId查找医生信息
     *
     * @param openId 微信openId
     * @return 医生信息
     */
    @GetMapping("/findByOpenId")
    R<Doctor> findByOpenId(@RequestParam(value = "openId") String openId, @RequestParam(value = "tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        Doctor doctor = null;
        List<Doctor> doctors = baseService.list(Wraps.<Doctor>lbQ()
                .eq(Doctor::getOpenId, openId)
                .orderByDesc(Doctor::getCreateTime)
        );
        if (doctors.size() >= 1) {
            doctor = doctors.get(0);
        }
        return R.success(doctor);
    }

    /**
     * 不需要授权，查找医生信息
     *
     * @param doctorId 医生id
     * @return 医生信息
     */
    @GetMapping("/anno/{doctorId}")
    public R<Doctor> getByIdAno(@PathVariable("doctorId") Long doctorId,
                                @RequestParam(value = "tenantCode") @NotEmpty(message = "项目编码不能为空") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        Doctor doctor = baseService.getById(doctorId);
        if (doctor == null) {
            return R.fail("医生信息不存在");
        }
        // 填充医生所属项目信息
        try {
            R<Tenant> te = tenantApi.getByCode(tenantCode);
            if (te.getIsSuccess() && Objects.nonNull(te.getData())) {
                String tenantName = te.getData().getName();
                doctor.setTenantName(tenantName);
            }
        } catch (Exception e) {
            log.error("获取项目信息失败", e);
        }
        return R.success(doctor);
    }


    @ApiOperation("查询患者所在机构可预约的医生")
    @PostMapping("/appointDoctor")
    public R<IPage<Doctor>> getAppointDoctor(@RequestBody @Validated PageParams<DoctorAppointPageDTO> dtoPageParams) {
        IPage<Doctor> buildPage = dtoPageParams.buildPage();
        baseService.getAppointDoctor(buildPage, dtoPageParams.getModel());
//        this.getBaseService().page(buildPage, lbqWrapper);
        return R.success(buildPage);
    }

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "带数据范围分页列表查询")
    @PostMapping(value = "/pageWithScope")
    @SysLog(value = "'带数据范围分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    public R<IPage<Doctor>> pageWithScope(@RequestBody @Validated PageParams<DoctorPageDTO> params) {
        IPage<Doctor> page = params.buildPage();
        DoctorPageDTO userPage = params.getModel();
        Doctor queryParam = BeanUtil.toBean(userPage, Doctor.class);
        QueryWrap<Doctor> wrap = handlerWrapper(queryParam, params);
        LbqWrapper<Doctor> wrapper = wrap.lambda()
                .like(Doctor::getName, queryParam.getName())
                .like(Doctor::getHospitalName,queryParam.getHospitalName())
                .eq(Doctor::getNursingId,queryParam.getNursingId());
        try {
            wrapper.like(Doctor::getMobile, EncryptionUtil.encrypt(queryParam.getMobile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        baseService.findPage(page, wrapper);
        if (CollUtil.isEmpty(page.getRecords())) {

            return R.success(page);
        }
        List<Doctor> pageRecords = page.getRecords();
        String currentUserType = userPage.getCurrentUserType();
        if (StrUtil.isNotEmpty(currentUserType)) {
            BaseContextHandler.setUserType(currentUserType);
        }
        baseService.desensitization(page.getRecords());

        Boolean noNeedCountPatient = userPage.getNoNeedCountPatient();
        if (noNeedCountPatient != null && noNeedCountPatient) {
            return R.success(page);
        }

        for (Doctor doctor : pageRecords) {
            long patientNoStatus = patientService.countPatientNoStatus(doctor.getId(), UserType.UCENTER_DOCTOR);
            doctor.setCommendNum(patientNoStatus);
            doctor.setTotalPatientCount(patientNoStatus);
            long status = patientService.countPatientByStatus(Patient.PATIENT_SUBSCRIBE_NORMAL, doctor.getId(), UserType.UCENTER_DOCTOR);
            doctor.setFansCount(status);
        }
        return R.success(page);
    }

    @ApiOperation(value = "医生新增会员趋势")
    @GetMapping(value = "/patientNewTrend")
    public R<List<Map<String, Object>>> patientNewTrend(@RequestParam(name = "doctorId") Long doctorId,
                                                        @RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
        List<Map<String, Object>> ret = new ArrayList<>();
        boolean isBefore = endTime.isBefore(startTime);
        if (isBefore) {
            return R.success(ret);
        }
        Period bet = Period.between(startTime, endTime);
        boolean showInYear = bet.getMonths() > 1 || bet.getYears() > 0;
        List<Map<String, Object>> tmp;
        if (showInYear) {
            tmp = patientService.listMaps(Wraps.<Patient>q().select("MONTH(create_time) as createTime", "count(id) countNum")
                    .eq("doctor_id", doctorId)
                    .between("create_time", startTime, endTime)
                    .groupBy("MONTH(create_time)"));
        } else {
            tmp = patientService.listMaps(Wraps.<Patient>q().select("DATE(create_time) as createTime", "count(id) countNum")
                    .eq("doctor_id", doctorId)
                    .between("create_time", startTime, endTime)
                    .groupBy("DATE(create_time)"));
        }
        // 整合数据为字典结构
        Map<String, Object> tmpMap = new HashMap<>();
        for (Map<String, Object> t : tmp) {
            tmpMap.put(Convert.toStr(t.get("createTime")), t.get("countNum"));
        }
        // 按年份统计
        if (showInYear) {
            // 计算相差的月份
            long distance = ChronoUnit.MONTHS.between(startTime, endTime);
            Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
                String thisMonth = Convert.toStr(f.getMonthValue());
                Map<String, Object> tm = MapUtil.of("name", thisMonth+"月");
                tm.put("value", tmpMap.get(thisMonth) == null ? 0 : tmpMap.get(thisMonth));
                ret.add(tm);
            });
            return R.success(ret);
        }
        // 按月份统计
        long distance = ChronoUnit.DAYS.between(startTime, endTime);
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            Map<String, Object> tm = MapUtil.of("name", thisDay);
            tm.put("value", tmpMap.get(thisDay) == null ? 0 : tmpMap.get(thisDay));
            ret.add(tm);
        });
        return R.success(ret);
    }

    @ApiOperation(value = "医生人数&服务会员")
    @GetMapping(value = "/statistics")
    public R<Map<String, Object>> sexDistribution() {
        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> m = userService.getDataScopeById(BaseContextHandler.getUserId());
        List<Long> orgIds = (List<Long>) m.get("orgIds");

        // 医生总数
        int total = baseService.count(Wraps.<Doctor>lbQ().in(Doctor::getOrganId, orgIds));
        // 服务的会员数
        int patientCount = patientService.count(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds).isNotNull(Patient::getDoctorId));
        ret.put("patientCount", patientCount);
        ret.put("total", total);
        return R.success(ret);
    }


    @ApiOperation(value = "app 预约设置中医生列表接口")
    @PostMapping("appointmentDoctorPage")
    public R<IPage<Doctor>> appointmentDoctorPage(@RequestBody PageParams<AppDoctorPageDTO> params) {
        IPage<Doctor> iPage = params.buildPage();
        AppDoctorPageDTO paramsModel = params.getModel();
        LbqWrapper<Doctor> lbqWrapper = new LbqWrapper<>();
        Long nursingId = paramsModel.getNursingId();
        if (Objects.nonNull(nursingId)) {
            lbqWrapper.eq(Doctor::getNursingId, nursingId);
        } else {
            throw new BizException("医助ID不能为空");
        }
        if (StrUtil.isNotEmpty(paramsModel.getDoctorName())) {
            lbqWrapper.like(Doctor::getName, paramsModel.getDoctorName());
        }
        lbqWrapper.orderByAsc(Doctor::getCloseAppoint);
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        baseService.page(iPage, lbqWrapper);
        List<Doctor> records = iPage.getRecords();
        DoctorCustomGroupService doctorCustomGroupService = SpringUtils.getBean(DoctorCustomGroupService.class);
        doctorCustomGroupService.setDoctorGroupInfo(records);
        return R.success(iPage);
    }

    @ApiOperation(value = "app使用的小组医生查询2.4")
    @PostMapping("doctorPage")
    public R<IPage<Doctor>> doctorPage(@RequestBody PageParams<AppDoctorPageDTO> params) {

        IPage<Doctor> iPage = params.buildPage();
        AppDoctorPageDTO paramsModel = params.getModel();
        LbqWrapper<Doctor> lbqWrapper = new LbqWrapper<>();
        Long groupId = paramsModel.getGroupId();
        Long nursingId = paramsModel.getNursingId();
        if (Objects.nonNull(nursingId)) {
            lbqWrapper.eq(Doctor::getNursingId, nursingId);
        }
        if (Objects.nonNull(groupId)) {
            lbqWrapper.apply("id in (select udg.doctor_id from u_user_doctor_group udg where udg.group_id = " + groupId + ")");
        }
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        iPage = baseService.page(iPage, lbqWrapper);
        List<Doctor> records = iPage.getRecords();
        patientService.countPatientByDoctor(records);
        return R.success(iPage);

    }


    @ApiOperation(value = "查询医助下医生的名字和id")
    @PostMapping("findDoctorNameAndId")
    public R<List<Doctor>> findDoctorNameAndId(@RequestBody AppDoctorDTO appDoctorDTO) {

        LbqWrapper<Doctor> lbqWrapper = new LbqWrapper<>();
        Integer independence = appDoctorDTO.getIndependence();
        Long nursingId = appDoctorDTO.getNursingId();
        if (Objects.nonNull(nursingId)) {
            lbqWrapper.eq(Doctor::getNursingId, nursingId);
        }
        String doctorName = appDoctorDTO.getDoctorName();
        if (StrUtil.isNotEmpty(doctorName)) {
            lbqWrapper.like(Doctor::getName, doctorName);
        }
        if (Objects.nonNull(independence)) {
            lbqWrapper.eq(Doctor::getIndependence, independence);
        }
        Long groupId = appDoctorDTO.getGroupId();
        if (Objects.nonNull(groupId)) {
            lbqWrapper.apply("id in (select doctor_id from u_user_doctor_group where group_id = "+ groupId +")" );
        }
        lbqWrapper.select(SuperEntity::getId, Doctor::getName);
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        List<Doctor> doctorList = baseService.list(lbqWrapper);
        return R.success(doctorList);

    }


    @ApiOperation("获取聊天小组详细成员2.4")
    @GetMapping("getPatientGroupDetail")
    public R<ImGroupDetail> getPatientGroupDetail(@RequestParam("patientId") Long patientId,
                                                  @RequestParam("doctorId") Long doctorId) {

        ImGroupDetail imGroupUser = patientService.getImGroupDetail(patientId, null, doctorId);
        return R.success(imGroupUser);

    }



    @ApiOperation("返回会员状态列表页面")
    @GetMapping("/getPatientsByStatusGroupNew")
    public R<JSONObject> getPatientByStateNew(@RequestParam("userId") Long userId,
                                             @RequestParam(value = "dimension", required = false) String dimension) {

        JSONObject ret = statisticsService.statisticPatientByDoctorId(userId, dimension);
        return R.success(ret);
    }


    @ApiOperation("查询患者的医生的基本信息")
    @GetMapping("/getDoctorBaseInfoById/{id}")
    public R<Doctor> getDoctorBaseInfoById(@PathVariable("id") Long id) {
        Doctor doctor = baseService.getBaseDoctorAndImOpenById(id);
        return R.success(doctor);
    }

    @ApiOperation("查询患者的医生的基本信息")
    @GetMapping("/getDoctorBaseInfoByPatientId/{patientId}")
    public R<Doctor> getDoctorBaseInfoByPatientId(@PathVariable("patientId") Long patientId) {

        Patient patient = patientService.getBasePatientById(patientId);
        if (Objects.nonNull(patient)) {
            Long doctorId = patient.getDoctorId();
            if (doctorId != null) {
                Doctor doctor = baseService.getBaseDoctorAndImOpenById(doctorId);
                return R.success(doctor);
            }
            return R.fail("患者没有存在");
        }
        return R.fail("患者不存在");
    }


    @ApiOperation("设置医生为医生组长")
    @GetMapping("/updateDoctorLeader/{doctorId}")
    public R<Doctor> updateDoctorLeader(@PathVariable("doctorId") Long doctorId) {
        Doctor doctor = baseService.updateDoctorLeader(doctorId);
        return R.success(doctor);
    }



    @ApiOperation("使用openId查询患者是否还存在")
    @GetMapping("/anno/checkPatientExist/{openId}")
    public R<Boolean> checkPatientExist(@PathVariable("openId") String openId) {

        Patient patient = patientService.findByOpenId(openId);
        if (Objects.nonNull(patient)) {
            return R.success(true);
        }
        return R.success(false);

    }


    @ApiOperation("删除openId的患者信息切换为医生标签")
    @GetMapping("/anno/changeRoleDeletePatient/{openId}")
    public R<Boolean> changeRoleDeletePatient(@PathVariable("openId") String openId) {

        Patient patient = patientService.findByOpenId(openId);
        if (Objects.nonNull(patient)) {
            String wxAppId = patient.getWxAppId();
            patientService.removeById(patient.getId());
            if (!StringUtils.isEmpty(wxAppId)) {
                baseService.bindUserTags(wxAppId, openId);
            }
        }
        return R.success(true);

    }


    @ApiOperation("创建医生并返回授权")
    @PostMapping("/anno/registerDoctorAndCreateToken")
    public R<JSONObject> registerDoctorAndCreateToken(@RequestBody DoctorRegisterDTO doctorRegisterDTO) {
        return baseService.registerDoctorAndCreateToken(doctorRegisterDTO);
    }


    @ApiOperation(value = "医生更换医助")
    @GetMapping("/changeNursing")
    public R<Boolean> changeNursing(@RequestParam("doctorId") Long doctorId,
                                   @RequestParam("nursingId") Long nursingId) {

        Boolean changeDoctor = baseService.changeNursing(doctorId, nursingId);
        return R.success(changeDoctor);
    }

    @ApiOperation(value = "项目信息更新，刷新医生的名片")
    @PutMapping("/updateDoctorBusinessCardQrCodeForTenantInfo")
    public R<Boolean> updateDoctorBusinessCardQrCodeForTenantInfo(@RequestParam("tenantCode") String tenantCode) {
        // 项目下 所有的医生的 名片都要更新
        LbqWrapper<Doctor> lbqWrapper = Wraps.lbQ();
        SaasGlobalThreadPool.execute(() -> {
            baseService.updateDoctorBusinessCardQrCode(null, lbqWrapper, tenantCode);
        });
        return R.success(true);
    }

    @ApiOperation(value = "统计医生，不包括默认医生")
    @GetMapping("/countDoctor")
    public R<Integer> countDoctor() {

        int count = baseService.count(Wraps.<Doctor>lbQ().eq(Doctor::getBuildIn, 0));
        return R.success(count);
    }


    @ApiOperation(value = "医生待办数据统计")
    @GetMapping("/doctorDealtWith")
    public R<DoctorNeedDealWithDTO> doctorDealtWith(@RequestParam("doctorId") Long doctorId) {

        DoctorNeedDealWithDTO dealWithDTO = new DoctorNeedDealWithDTO();
        // 统计医生未读的IM消息
        R<Integer> doctorMsgNumber = imApi.countDoctorMsgNumber(doctorId);
        if (doctorMsgNumber.getIsSuccess()) {
            dealWithDTO.setImMessage(doctorMsgNumber.getData());
        }
        // 统计医生待审批的预约记录
        R<Integer> approvalNumber = appointmentApi.doctorStatisticsApprovalNumber(doctorId);
        if (approvalNumber.getIsSuccess()) {
            dealWithDTO.setAppointment(approvalNumber.getData());
        }
        // 统计医生待处理的会诊邀请
        Integer number = consultationGroupService.countInviteNumber(doctorId, ConsultationGroupMember.ROLE_DOCTOR);
        dealWithDTO.setConsultationGroup(number);
        // 统计医生待处理的转诊
        return R.success(dealWithDTO);
    }

    @ApiOperation(value = "医生全局搜索关键词相关内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doctorId", required = true, value = "医生ID"),
            @ApiImplicitParam(name = "dimension", required = false, value = "维度： 全部医生时传 all"),
            @ApiImplicitParam(name = "searchContent", required = true, value = "搜索的关键字")
    })
    @GetMapping("/doctorGlobalQuery")
    public R<DoctorGlobalQuery> doctorGlobalQuery(@RequestParam("doctorId") Long doctorId,
                                                  @RequestParam(value = "dimension", required = false)  String dimension,
                                                  @RequestParam("searchContent") String searchContent) {

        DoctorGlobalQuery doctorGlobalQuery = baseService.doctorGlobalQuery(doctorId, dimension, searchContent);


        return R.success(doctorGlobalQuery);

    }


    @ApiOperation(value = "医生群发时统计诊断类型")
    @GetMapping("/doctorCountDisease")
    public R<List<DoctorGlobalDiseaseDTO>> doctorCountDisease(@RequestParam("doctorId") Long doctorId) {
        List<DoctorGlobalDiseaseDTO> diseaseDTOS = baseService.doctorCountDisease(doctorId);
        return R.success(diseaseDTOS);
    }


    @ApiOperation("增加推荐功能使用的热度")
    @PostMapping("imRecommendationHeat")
    public R<Boolean> imRecommendationHeat(@RequestBody ImRecommendationHeat imRecommendationHeat) {
        imRecommendationHeat.setUserType(UserType.UCENTER_DOCTOR);
        imRecommendationHeatService.save(imRecommendationHeat);
        return R.success(true);
    }

    /**
     * 组装医生im推荐给患者的功能
     * @return
     */
    @GetMapping("doctorImRecommend")
    @ApiOperation("医生推荐IM功能列表")
    public R<List<H5Router>> doctorImRecommend(@RequestParam Long doctorId) {

        // 这里不区分医生是否可以看到这个功能。只要符合下方的类型就可以
        R<List<H5Router>> routerByModuleType = h5RouterApi.getH5RouterByModuleType(RouterModuleTypeEnum.MY_FILE, UserType.ADMIN);
        Boolean success = routerByModuleType.getIsSuccess();
        List<H5Router> routerList = new ArrayList<>();
        Boolean hasMonitor = false;
        if (success) {
            List<H5Router> data = routerByModuleType.getData();
            for (H5Router router : data) {
                switch (router.getDictItemType()) {
                    case "BASE_INFO" :
                    case "HEALTH" :
                    case "HEALTH_CALENDAR" :
                    case "MEDICINE" :
                    case "OTHER" :
                    case "TEST_NUMBER" :
                    case "CUSTOM_FOLLOW_UP" :
                    case "CALENDAR" :
                        routerList.add(router);
                        break;
                    case "MONITOR" :
                        hasMonitor = true;
                        break;
                }
            }
        }
        List<ImRecommendationHeat> heats = imRecommendationHeatService.list(Wraps.<ImRecommendationHeat>lbQ()
                .eq(ImRecommendationHeat::getUserId, doctorId)
                .eq(ImRecommendationHeat::getUserType, UserType.UCENTER_DOCTOR)
                .orderByDesc(ImRecommendationHeat::getFunctionHeat));

        if (hasMonitor) {
            R<List<Plan>> monitoringDataPlan = planApi.getPatientMonitoringDataPlan();
            if (monitoringDataPlan.getIsSuccess()) {
                List<Plan> planData = monitoringDataPlan.getData();
                for (Plan plan : planData) {
                    H5Router router = new H5Router();
                    router.setId(plan.getId());
                    router.setName(plan.getName());
                    if (plan.getPlanType() != null) {
                        router.setDictItemId(Long.valueOf(plan.getPlanType()));
                    }
                    router.setDictItemType("MONITOR");
                    routerList.add(router);
                }
            }
        }
        if (CollUtil.isNotEmpty(heats)) {
            List<H5Router> result = new ArrayList<>(routerList.size());
            Map<Long, H5Router> routerMap = routerList.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
            for (ImRecommendationHeat heat : heats) {
                H5Router router = routerMap.get(heat.getFunctionId());
                if (Objects.nonNull(router)) {
                    result.add(router);
                    routerList.remove(router);
                }
            }
            for (H5Router router : routerList) {
                result.add(router);
            }
            return R.success(result);
        } else {
            return R.success(routerList);
        }

    }

    // 判断是 .xls 还是 .xlsx 格式
    private static Workbook getWorkbook(FileInputStream fis, String filePath) throws IOException {
        if (filePath.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (filePath.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("文件格式不支持！");
        }
    }
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void importDoctor() {
        // 读取表格，获取数据中所有的医生信息
        // 读取表格，获取数据中所有的医生信息
        String filePath = "D:\\doctor_import.xlsx";

        List<Doctor> doctors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = getWorkbook(fis, filePath)) {

            Sheet sheet = workbook.getSheetAt(0);


            // 遍历每一行
            int i = 0;
            for (Row row : sheet) {
                // 第二行开始是医生信息
                if (i > 0) {
                    Doctor doctor = new Doctor();
                    Cell cell = row.getCell(0);
                    if (cell == null) {
                        break;
                    }
                    String cellValue = cell.getStringCellValue();
                    doctor.setName(cellValue);


                    cell = row.getCell(1);
                    String mobile = cell.getStringCellValue();
                    doctor.setMobile(mobile);

                    cell = row.getCell(2);
                    String avatar = cell.getStringCellValue();
                    doctor.setAvatar(avatar);

                    cell = row.getCell(3);
                    if (cell != null) {
                        String hospitalName = cell.getStringCellValue();
                        doctor.setHospitalName(hospitalName);
                    }

                    cell = row.getCell(4);
                    if (cell != null) {
                        String departmentName = cell.getStringCellValue();
                        doctor.setDeptartmentName(departmentName);
                    }

                    cell = row.getCell(5);
                    if (cell != null) {
                        String title = cell.getStringCellValue();
                        doctor.setTitle(title);
                    }

                    cell = row.getCell(6);
                    if (cell != null) {

                    }

                    String specialties = "";
                    cell = row.getCell(7);
                    if (cell != null) {
                        specialties = cell.getStringCellValue();
                    }

                    String Introduction = "";
                    cell = row.getCell(8);
                    if (cell != null) {
                        Introduction = cell.getStringCellValue();
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Specialties", specialties);
                    jsonObject.put("Introduction", Introduction);
                    doctor.setExtraInfo(jsonObject.toJSONString());

                    doctors.add(doctor);
                }
                i++;

            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String doctorApiUrl = "https://domain/api/ucenter/doctor";
        String token = "";
        String tenant = "";
        OkHttpClient client = new OkHttpClient();
        for (Doctor doctor : doctors) {
            doctor.setNursingId(1513797282586165248l);
            String avatar = doctor.getAvatar();
            String avatarPath = "D:\\doctor_avatar\\111\\" + avatar;
            File file = new File(avatarPath);
            if (file.exists()) {

                try {
                    String string = uploadFile(file);
                    doctor.setAvatar(string);
                } catch (IOException e) {
                    doctor.setAvatar(null);
                }
            }
            String jsonString = JSONObject.toJSONString(doctor);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, jsonString);

            // 构建请求
            Request request = new Request.Builder()
                    .url(doctorApiUrl)
                    .post(body)
                    .addHeader("Token", token)
                    .addHeader("tenant", tenant)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "*/*")
                    .addHeader("Connection", "keep-alive")
                    .build();
            // 发送请求并处理响应
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("Response Code: " + doctor.getName() + " " + response.code() + "body: " + responseBody);
                } else {
                    System.err.println("Request failed: " + doctor.getName() + " " + response.code() + "body: " + responseBody);
                    if (response.body() != null) {
                        System.err.println("Error Body: " + responseBody);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        importDoctor();
    }

    public static String uploadFile(File file) throws IOException {
        // 1. 构建 MultipartBody
        String fileApiUrl = "https://domain/api/file/file/upload";
        String token = "Bearer ";
        String tenant = "";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), okhttp3.RequestBody.create(MediaType.parse("application/octet-stream"), file))
                // 如果需要其他字段，可以继续 addFormDataPart
                 .addFormDataPart("folderId", "1")
                .build();

        // 2. 构建请求
        Request request = new Request.Builder()
                .url(fileApiUrl)
                .post(requestBody)
                .addHeader("Token", token)
                .addHeader("tenant", tenant)
                .build();

        // 3. 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code: " + response);
            }
            okhttp3.ResponseBody responseBody = response.body();
            JSONObject jsonObject = JSONObject.parseObject(responseBody.string());
            JSONObject data = jsonObject.getJSONObject("data");
            String fileUrl = data.getString("url");
            return fileUrl;
        }
    }
}
