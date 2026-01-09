package com.caring.sass.nursing.controller.appointment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.nursing.constant.AppointmentStatusEnum;
import com.caring.sass.nursing.dto.appointment.*;
import com.caring.sass.nursing.entity.appointment.AppointConfig;
import com.caring.sass.nursing.entity.appointment.Appointment;
import com.caring.sass.nursing.service.appointment.AppointConfigService;
import com.caring.sass.nursing.service.appointment.AppointmentService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.AppDoctorDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 患者预约表
 * </p>
 *
 * @author leizhi
 * @date 2021-01-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/appointment")
@Api(value = "Appointment", tags = "患者预约表")
//@PreAuth(replace = "appointment:")
public class AppointmentController extends SuperController<AppointmentService, Long, Appointment, AppointmentPageDTO, AppointmentSaveDTO, AppointmentUpdateDTO> {

    @Autowired
    PatientApi patientApi;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    AppointConfigService appointConfigService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Appointment> appointmentList = list.stream().map((map) -> {
            Appointment appointment = Appointment.builder().build();
            //TODO 请在这里完成转换
            return appointment;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(appointmentList));
    }


    @ApiOperation("预约每周统计 废弃")
    @Deprecated
    @ApiImplicitParams({
            @ApiImplicitParam(value = "上一周为负，当前为0，下一周为正数，依次+- 1", name = "week", dataType = "Integer")
    })
    @GetMapping("statistics/week")
    public R<AppointWeekTotalVo> statisticsWeek(@RequestParam("week") Integer week,
                                                @RequestParam(value = "doctorId", required = false) Long doctorId,
                                                @RequestParam(value = "groupId", required = false) Long groupId,
                                                @RequestParam(value = "organId", required = false) Long organId) {

        AppointWeekTotalVo statisticsWeek = baseService.statisticsWeek(week, doctorId, organId, groupId);
        return R.success(statisticsWeek);

    }

    @ApiOperation("预约每天统计 废弃")
    @Deprecated
    @ApiImplicitParams({
            @ApiImplicitParam(value = "yyyy-MM-dd", name = "day", dataType = "String")
    })
    @GetMapping("statistics/day")
    public R<AppointDayCountVo> statisticsDay(@RequestParam("day") String day,
                                              @RequestParam(value = "doctorId", required = false) Long doctorId,
                                              @RequestParam(value = "groupId", required = false) Long groupId,
                                              @RequestParam(value = "organId", required = false) Long organId) {

        AppointDayCountVo dayCountVo = baseService.statisticsDay(day, doctorId, organId, groupId);
        return R.success(dayCountVo);

    }

    @ApiOperation("【精准预约】 医生，app 查询每日待就诊或已就诊数据")
    @Override
    public R<IPage<Appointment>> page(@RequestBody PageParams<AppointmentPageDTO> params) {

        AppointmentPageDTO model = params.getModel();
        if (AppointmentStatusEnum.NO_VISIT.getCode() == model.getStatus()) {
            params.setSort("time,createTime");
            params.setOrder("ascending,ascending");
        } else if (AppointmentStatusEnum.VISITED.getCode() == model.getStatus()) {
            params.setSort("time,visit_time");
            params.setOrder("ascending,descending");
        }
        IPage<Appointment> buildPage = params.buildPage();
        model.setPatientDeleteMark(null);
        Appointment appointment = BeanUtil.toBean(model, Appointment.class);
        QueryWrap<Appointment> queryWrap = this.handlerWrapper(appointment, params);
        if (UserType.NURSING_STAFF.equals(BaseContextHandler.getUserType())) {
            // 没有医生信息。没有小组信息。就是查询医助下全部的医生预约情况
            if (model.getDoctorId() == null) {
                AppDoctorDTO appDoctorDTO = new AppDoctorDTO();
                appDoctorDTO.setNursingId(BaseContextHandler.getUserId());
                R<List<Doctor>> doctorNameAndId = doctorApi.findDoctorNameAndId(appDoctorDTO);
                List<Doctor> data = doctorNameAndId.getData();
                if (CollUtil.isNotEmpty(data)) {
                    List<Long> doctorIds = data.stream().map(SuperEntity::getId).collect(Collectors.toList());
                    queryWrap.in("doctor_id", doctorIds);
                } else {
                    // 未查询到医助下的医生。
                    return R.success(buildPage);
                }
            }
        }
        baseService.page(buildPage, queryWrap);
        List<Appointment> records = buildPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<Long> longList = records.stream().map(Appointment::getPatientId).collect(Collectors.toList());
            R<List<Patient>> listByIds = patientApi.listByIds(longList);
            if (listByIds.getIsSuccess() && listByIds.getData() != null) {
                Map<Long, Patient> collect = listByIds.getData().stream().collect(Collectors.toMap(Patient::getId, item -> item));
                records.forEach(item -> {
                    Patient patient = collect.get(item.getPatientId());
                    if (Objects.nonNull(patient)) {
                        item.setPatientName(patient.getName());
                        item.setRemark(patient.getRemark());
                        item.setDoctorRemark(patient.getDoctorRemark());
                        item.setPatientDoctorId(patient.getDoctorId());
                        item.setAvatar(patient.getAvatar());
                    }
                });
            }
        }
        return R.success(buildPage);
    }



    @PostMapping("patient/appoint")
    @ApiOperation("【精准预约】 患者端我的预约页面使用")
    public R<IPage<Appointment>> patientAppoint(@RequestBody @Validated PageParams<AppointmentPageDTO> params) {

        params.setSort("appointment_sort,appointment_date");
        params.setOrder("ascending,descending");
        IPage<Appointment> buildPage = params.buildPage();
        super.query(params, buildPage, null);
        List<Appointment> records = buildPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<Long> longList = records.stream().map(Appointment::getDoctorId).collect(Collectors.toList());
            R<List<Doctor>> listByIds = doctorApi.listByIds(longList);
            if (listByIds.getIsSuccess() && listByIds.getData() != null) {
                Map<Long, Doctor> collect = listByIds.getData().stream().collect(Collectors.toMap(Doctor::getId, item -> item));
                records.forEach(appointment -> {
                    Doctor doctor = collect.get(appointment.getDoctorId());
                    if (Objects.nonNull(doctor)) {
                        appointment.setDoctorName(doctor.getName());
                        appointment.setAvatar(doctor.getAvatar());
                        appointment.setDeptartmentName(doctor.getDeptartmentName());
                        appointment.setTitle(doctor.getTitle());
                        appointment.setCloseAppoint(doctor.getCloseAppoint());
                    }
                });
            }
        }
        return R.success(buildPage);
    }


    @GetMapping("patient/appoint/{doctorId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "week", value = "不传默认今日后7天。 传1 为下一轮 7天。", dataType = "Integer")
    })
    @ApiOperation("【精准预约】 患者查看7天医生号源")
    public R<List<AppointModel>> getDoctorConfig(@PathVariable(value = "doctorId") Long doctorId,
                                                 @RequestParam(value = "patientId", required = false) Long patientId,
                                                 @RequestParam(value = "week", required = false) Integer week) {

        LbqWrapper<AppointConfig> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(AppointConfig::getDoctorId, doctorId);
        lbqWrapper.last(" limit 0, 1 ");
        if (patientId == null) {
            patientId = BaseContextHandler.getUserId();
        }
        AppointConfig serviceOne = appointConfigService.getOne(lbqWrapper);
        if (serviceOne == null) {
            List<AppointModel> appointModels = baseService.noAppointConfig(week);
            return R.success(appointModels);
        }
        List<Date> dateList = DateUtils.get7Day(week);
        // 统计日期内 预约情况
        List<AppointModel> appointModels = baseService.statistics7Day(doctorId, dateList);

        // 计算剩余号源和可预约状态
        baseService.getSurplusAppoint(serviceOne, appointModels, dateList, patientId, doctorId);
        return R.success(appointModels);
    }


    @ApiOperation("【精准预约】患者预约")
    @Override
    public R<Appointment> save(@RequestBody @Validated AppointmentSaveDTO appointmentSaveDTO) {
        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentSaveDTO, appointment);
        baseService.save(appointment);
        return R.success(appointment);
    }

    @ApiOperation("【精准预约】 医助或医生 查看日统计数据")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "yyyy-MM-dd", name = "day", dataType = "String")
    })
    @GetMapping("accurateReservation/statistics/day")
    public R<AppointDayCountVo> statisticsDay(@RequestParam("day") String day,
                                              @RequestParam(value = "doctorId", required = false) Long doctorId,
                                              @RequestParam(value = "nursingId", required = false) Long nursingId) {

        List<Long> doctorIds = getQueryDoctorIds(doctorId, nursingId);
        AppointDayCountVo dayCountVo = baseService.statisticsDay(day, doctorIds);
        return R.success(dayCountVo);

    }



    @ApiOperation("【精准预约】 医助或医生 查看周统计数据")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "上一周为负，当前为0，下一周为正数，依次+- 1", name = "week", dataType = "Integer")
    })
    @GetMapping("accurateReservation/statistics/week")
    public R<AppointWeekTotalVo> statisticsWeek(@RequestParam("week") Integer week,
                                                @RequestParam(value = "doctorId", required = false) Long doctorId,
                                                @RequestParam(value = "nursingId", required = false) Long nursingId) {

        List<Long> doctorIds = getQueryDoctorIds(doctorId, nursingId);
        AppointWeekTotalVo statisticsWeek = baseService.statisticsWeek(week, doctorIds);
        return R.success(statisticsWeek);

    }

    @ApiOperation("【精准预约】 医生审批预约接口")
    @PostMapping("doctorApprove")
    public R<Void> doctorApprove(@RequestBody @Validated AppointmentAuditDTO appointmentAuditDTO) {

        baseService.doctorApprove(appointmentAuditDTO);
        return R.success(null);

    }


    @ApiOperation("【精准预约】 医助审批预约接口")
    @PostMapping("nursingApprove")
    public R<Void> nursingApprove(@RequestBody @Validated AppointmentAuditDTO appointmentAuditDTO) {

        baseService.nursingApprove(appointmentAuditDTO);
        return R.success(null);

    }

    @ApiOperation("【精准预约】 医生医助同意增加号源并对预约进行审批通过")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "预约记录的ID", name = "appointmentId", dataType = "Integer"),
            @ApiImplicitParam(value = "是否是医助，app传true", name = "nursing", dataType = "Boolean")
    })
    @PostMapping("directApproval")
    public R<Void> directApproval(@RequestParam(value = "appointmentId") Long appointmentId,
                                  @RequestParam(value = "nursing", required = false) Boolean nursing) {

        baseService.directApproval(appointmentId, nursing);
        return R.success(null);

    }


    @PostMapping("doctor/appoint/approval")
    @ApiOperation("【精准预约】 医生待审核数据列表接口")
    public R<IPage<Appointment>> doctorAppointApproval(@RequestBody @Validated PageParams<AppointmentPageDTO> params) {
        AppointmentPageDTO model = params.getModel();
        // 医生查询的是 待审核的 和已过期的预约数据。
        IPage<Appointment> buildPage = params.buildPage();
        LbqWrapper<Appointment> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Appointment::getDoctorId, model.getDoctorId());
        lbqWrapper.eq(Appointment::getPatientDeleteMark, 0);
        lbqWrapper.in(Appointment::getStatus, AppointmentStatusEnum.UNDER_REVIEW.getCode(), AppointmentStatusEnum.AUDIT_FAILED.getCode());
        lbqWrapper.orderByAsc(Appointment::getAppointmentSort);
        lbqWrapper.orderByDesc(Appointment::getAppointmentDate);
        baseService.page(buildPage, lbqWrapper);
        List<Appointment> records = buildPage.getRecords();
        // 计算预约所在日期，医生剩余号源
        setRemainingNumber(records);
        setPatientInfo(records);
        return R.success(buildPage);
    }



    @PostMapping("nursing/appoint/approval")
    @ApiOperation("【精准预约】 医助待审核数据列表接口")
    public R<IPage<Appointment>> nursingAppointApproval(@RequestBody @Validated PageParams<AppointmentPageDTO> params) {

        AppointmentPageDTO model = params.getModel();
        Long nursingId = model.getNursingId();
        Long doctorId = model.getDoctorId();
        String patientName = model.getPatientName();
        List<Long> doctorIds = getQueryDoctorIds(doctorId, nursingId);
        IPage<Appointment> buildPage = params.buildPage();
        LbqWrapper<Appointment> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.in(Appointment::getDoctorId,doctorIds);
        lbqWrapper.eq(Appointment::getPatientDeleteMark, 0);
        lbqWrapper.in(Appointment::getStatus, AppointmentStatusEnum.UNDER_REVIEW.getCode(), AppointmentStatusEnum.AUDIT_FAILED.getCode());
        if (StrUtil.isNotEmpty(patientName)) {
            lbqWrapper.apply(" patient_id in (select id FROM u_user_patient where service_advisor_id = '"+nursingId+"' and name like '%" + patientName + "%')");
        }
        lbqWrapper.orderByAsc(Appointment::getAppointmentSort);
        lbqWrapper.orderByDesc(Appointment::getAppointmentDate);
        if (CollUtil.isNotEmpty(doctorIds)) {
            baseService.page(buildPage, lbqWrapper);
        }
        List<Appointment> records = buildPage.getRecords();
        // 计算预约所在日期，医生剩余号源
        setRemainingNumber(records);
        setPatientInfo(records);
        return R.success(buildPage);
    }


    @PutMapping("clear/appoint")
    @ApiOperation("【精准预约】 医生医助一键清除 已过期 预约数据")
    public R<Void> clearAppoint(@RequestParam(required = false) Long doctorId,
                                @RequestParam(required = false)  Long nursingId) {

        List<Long> doctorIds = getQueryDoctorIds(doctorId, nursingId);
        if (CollUtil.isEmpty(doctorIds)) {
            return R.success(null);
        }
        baseService.clearAppoint(doctorIds);
        return R.success(null);
    }


    @PutMapping("patient/cancel/appoint")
    @ApiOperation("【精准预约】 患者取消预约")
    public R<Void> patientCancelAppoint(@RequestParam Long appointmentId) {

        baseService.patientCancelAppoint(appointmentId);
        return R.success(null);
    }


    @DeleteMapping("patient/delete/appoint")
    @ApiOperation("【精准预约】 患者删除预约")
    public R<Void> patientDeleteAppoint(@RequestParam Long appointmentId) {

        baseService.patientDeleteAppoint(appointmentId);
        return R.success(null);
    }


    @GetMapping("doctor/statistics/approvalNumber")
    @ApiOperation("【精准预约】 医生统计待审核预约数量")
    public R<Integer> doctorStatisticsApprovalNumber(@RequestParam(name = "doctorId") Long doctorId) {

        LbqWrapper<Appointment> lbqWrapper = Wraps.<Appointment>lbQ();
        lbqWrapper.eq(Appointment::getDoctorId, doctorId);
        lbqWrapper.eq(Appointment::getStatus, AppointmentStatusEnum.UNDER_REVIEW.getCode());
        int count = baseService.count(lbqWrapper);
        return R.success(count);
    }


    @GetMapping("nursing/statistics/approvalNumber")
    @ApiOperation("【精准预约】 医助统计待审核预约数量")
    public R<Integer> nursingStatisticsApprovalNumber(@RequestParam(required = false) Long doctorId,
                                                  @RequestParam Long nursingId) {

        List<Long> doctorIds = getQueryDoctorIds(doctorId, nursingId);
        LbqWrapper<Appointment> lbqWrapper = Wraps.<Appointment>lbQ();
        lbqWrapper.in(Appointment::getDoctorId, doctorIds);
        lbqWrapper.eq(Appointment::getStatus, AppointmentStatusEnum.UNDER_REVIEW.getCode());
        if (CollUtil.isEmpty(doctorIds)) {
            return R.success(0);
        }
        int count = baseService.count(lbqWrapper);
        return R.success(count);
    }


    /**
     * 设置医生端。医助端查询数据是使用的医生集合参数
     * @param doctorId
     * @param nursingId
     * @return
     */
    private List<Long> getQueryDoctorIds(Long doctorId, Long nursingId) {
        List<Long> doctorIds = new ArrayList<>();
        if (Objects.nonNull(nursingId) && Objects.isNull(doctorId)) {
            // 查询医助的全部医生。
            AppDoctorDTO appDoctorDTO = new AppDoctorDTO();
            appDoctorDTO.setNursingId(nursingId);
            R<List<Doctor>> doctorNameAndId = doctorApi.findDoctorNameAndId(appDoctorDTO);
            List<Doctor> data = doctorNameAndId.getData();
            if (CollUtil.isNotEmpty(data)) {
                doctorIds = data.stream().map(SuperEntity::getId).collect(Collectors.toList());
            } else {
                return doctorIds;
            }
        }
        if (Objects.nonNull(doctorId)) {
            doctorIds.add(doctorId);
        }
        return doctorIds;
    }


    /**
     * 医生 医助查看待审核列表时，设置患者的信息
     * @param records
     */
    private void setPatientInfo(List<Appointment> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<Long> longList = records.stream().map(Appointment::getPatientId).collect(Collectors.toList());
        R<List<Patient>> listByIds = patientApi.listByIds(longList);
        if (listByIds.getIsSuccess() && listByIds.getData() != null) {
            Map<Long, Patient> collect = listByIds.getData().stream().collect(Collectors.toMap(Patient::getId, item -> item));
            records.forEach(appointment -> {
                Patient patient = collect.get(appointment.getPatientId());
                if (Objects.nonNull(patient)) {
                    appointment.setRemark(patient.getRemark());
                    appointment.setDoctorRemark(patient.getDoctorRemark());
                    appointment.setAvatar(patient.getAvatar());
                    appointment.setPatientDoctorId(patient.getDoctorId());
                    appointment.setSex(patient.getSex());
                    appointment.setBirthday(patient.getBirthday());
                    appointment.setPatientName(patient.getName());
                }
            });
        }
    }


    /**
     * @title 根据预约日期，医生信息，上下午 统计出已经预约人数。 计算出剩余号源
     * @author 杨帅
     * @updateTime 2023/3/29 13:01
     * @throws
     */
    private void setRemainingNumber(List<Appointment> records) {

        if (CollUtil.isEmpty(records)) {
            return;
        }
        // 需要知道这些医生的 号源设置
        List<Appointment> appointments = records.stream()
                .filter(item -> AppointmentStatusEnum.UNDER_REVIEW.getCode() == item.getStatus())
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(appointments)) {
            return;
        }
        Set<Long> doctorIds = new HashSet<>();
        Set<LocalDate> appointmentDate = new HashSet<>();
        Set<Integer> times = new HashSet<>();

        for (Appointment appointment : appointments) {
            doctorIds.add(appointment.getDoctorId());
            appointmentDate.add(appointment.getAppointDate());
            times.add(appointment.getTime());
        }
        LbqWrapper<AppointConfig> configLbqWrapper = new LbqWrapper<>();
        configLbqWrapper.in(AppointConfig::getDoctorId, doctorIds);
        List<AppointConfig> appointConfigList = appointConfigService.list(configLbqWrapper);
        if (CollUtil.isEmpty(appointConfigList)) {
            return ;
        }
        Map<Long, AppointConfig> appointConfigMap = appointConfigList.stream()
                .collect(Collectors.toMap(AppointConfig::getDoctorId, item -> item, (o1, o2) -> o2));
        // 统计对应日期的 未就诊和已就诊人数。
        // 分组统计 医生 日期 上下午 的 未就诊和已就诊 数量
        QueryWrap<Appointment> lbqWrapper = Wraps.<Appointment>q();
        lbqWrapper.in("doctor_id", doctorIds);
        lbqWrapper.in("appoint_date", appointmentDate);
        lbqWrapper.in("time", times);
        lbqWrapper.in("status", AppointmentStatusEnum.NO_VISIT.getCode(), AppointmentStatusEnum.VISITED.getCode());
        lbqWrapper.groupBy("doctor_id, appoint_date, time");
        lbqWrapper.select("doctor_id, appoint_date, time, count(*) as countNum");
        List<Map<String, Object>> mapList = baseService.listMaps(lbqWrapper);
        Object doctorId;
        Object appointDate;
        Object time;
        Object countNum;
        Map<String, Integer> doctorAppointmentNumber = new HashMap<>();
        for (Map<String, Object> objectMap : mapList) {
            doctorId = objectMap.get("doctor_id");
            appointDate = objectMap.get("appoint_date");
            time = objectMap.get("time");
            countNum = objectMap.get("countNum");
            if (Objects.nonNull(countNum)) {
                doctorAppointmentNumber.put(doctorId.toString() + appointDate.toString() + time.toString(), Integer.parseInt(countNum.toString()));
            }
        }
        for (Appointment appointment : appointments) {
            // 预约数
            Integer countNumber = doctorAppointmentNumber.get(appointment.getDoctorId().toString() + appointment.getAppointDate().toString() + appointment.getTime().toString());
            if (countNumber == null) {
                countNumber = 0;
            }
            // 医生的配置
            AppointConfig appointConfig = appointConfigMap.get(appointment.getDoctorId());
            LocalDate appointmentAppointDate = appointment.getAppointDate();
            DayOfWeek dayOfWeek = appointmentAppointDate.getDayOfWeek();
            int week = dayOfWeek.getValue();
            Integer appointmentTime = appointment.getTime();
            // 计算日期所在的周次。 使用周次拿到号源设置，
            int doctorSettingNumber = baseService.getTotalNum(week, appointmentTime, appointConfig);
            int i = doctorSettingNumber - countNumber;
            if (i < 0) {
                appointment.setRemainingNumber(0);
            } else {
                appointment.setRemainingNumber(i);
            }
        }
    }



}
