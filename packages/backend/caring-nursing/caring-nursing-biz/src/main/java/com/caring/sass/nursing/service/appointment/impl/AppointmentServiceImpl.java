package com.caring.sass.nursing.service.appointment.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.DoctorAppointmentReviewEnum;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.msgs.dto.SendAssistanceNoticeDto;
import com.caring.sass.nursing.constant.AppointmentSortEnum;
import com.caring.sass.nursing.constant.AppointmentStatusEnum;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.appointment.AppointConfigMapper;
import com.caring.sass.nursing.dao.appointment.AppointmentMapper;
import com.caring.sass.nursing.dto.appointment.AppointDayCountVo;
import com.caring.sass.nursing.dto.appointment.AppointModel;
import com.caring.sass.nursing.dto.appointment.AppointWeekTotalVo;
import com.caring.sass.nursing.dto.appointment.AppointmentAuditDTO;
import com.caring.sass.nursing.entity.appointment.AppointConfig;
import com.caring.sass.nursing.entity.appointment.Appointment;
import com.caring.sass.nursing.service.appointment.AppointmentService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.service.task.sender.WeixinSender;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者预约表
 * </p>
 *
 * @author leizhi
 * @date 2021-01-27
 */
@Slf4j
@Service
public class AppointmentServiceImpl extends SuperServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {


    @Autowired
    PatientApi patientApi;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    NursingStaffApi nursingStaffApi;

    @Autowired
    AppointConfigMapper appointConfigMapper;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WeixinSender weixinSender;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    TemplateMsgApi templateMsgApi;

    @Autowired
    ImApi imApi;

    @Autowired
    MsgPatientSystemMessageApi patientSystemMessageApi;

    @Autowired
    BusinessReminderLogControllerApi businessReminderLogControllerApi;


    @Override
    public boolean updateById(Appointment model) {
        Long id = model.getId();
        Appointment appointment = baseMapper.selectById(id);
        // 待就诊的 点击 签到
        if (model.getStatus() == AppointmentStatusEnum.VISITED.getCode()) {
            if (appointment.getStatus() == AppointmentStatusEnum.NO_VISIT.getCode()) {
                appointment.setStatus(AppointmentStatusEnum.VISITED.getCode());
                appointment.setAppointmentSort(AppointmentSortEnum.VISITED.getCode());
                appointment.setVisitTime(LocalDateTime.now());
                baseMapper.updateById(appointment);
                return true;
            } else {
                return super.updateById(model);
            }
        }
        return super.updateById(model);
    }

    /**
     * 【精准预约】 医助或医生 查看周统计数据
     * @param week
     * @param doctorIds
     * @return
     */
    @Override
    public AppointWeekTotalVo statisticsWeek(Integer week, List<Long> doctorIds) {
        List<Date> dateList = DateUtils.thisMondayAndthisSunday(week);
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(AppointmentStatusEnum.NO_VISIT.getCode());
        arrayList.add(AppointmentStatusEnum.VISITED.getCode());

        QueryWrapper<Appointment> queryWrapper = Wrappers.<Appointment>query();
        queryWrapper.select("appoint_date as appointDate", "count(*) as total");
        AppointWeekTotalVo.AppointWeekTotalVoBuilder builder = AppointWeekTotalVo.builder();
        if (CollUtil.isNotEmpty(doctorIds)) {
            queryWrapper.in("doctor_id", doctorIds);
        } else {
            Date date = dateList.get(0);
            Date date1 = dateList.get(1);
            int i = 1;
            for (; date.before(date1); date = DateUtils.addDay(date, 1), i++ ) {
                if (i == 1)
                    builder.mondayDay(date).mondayUserTotal(0);
                if (i == 2)
                    builder.tuesdayDay(date).tuesdayUserTotal(0);
                if (i == 3)
                    builder.wednesdayDay(date).wednesdayUserTotal(0);
                if (i == 4)
                    builder.thursdayDay(date).thursdayUserTotal(0);
                if (i == 5)
                    builder.fridayDay(date).fridayUserTotal(0);
                if (i == 6)
                    builder.saturdayDay(date).saturdayUserTotal(0);
                if (i == 7)
                    builder.sundayDay(date).sundayUserTotal(0);
            }
            return builder.build();
        }
        queryWrapper.between("appoint_date", dateList.get(0), dateList.get(1))
                .in("status", arrayList)
                .groupBy("appoint_date");

        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        Date date = dateList.get(0);
        Date date1 = dateList.get(1);
        int i = 1;
        for (; date.before(date1); date = DateUtils.addDay(date, 1), i++ ) {
            Integer countResult = getCountResult(mapList, "appointDate", "total", DateUtils.date2Str(date, DateUtils.Y_M_D));
            if (i == 1)
                builder.mondayDay(date).mondayUserTotal(countResult);
            if (i == 2)
                builder.tuesdayDay(date).tuesdayUserTotal(countResult);
            if (i == 3)
                builder.wednesdayDay(date).wednesdayUserTotal(countResult);
            if (i == 4)
                builder.thursdayDay(date).thursdayUserTotal(countResult);
            if (i == 5)
                builder.fridayDay(date).fridayUserTotal(countResult);
            if (i == 6)
                builder.saturdayDay(date).saturdayUserTotal(countResult);
            if (i == 7)
                builder.sundayDay(date).sundayUserTotal(countResult);
        }
        return builder.build();
    }


    /**
     * @Author yangShuai
     * @Description 统计这一周内。每天的预约人数
     * @Date 2021/1/27 16:45
     *
     * @param week
     * @param doctorId
     * @return com.caring.sass.nursing.dto.appointment.AppointWeekTotalVo
     */
    @Deprecated
    @Override
    public AppointWeekTotalVo statisticsWeek(Integer week, Long doctorId, Long organId, Long groupId) {
        List<Date> dateList = DateUtils.thisMondayAndthisSunday(week);
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(0);
        arrayList.add(1);

        QueryWrapper<Appointment> queryWrapper = Wrappers.<Appointment>query();
        queryWrapper.select("appoint_date as appointDate", "count(*) as total");
        if (Objects.nonNull(doctorId)) {
            queryWrapper.eq("doctor_id", doctorId);
        }
        if (Objects.nonNull(organId)) {
            queryWrapper.eq("organization_id", organId);
        }
        if (Objects.nonNull(groupId)) {
            queryWrapper.eq("group_id", groupId);
        }
        queryWrapper.between("appoint_date", dateList.get(0), dateList.get(1))
                .in("status", arrayList)
                .groupBy("appoint_date");

        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        AppointWeekTotalVo.AppointWeekTotalVoBuilder builder = AppointWeekTotalVo.builder();
        Date date = dateList.get(0);
        Date date1 = dateList.get(1);
        int i = 1;
        for (; date.before(date1); date = DateUtils.addDay(date, 1), i++ ) {
            Integer countResult = getCountResult(mapList, "appointDate", "total", DateUtils.date2Str(date, DateUtils.Y_M_D));
            if (i == 1)
                builder.mondayDay(date).mondayUserTotal(countResult);
            if (i == 2)
                builder.tuesdayDay(date).tuesdayUserTotal(countResult);
            if (i == 3)
                builder.wednesdayDay(date).wednesdayUserTotal(countResult);
            if (i == 4)
                builder.thursdayDay(date).thursdayUserTotal(countResult);
            if (i == 5)
                builder.fridayDay(date).fridayUserTotal(countResult);
            if (i == 6)
                builder.saturdayDay(date).saturdayUserTotal(countResult);
            if (i == 7)
                builder.sundayDay(date).sundayUserTotal(countResult);
        }
        return builder.build();
    }

    /**
     * @title 分析统计出的数据
     * @author 杨帅
     * @updateTime 2023/3/29 13:16
     * @throws
     */
    private Integer getCountResult(List<Map<String, Object>> mapList, String key1, String countName, String key1Value) {
        if (CollectionUtils.isEmpty(mapList))
            return 0;
        for (Map<String, Object> stringObjectMap : mapList) {
            if (stringObjectMap == null)
                continue;
            Object o = stringObjectMap.get(key1);
            if (Objects.nonNull(o) && o.toString().equals(key1Value)) {
                Object count = stringObjectMap.get(countName);
                if (Objects.isNull(count)) {
                    return 0;
                }
                return Integer.parseInt(count.toString());
            }
        }
        return 0;
    }

    /**
     * @title 【精准预约】 医助或医生 查看日统计数据
     * @author 杨帅
     * @updateTime 2023/3/29 9:24
     * @throws
     */
    @Override
    public AppointDayCountVo statisticsDay(String day, List<Long> doctorIds) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(AppointmentStatusEnum.NO_VISIT.getCode());
        arrayList.add(AppointmentStatusEnum.VISITED.getCode());
        // 按 预约的上下午统计 不区分是否就诊
        QueryWrapper<Appointment> queryWrapper = Wrappers.<Appointment>query()
                .select("time as time", "count(*) as total");
        AppointDayCountVo.AppointDayCountVoBuilder builder = AppointDayCountVo.builder();
        if (CollUtil.isNotEmpty(doctorIds)) {
            queryWrapper.in("doctor_id", doctorIds);
        } else {
            builder.appointmentMorningTotal(0);
            builder.appointmentAfternoonTotal(0);
            builder.appointmentTotal(0);
            builder.seeDoctorMorningTotal(0);
            builder.seeDoctorAfternoonTotal(0);
            builder.seeDoctorTotal(0);
            builder.weekName(DateUtils.getWeekName(DateUtils.date2str(day, DateUtils.Y_M_D)));
            return builder.build();
        }
        queryWrapper.eq("appoint_date", DateUtils.date2str(day, DateUtils.Y_M_D))
                .in("status", arrayList)
                .groupBy("time");

        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        Integer appointmentMorningTotal = getCountResult(mapList, "time", "total", "1");
        Integer appointmentAfternoonTotal = getCountResult(mapList, "time", "total", "2");
        builder.appointmentMorningTotal(appointmentMorningTotal);
        builder.appointmentAfternoonTotal(appointmentAfternoonTotal);
        builder.appointmentTotal(appointmentMorningTotal + appointmentAfternoonTotal);

        QueryWrapper<Appointment> wrapper = Wrappers.<Appointment>query()
                .select("time as time", "count(*) as total");
        if (CollUtil.isNotEmpty(doctorIds)) {
            wrapper.in("doctor_id", doctorIds);
        } else {
            throw new BizException("无医生有预约可看");
        }
        wrapper.eq("status", 1)
                .eq("appoint_date", DateUtils.date2str(day, DateUtils.Y_M_D))
                .groupBy("time");
        // 按就诊 的上下午统计
        List<Map<String, Object>> mapListStatus = baseMapper.selectMaps(wrapper);
        Integer seeDoctorMorningTotal = getCountResult(mapListStatus, "time", "total", "1");
        Integer seeDoctorAfternoonTotal = getCountResult(mapListStatus, "time", "total", "2");
        builder.seeDoctorMorningTotal(seeDoctorMorningTotal);
        builder.seeDoctorAfternoonTotal(seeDoctorAfternoonTotal);
        builder.seeDoctorTotal(seeDoctorMorningTotal + seeDoctorAfternoonTotal);
        builder.weekName(DateUtils.getWeekName(DateUtils.date2str(day, DateUtils.Y_M_D)));
        return builder.build();
    }

    /**
     * @Author yangShuai
     * @Description 统计上下午的预约情况
     * @Date 2021/1/28 14:18
     *
     * @param day
     * @param doctorId
     * @return com.caring.sass.nursing.dto.appointment.AppointDayCountVo
     */
    @Deprecated
    @Override
    public AppointDayCountVo statisticsDay(String day, Long doctorId,  Long organId, Long groupId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(0);

        arrayList.add(1);
        // 按 预约的上下午统计 不区分是否就诊
        QueryWrapper<Appointment> queryWrapper = Wrappers.<Appointment>query()
                .select("time as time", "count(*) as total");
        if (Objects.nonNull(doctorId)) {
            queryWrapper.eq("doctor_id", doctorId);
        }
        if (Objects.nonNull(organId)) {
            queryWrapper.eq("organization_id", organId);
        }
        if (Objects.nonNull(groupId)) {
            queryWrapper.eq("group_id", groupId);
        }
        queryWrapper.eq("appoint_date", DateUtils.date2str(day, DateUtils.Y_M_D))
                .in("status", arrayList)
                .groupBy("time");

        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        AppointDayCountVo.AppointDayCountVoBuilder builder = AppointDayCountVo.builder();
        Integer appointmentMorningTotal = getCountResult(mapList, "time", "total", "1");
        Integer appointmentAfternoonTotal = getCountResult(mapList, "time", "total", "2");
        builder.appointmentMorningTotal(appointmentMorningTotal);
        builder.appointmentAfternoonTotal(appointmentAfternoonTotal);
        builder.appointmentTotal(appointmentMorningTotal + appointmentAfternoonTotal);

        QueryWrapper<Appointment> wrapper = Wrappers.<Appointment>query()
                .select("time as time", "count(*) as total");
        if (Objects.nonNull(doctorId)) {
            wrapper.eq("doctor_id", doctorId);
        }
        if (Objects.nonNull(organId)) {
            wrapper.eq("organization_id", organId);
        }
        if (Objects.nonNull(groupId)) {
            wrapper.eq("group_id", groupId);
        }
        wrapper.eq("status", 1)
                .eq("appoint_date", DateUtils.date2str(day, DateUtils.Y_M_D))
                .groupBy("time");
        // 按就诊 的上下午统计
        List<Map<String, Object>> mapListStatus = baseMapper.selectMaps(wrapper);
        Integer seeDoctorMorningTotal = getCountResult(mapListStatus, "time", "total", "1");
        Integer seeDoctorAfternoonTotal = getCountResult(mapListStatus, "time", "total", "2");
        builder.seeDoctorMorningTotal(seeDoctorMorningTotal);
        builder.seeDoctorAfternoonTotal(seeDoctorAfternoonTotal);
        builder.seeDoctorTotal(seeDoctorMorningTotal + seeDoctorAfternoonTotal);
        builder.weekName(DateUtils.getWeekName(DateUtils.date2str(day, DateUtils.Y_M_D)));
        return builder.build();
    }

    /**
     * @Author yangShuai
     * @Description 当医生没有设置预约计划时返回预约情况
     * @Date 2021/1/28 14:19
     *
     * @param week
     * @return java.util.List<com.caring.sass.nursing.dto.appointment.AppointModel>
     */
    @Override
    public List<AppointModel> noAppointConfig(Integer week) {

        List<Date> dateList = DateUtils.get7Day(week);
        List<AppointModel> appointments = new ArrayList<>();
        for (Date date : dateList) {
            appointments.add(AppointModel.builder().status(2).date(date).title(DateUtils.get7DayTitle(date)).morning(0).afternoon(0).build());
        }
        return appointments;
    }


    /**
     * @Author yangShuai
     * @Description 统计日期范围内。每天上下午的预约总人数
     * @Date 2021/1/28 13:57
     *
     * @param doctorId
     * @param dateList
     * @return java.util.List<com.caring.sass.nursing.dto.appointment.AppointModel>
     */
    @Override
    public List<AppointModel> statistics7Day(Long doctorId, List<Date> dateList) {
        if (dateList == null) {
            return new ArrayList<>();
        }
        QueryWrapper<Appointment> queryWrapper = Wrappers.<Appointment>query()
                .select("concat(appoint_date,time) as appoint_date_time", "count(*) as total");
        if (Objects.nonNull(doctorId)) {
            queryWrapper.eq("doctor_id", doctorId);
        }
//        queryWrapper.in("status", 2, 0)
        // 统计未就诊的已就诊的
        queryWrapper.in("status", AppointmentStatusEnum.NO_VISIT.getCode(), AppointmentStatusEnum.VISITED.getCode())
                .between("appoint_date", dateList.get(0), dateList.get(dateList.size() - 1))
                .groupBy("appoint_date,time");
        List<Map<String, Object>> mapListStatus = baseMapper.selectMaps(queryWrapper);
        List<AppointModel> appointments = new ArrayList<>();
        Integer morning;
        Integer afternoon;
        for (Date date : dateList) {
            morning = getCountResult(mapListStatus, "appoint_date_time", "total", DateUtils.date2Str(date, DateUtils.Y_M_D) + "1");
            afternoon = getCountResult(mapListStatus, "appoint_date_time", "total", DateUtils.date2Str(date, DateUtils.Y_M_D) + "2");
            appointments.add(
                    AppointModel.builder().title(DateUtils.get7DayTitle(date))
                    .date(date)
                    .morning(morning)
                    .afternoon(afternoon)
                    .build());

        }
        return appointments;
    }


    /**
     * @Author yangShuai
     * @Description 根据医生的预约配置表 和 当前时间段的预约情况，计算每日剩余的号源
     * @Date 2021/1/28 14:19
     *
     * @param serviceOne
     * @param appointModels
     * @param doctorId
     * @return java.util.List<com.caring.sass.nursing.dto.appointment.AppointModel>
     */
    @Override
    public List<AppointModel> getSurplusAppoint(AppointConfig serviceOne, List<AppointModel> appointModels, List<Date> dateList, Long userId, Long doctorId) {

        if (CollectionUtils.isEmpty(appointModels)) {
            return appointModels;
        }

        // 查询患者 这个周期内的预约记录。
        List<Appointment> appointmentList = baseMapper.selectList(new LbqWrapper<Appointment>()
                .select(Appointment::getAppointDate, Appointment::getTime, Appointment::getStatus)
                .eq(Appointment::getPatientId, userId)
                .eq(Appointment::getDoctorId, doctorId)
                .in(Appointment::getStatus, AppointmentStatusEnum.NO_VISIT.getCode(), AppointmentStatusEnum.VISITED.getCode(), AppointmentStatusEnum.UNDER_REVIEW.getCode())
                .between(Appointment::getAppointDate, dateList.get(0), dateList.get(dateList.size() - 1))
        );
        Map<String, Integer> map = new HashMap<>(appointmentList.size());
        Integer integer;
        // 根据日期 处理一下预约数据
        for (Appointment appointment : appointmentList) {
            String format = appointment.getAppointDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            integer = map.get(format + appointment.getTime());
            if (integer == null) {
                map.put(format + appointment.getTime(), appointment.getStatus());
            } else {
                if (integer < appointment.getStatus()) {
                    map.put(format + appointment.getTime(), appointment.getStatus());                }
            }
        }

        for (AppointModel appointModel : appointModels) {
            if (Objects.isNull(appointModel)) {
                continue;
            }
            Integer week = DateUtils.getWeek(appointModel.getDate());
            // 计算一下上下午 预约状态
            appointModel.calculationQualifications(appointModel, map);
            switch (week) {
                case 1: {
                    appointModel.setMorning(serviceOne.getNumOfSundayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfSundayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfSundayMorning(), serviceOne.getNumOfSundayAfternoon()));
                    }
                    break;
                case 2:
                    appointModel.setMorning(serviceOne.getNumOfMondayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfMondayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfMondayMorning(), serviceOne.getNumOfMondayAfternoon()));
                    break;
                case 3:
                    appointModel.setMorning(serviceOne.getNumOfTuesdayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfTuesdayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfTuesdayMorning(), serviceOne.getNumOfTuesdayAfternoon()));
                    break;
                case 4:
                    appointModel.setMorning(serviceOne.getNumOfWednesdayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfWednesdayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfWednesdayMorning(), serviceOne.getNumOfWednesdayAfternoon()));
                    break;
                case 5:
                    appointModel.setMorning(serviceOne.getNumOfThursdayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfThursdayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfThursdayMorning(), serviceOne.getNumOfThursdayAfternoon()));
                    break;
                case 6:
                    appointModel.setMorning(serviceOne.getNumOfFridayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfFridayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfFridayMorning(), serviceOne.getNumOfFridayAfternoon()));
                    break;
                case 7:
                    appointModel.setMorning(serviceOne.getNumOfSaturdayMorning() - appointModel.getMorning());
                    appointModel.setAfternoon(serviceOne.getNumOfSaturdayAfternoon() - appointModel.getAfternoon());
                    appointModel.setStatus(appointModel.calculationStatus(serviceOne.getNumOfSaturdayMorning(), serviceOne.getNumOfSaturdayAfternoon()));
                    break;
                default:
            }
        }
        return appointModels;
    }




    /**
     * @Author yangShuai
     * @Description
     * 检查当前时间段患者对此医生是否有审核中或待就诊的预约，有：异常 （您已经预约过了。请先就诊后再次预约）。
     *     无：当前时间段医生是否还有剩余号源？ 无：异常（已经没有号了，请刷新后重新预约）。 有：
     *     预约医生是否无需审核？
     *     是：发起预约申请，设置预约状态为审核中, 返回给前端展示审核中，给医生发送 预约待审核模板通知
     *         否：发起预约申请，设置预约状态为待就诊，并发送 预约结果通知(预约成功)
     *     记录预约时间并计算出预约审核过期时间精确到分钟
     *     Redis 存储设计： 使用set数据结构。 预约审核过期时间为key， tenant_code_XXX+ 预约ID 为 member
     * @Date 2021/1/28 14:28
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(Appointment model) {
        R<Patient> patientR = patientApi.get(model.getPatientId());
        if (patientR.getIsError() || Objects.isNull(patientR.getData())) {
            throw new BizException("患者不存在");
        }

        R<Doctor> doctorR = doctorApi.get(model.getDoctorId());
        if (doctorR.getIsError() || Objects.isNull(doctorR.getData())) {
            throw new BizException("医生不存在");
        }


        AppointConfig config = appointConfigMapper.selectOne(Wraps.<AppointConfig>lbQ().eq(AppointConfig::getDoctorId, model.getDoctorId()));
        if (config == null) {
            throw new BizException("医生预约配置为空");
        }

        // 检查当前时间段患者对此医生是否有审核中或待就诊的预约，有：异常 （您已经预约过了。请先就诊后再次预约）。
        LbqWrapper<Appointment> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Appointment::getDoctorId, model.getDoctorId());
        lbqWrapper.eq(Appointment::getAppointDate, model.getAppointDate());
        lbqWrapper.eq(Appointment::getTime, model.getTime());
        lbqWrapper.eq(Appointment::getPatientId, model.getPatientId());
        lbqWrapper.in(Appointment::getStatus, AppointmentStatusEnum.NO_VISIT.getCode(), AppointmentStatusEnum.VISITED.getCode(), AppointmentStatusEnum.UNDER_REVIEW.getCode());
        Integer selectCount = baseMapper.selectCount(lbqWrapper);
        if (selectCount != null && selectCount >= 1) {
            throw new BizException(I18nUtils.getMessage("APPOINTMENT_REMINDER_EXIST"));
        }

        // 设置model冗余字段
        Patient patient = patientR.getData();
        Doctor doctor = doctorR.getData();

        model.setPatientName(patient.getName());
        model.setDoctorName(doctor.getName());
        model.setOrganizationId(doctor.getOrganId());
        // 开启redis锁。进行检验是否可以预约
        // 尝试获取锁 5 秒内获取不到，抛出超时
        String lock = model.getDoctorId() + model.getAppointDate().toString() + model.getTime() + ":lock";
        String tenant = BaseContextHandler.getTenant();
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 20);
            if (lockBoolean) {
                // 查询当前 待就诊 和已就诊 的数量
                lbqWrapper = new LbqWrapper<>();
                lbqWrapper.eq(Appointment::getDoctorId, model.getDoctorId());
                lbqWrapper.eq(Appointment::getAppointDate, model.getAppointDate());
                lbqWrapper.eq(Appointment::getTime, model.getTime());
                lbqWrapper.in(Appointment::getStatus, AppointmentStatusEnum.NO_VISIT.getCode(), AppointmentStatusEnum.VISITED.getCode());
                // 统计已经预约数量
                selectCount = baseMapper.selectCount(lbqWrapper);

                // 获取是周几。
                int week = model.getAppointDate().getDayOfWeek().getValue();

                // 根据 周几 获取 总号源多少。
                Integer totalNum = getTotalNum(week, model.getTime(), config);
                // 当前时间段医生是否还有剩余号源？ 无：异常（已经没有号了，请刷新后重新预约）。
                if (selectCount == null || totalNum <= selectCount) {
                    // redis 解锁
                    throw new BizException(I18nUtils.getMessage("APPOINTMENT_FULL_EXIST"));
                }
                // 预约医生是否无需审核？
                String appointmentReview = doctor.getAppointmentReview();
                model.setAppointmentDate(LocalDateTime.now());
                if (DoctorAppointmentReviewEnum.need_review.toString().equals(appointmentReview)) {
                    model.setStatus(AppointmentStatusEnum.UNDER_REVIEW.getCode());
                    model.setAppointmentSort(AppointmentSortEnum.UNDER_REVIEW.getCode());
                    baseMapper.insert(model);
                    // 异步发送医生待审核模板
                    // 记录预约时间并计算出预约审核过期时间精确到分钟
                    SaasGlobalThreadPool.execute(() -> appointmentHandle(model, patient, doctor, tenant));
                } else {
                    model.setStatus(AppointmentStatusEnum.NO_VISIT.getCode());
                    model.setAppointmentSort(AppointmentSortEnum.NO_VISIT.getCode());
                    baseMapper.insert(model);
                    // 异步发送患者 预约成功模板
                    SaasGlobalThreadPool.execute(() -> appointmentHandle(model, patient, doctor, tenant));
                }
            } else {
                throw new BizException(I18nUtils.getMessage("APPOINTMENT_TIMEOUT_EXIST"));
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
        return true;
    }


    /**
     * 对完成预约后的 后续事情处理。
     * 异步发送医生待审核模板
     * 记录预约时间并计算出预约审核过期时间精确到分钟
     * 异步发送患者 预约成功模板
     */
    private void appointmentHandle(Appointment appointment, Patient patient, Doctor doctor, String tenantCode) {
        Integer status = appointment.getStatus();
        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = null;
        if (tenantR.getIsSuccess()) {
            tenant = tenantR.getData();
        }
        if (Objects.isNull(tenant)) {
            return;
        }

        if (AppointmentStatusEnum.UNDER_REVIEW.getCode() == status) {
            // 异步发送医生待审核模板
            // 记录预约时间并计算出预约审核过期时间精确到分钟
            LocalDateTime appointmentDate = appointment.getAppointmentDate();
            String appointRedisKey = getAppointmentRedisKey(appointmentDate);
            String member = getRedisMember(tenantCode, appointment.getId());
            redisTemplate.boundSetOps(appointRedisKey).add(member);
            try {
                if (StrUtil.isNotEmpty(doctor.getOpenId())) {
                    if (doctor.getWxStatus().equals(1)) {
                        if (tenant.isCertificationServiceNumber()) {
                            R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE);
                            if (templateMsgDtoR.getIsSuccess() && templateMsgDtoR.getData() != null) {
                                TemplateMsgDto templateMsgDto = templateMsgDtoR.getData();
                                Boolean commonCategory = templateMsgDto.getCommonCategory();
                                String templateId = templateMsgDto.getTemplateId();
                                // 进入 医生的 待审核页面
                                String url = ApplicationDomainUtil.wxDoctorBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.DOCTOR_RESERVATION_APPROVE);
                                String wxAppId = tenant.getWxAppId();
                                List<WxMpTemplateData> wxMpTemplateData = AppointmentMessageTemplate.appointmentTemplateMap.get(TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE)
                                        .doctorApprove(appointment, commonCategory, tenant.getDefaultLanguage());

                                weixinSender.sendAppointmentWeiXinMessage(wxAppId, tenantCode,
                                        templateId, doctor.getOpenId(), wxMpTemplateData, templateId, url);
                            } else {
                                log.error("No relevant template found, cancel template message sending");
                            }
                        }
                    }
                }
                Long serviceAdvisorId = patient.getServiceAdvisorId();
                R<NursingStaff> staffR = nursingStaffApi.get(serviceAdvisorId);
                if (staffR.getIsSuccess()) {
                    NursingStaff staffRData = staffR.getData();
                    String imAccount = staffRData.getImAccount();
                    SendAssistanceNoticeDto noticeDtoBuilder = SendAssistanceNoticeDto.builder()
                            .assistanceImAccount(imAccount)
                            .msgType(SendAssistanceNoticeDto.AssistanceNoticeMsgType.PATIENT_APPOINTMENT.toString())
                            .msgContent(I18nUtils.getMessageByTenantDefault("APPOINTMENT_Notice", tenant.getDefaultLanguage(), patient.getName(), doctor.getName()))
                            .build();
                    imApi.sendAssistanceNotice(noticeDtoBuilder);
                }
            } catch (Exception e) {
                log.error("医生待审核预约模板发送失败");
            }
        } else {
            // 异步发送患者 预约成功模板
            R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE);
            if (templateMsgDtoR.getIsSuccess() && templateMsgDtoR.getData() != null) {
                TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
                Boolean commonCategory = msgDtoRData.getCommonCategory();
                String templateId = msgDtoRData.getTemplateId();
                // 进入 患者的 我的预约页面 的链接
                String url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.PATIENT_RESERVATION);
                String wxAppId = tenant.getWxAppId();
                List<WxMpTemplateData> wxMpTemplateData = AppointmentMessageTemplate.appointmentTemplateMap.get(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE)
                        .initPatient(appointment, commonCategory, tenant.getDefaultLanguage());

                sendSystemMessageToPatient(appointment, url, patient.getId(), tenantCode);
                if (tenant.isCertificationServiceNumber()) {
                    weixinSender.sendAppointmentWeiXinMessage(wxAppId, tenantCode,
                            TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE, patient.getOpenId(), wxMpTemplateData, templateId, url);
                }
            } else {
                log.error("No relevant template found, cancel template message sending");
            }
        }

    }


    /**
     * 设置患者删除了预约
     * @param appointmentId
     */
    @Override
    public void patientDeleteAppoint(Long appointmentId) {
        Appointment appointment = baseMapper.selectById(appointmentId);
        if (Objects.nonNull(appointment)) {
            Integer status = appointment.getStatus();
            if (AppointmentStatusEnum.VISITED.getCode() == status ||
                    AppointmentStatusEnum.CANCEL_VISIT.getCode() == status ||
                    AppointmentStatusEnum.VISIT_EXPIRED.getCode() == status ||
                    AppointmentStatusEnum.AUDIT_FAILED.getCode() == status) {
                appointment.setPatientDeleteMark(1);
                baseMapper.updateById(appointment);

                patientSystemMessageApi.deleteByBusinessId(PlanFunctionTypeEnum.BOOKING_MANAGEMENT.getCode(), appointmentId);
            } else {
                throw new BizException("预约已经被修改。取消失败");
            }
        }
    }


    /**
     * @title 患者取消预约
     * @author 杨帅
     * @updateTime 2023/3/29 13:13
     * @throws
     */
    @Override
    public void patientCancelAppoint(Long appointmentId) {
        String lock = getAppointmentLock(appointmentId);
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 3000L, 5);
            if (lockBoolean) {
                Appointment appointment = baseMapper.selectById(appointmentId);
                if (Objects.nonNull(appointment)) {
                    Integer status = appointment.getStatus();
                    if (AppointmentStatusEnum.UNDER_REVIEW.getCode() == status || AppointmentStatusEnum.NO_VISIT.getCode() == status) {
                        appointment.setStatus(AppointmentStatusEnum.CANCEL_VISIT.getCode());
                        // 已取消
                        appointment.setAppointmentSort(AppointmentSortEnum.CANCELED.getCode());
                        baseMapper.updateById(appointment);
                        String tenant = BaseContextHandler.getTenant();
                        SaasGlobalThreadPool.execute(() -> cancelRedisExceed(appointment, tenant));
                    } else {
                        throw new BizException("预约已经被修改。取消失败");
                    }
                }
            } else {
                throw new BizException("取消预约超时，请重试");
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
    }

    /**
     * @title 医生审核预约 通过或者 拒绝
     * @author 杨帅
     * @updateTime 2023/3/29 14:41
     * @throws
     */
    @Override
    public void doctorApprove(AppointmentAuditDTO appointmentAuditDTO) {
        approveAppointment(appointmentAuditDTO, false);
    }

    /**
     * @title 审核预约 通过或者 拒绝
     * @author 杨帅
     * @updateTime 2023/3/29 14:41
     * @throws
     */
    private void approveAppointment(AppointmentAuditDTO appointmentAuditDTO, boolean isNursing) {
        @NotNull Long appointmentId = appointmentAuditDTO.getAppointmentId();
        @NotNull Integer status = appointmentAuditDTO.getStatus();
        String lock = getAppointmentLock(appointmentId);
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 3000L, 5);
            if (lockBoolean) {
                Appointment appointment = baseMapper.selectById(appointmentId);
                if (Objects.nonNull(appointment)) {
                    Integer currentStatus = appointment.getStatus();
                    if (AppointmentStatusEnum.UNDER_REVIEW.getCode() == currentStatus) {
                        // 审核 通过。需要检查这个预约所在时间范围 是否还有号源
                        String tenant = BaseContextHandler.getTenant();
                        if (status.equals(0)) {
                            checkDoctorCodeNumber(appointment);
                            appointment.setStatus(AppointmentStatusEnum.NO_VISIT.getCode());
                            appointment.setAppointmentSort(AppointmentSortEnum.NO_VISIT.getCode());
                            baseMapper.updateById(appointment);
                            // 发送预约成功模板 给患者
                        } else if (status.equals(3)) {
                            appointment.setStatus(AppointmentStatusEnum.AUDIT_FAILED.getCode());
                            appointment.setAppointmentSort(AppointmentSortEnum.APPOINT_FAILED.getCode());
                            appointment.setAuditFailReason(appointmentAuditDTO.getAuditFailReason());
                            appointment.setAuditFailReasonDesc(appointmentAuditDTO.getAuditFailReasonDesc());
                            baseMapper.updateById(appointment);
                        }
                        // 发送 模板 给患者 或者医生
                        SaasGlobalThreadPool.execute(() -> sendTemplateMessage(appointment, tenant, null, isNursing, isNursing));
                        // 清除此预约在redis中预约审核过期时间
                        SaasGlobalThreadPool.execute(() -> cancelRedisExceed(appointment, tenant));
                    } else {
                        auditStatusError(currentStatus);
                    }
                }
            } else {
                throw new BizException("审核超时，请刷新列表重试");
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
    }

    /**
     * @title  医助审核预约 通过或者 拒绝
     * @author 杨帅
     * @updateTime 2023/3/29 14:47
     * @throws
     */
    @Override
    public void nursingApprove(AppointmentAuditDTO appointmentAuditDTO) {
        approveAppointment(appointmentAuditDTO, true);
    }

    /**
     * 不检查医生剩余号源数量。直接通过审批
     * @param appointmentId
     * @param isNursing
     */
    @Override
    public void directApproval(Long appointmentId, Boolean isNursing) {

        String lock = getAppointmentLock(appointmentId);
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 3000L, 5);
            if (lockBoolean) {
                Appointment appointment = baseMapper.selectById(appointmentId);
                if (Objects.nonNull(appointment)) {
                    Integer currentStatus = appointment.getStatus();
                    if (AppointmentStatusEnum.UNDER_REVIEW.getCode() == currentStatus) {
                        String tenant = BaseContextHandler.getTenant();
                        appointment.setStatus(AppointmentStatusEnum.NO_VISIT.getCode());
                        appointment.setAppointmentSort(AppointmentSortEnum.NO_VISIT.getCode());
                        baseMapper.updateById(appointment);
                        // 发送 模板 给患者 或者医生
                        SaasGlobalThreadPool.execute(() -> sendTemplateMessage(appointment, tenant, null, isNursing, isNursing));
                        // 清除此预约在redis中预约审核过期时间
                        SaasGlobalThreadPool.execute(() -> cancelRedisExceed(appointment, tenant));
                    } else {
                        auditStatusError(currentStatus);
                    }
                }
            } else {
                throw new BizException("审核超时，请刷新列表重试");
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }

    }

    /**
     * @title 预约审核过期
     * @author 杨帅
     * @updateTime 2023/3/30 17:51
     * @throws
     * @param localDateTime
     */
    @Override
    public void appointmentReviewExpired(LocalDateTime localDateTime) {
        localDateTime = localDateTime.plusDays(-1);
        String appointRedisKey = getAppointmentRedisKey(localDateTime);
        Boolean hasKey = redisTemplate.hasKey(appointRedisKey);
        if (hasKey != null && hasKey) {
            BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(appointRedisKey);
            Long size = setOperations.size();
            if (size == null) {
                return;
            }
            while (size >= 3) {
                Set<String> members = setOperations.distinctRandomMembers(3);
                size-=3;
                if (CollUtil.isEmpty(members)) {
                    continue;
                }
                for (String member : members) {
                    setOperations.remove(member);
                }
                SaasGlobalThreadPool.execute(() -> appointmentReviewExpired(members));
            }
            if (size >0) {
                Set<String> members = setOperations.members();
                redisTemplate.delete(appointRedisKey);
                SaasGlobalThreadPool.execute(() -> appointmentReviewExpired(members));
            }
        }

    }

    /**
     * 预约审核过期数据解析 并修改预约记录 发送模板
     * @param members
     */
    public void appointmentReviewExpired(Set<String> members) {
        Map<String, Tenant> tenantMap = new HashMap<>();
        for (String member : members) {
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(member);
            Object tenantCodeObj = jsonObject.get("tenantCode");
            Object id = jsonObject.get("id");
            String tenantCode = tenantCodeObj.toString();
            Tenant tenant = tenantMap.get(tenantCode);
            BaseContextHandler.setTenant(tenantCode);
            Appointment appointment = baseMapper.selectById(Long.parseLong(id.toString()));
            if (Objects.isNull(appointment)) {
                return;
            }
            if (!appointment.getStatus().equals(AppointmentStatusEnum.UNDER_REVIEW.getCode())) {
                return;
            }
            appointment.setStatus(AppointmentStatusEnum.AUDIT_FAILED.getCode());
            appointment.setAppointmentSort(AppointmentSortEnum.APPOINT_FAILED.getCode());
            appointment.setAuditFailReason("APPROVAL_NOT_PROCESSED");
            appointment.setAuditFailReasonDesc("预约过期");
            baseMapper.updateById(appointment);
            if (tenant == null) {
                R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
                if (!tenantR.getIsSuccess()) {
                    continue;
                }
                tenant = tenantR.getData();
                tenantMap.put(tenantCode, tenant);
            }
            sendTemplateMessage(appointment, tenantCode, tenant, false, true);
        }
    }


    /**
     * @title 预约就诊过期
     * @author 杨帅
     * @updateTime 2023/3/30 17:52
     * @throws
     * @param localDate
     */
    @Override
    public void appointmentVisitExpired(LocalDate localDate) {

        List<String> tenantCodes = baseMapper.getAllTenantCode(AppointmentStatusEnum.NO_VISIT.getCode(), localDate);
        LbqWrapper<Appointment> wrapper = Wraps.<Appointment>lbQ()
                .eq(Appointment::getStatus, AppointmentStatusEnum.NO_VISIT.getCode())
                .lt(Appointment::getAppointDate, localDate);
        for (String tenantCode : tenantCodes) {
            BaseContextHandler.setTenant(tenantCode);
            Integer count = baseMapper.selectCount(wrapper);
            if (count != null && count > 0) {
                UpdateWrapper<Appointment> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("status", AppointmentStatusEnum.VISIT_EXPIRED.getCode());
                updateWrapper.set("appointment_sort", AppointmentSortEnum.EXPIRED.getCode());
                updateWrapper.eq("status", AppointmentSortEnum.NO_VISIT.getCode());
                updateWrapper.lt("appoint_date", localDate);
                baseMapper.update(new Appointment(), updateWrapper);
            }
        }

    }


    /**
     * 处理个人服务号项目。
     * 当医生有还有未审核的预约时，发送短信提醒给医生。
     */
    @Override
    public void doctorAppointmentReviewReminder() {
        List<String> tenantCodes = baseMapper.getAllTenantCodeByStatus(AppointmentStatusEnum.UNDER_REVIEW.getCode());
        for (String tenantCode : tenantCodes) {
            // 只处理个人服务号项目
            R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
            Tenant data = tenantR.getData();
            if (data.isCertificationServiceNumber()) {
                continue;
            }
            String doctorBizUrl = ApplicationDomainUtil.wxDoctorBizUrl(data.getDomainName(), true, H5Router.DOCTOR_RESERVATION_APPROVE);
            TenantDiseasesTypeEnum diseasesType = data.getDiseasesType();
            BaseContextHandler.setTenant(tenantCode);
            List<Appointment> appointments = baseMapper.selectList(Wraps.<Appointment>lbQ().eq(Appointment::getStatus, AppointmentStatusEnum.UNDER_REVIEW.getCode()));
            Map<Long, List<Appointment>> doctorAppointmentMap = appointments.stream().collect(Collectors.groupingBy(Appointment::getDoctorId));
            doctorAppointmentMap.forEach((doctorId, appointmentList) -> {
                R<Doctor> doctorR = doctorApi.getDoctorBaseInfoById(doctorId);
                Doctor doctor = doctorR.getData();
                if (doctor != null && doctor.getMobile() != null && doctor.getBuildIn().equals(0)) {
                    String smsParams = BusinessReminderType.getDoctorTODOList(data.getName(),
                            appointmentList.size());

                    // 创建一条 今日待办消息 的推送任务
                    BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                            .mobile(doctor.getMobile())
                            .wechatRedirectUrl(doctorBizUrl)
                            .diseasesType(diseasesType == null ? TenantDiseasesTypeEnum.other.toString() : diseasesType.toString())
                            .type(BusinessReminderType.DOCTOR_TODAY_TO_DO_LIST)
                            .tenantCode(tenantCode)
                            .queryParams(smsParams)
                            .doctorId(doctor.getId())
                            .status(0)
                            .openThisMessage(0)
                            .finishThisCheckIn(0)
                            .build();
                    businessReminderLogControllerApi.sendNoticeSms(logSaveDTO);
                }
            });
        }
    }

    /**
     * 给患者发送 预约的系统消息。成功或者失败
     * @param appointment
     * @param patientBizUrl
     * @param patientId
     * @param tenantCode
     */
    private void sendSystemMessageToPatient(Appointment appointment, String patientBizUrl, Long patientId, String tenantCode) {
        MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(PlanFunctionTypeEnum.BOOKING_MANAGEMENT.getCode(),
                appointment.getId(),
                patientBizUrl,
                patientId,
                LocalDateTime.now(),
                appointment.getDoctorName(),
                tenantCode);
        LocalDateTime appointmentDate = appointment.getAppointmentDate();
        int month = appointmentDate.getMonth().getValue();
        int dayOfMonth = appointmentDate.getDayOfMonth();
        Integer time = appointment.getTime();
        String appointmentTime = "" + month + "-" + dayOfMonth + " ";
        if (time != null) {
            if (time.equals(1)) {
                appointmentTime+="上午";
                appointmentDate = appointmentDate.withHour(8);
            } else if (time.equals(2)) {
                appointmentTime+="下午";
                appointmentDate = appointmentDate.withHour(14);
            }
        }
        systemMessageSaveDTO.createPushContent(null, appointment.getStatus(), appointmentTime);
        systemMessageSaveDTO.setAppointmentStatus(appointment.getStatus());
        systemMessageSaveDTO.setAppointmentTime(appointmentDate);
        patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
    }

    /**
     * 发送预约的模板给 患者 或者 医生。
     *
     */
    private void sendTemplateMessage(Appointment appointment, String tenantCode, Tenant tenant, boolean isNursing, boolean sendDoctor) {
        // 根据预约状态。判断发送的模板内容
        BaseContextHandler.setTenant(tenantCode);
        Integer status = appointment.getStatus();
        R<Patient> patientR = patientApi.get(appointment.getPatientId());
        if (!patientR.getIsSuccess()) {
            return;
        }
        Patient patient = patientR.getData();
        if (Objects.isNull(patient)) {
            return;
        }
        if (Objects.isNull(tenant)) {
            R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
            if (!tenantR.getIsSuccess()) {
                return;
            }
            tenant = tenantR.getData();
        }
        if (Objects.isNull(tenant)) {
            return;
        }

        // TODO: 先取消 个人服务号的模版推送
        if (tenant.isPersonalServiceNumber()) {
            return;
        }

        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(tenantCode, null, TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE);
        if (!templateMsgDtoR.getIsSuccess() || templateMsgDtoR.getData() == null) {
            return;
        }
        TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
        Boolean commonCategory = msgDtoRData.getCommonCategory();
        String templateId = msgDtoRData.getTemplateId();
        if (AppointmentStatusEnum.NO_VISIT.getCode() == status
                || AppointmentStatusEnum.AUDIT_FAILED.getCode() == status
                || AppointmentStatusEnum.VISIT_EXPIRED.getCode() == status) {

            List<WxMpTemplateData> templateData = AppointmentMessageTemplate.appointmentTemplateMap.get(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE)
                    .initPatient(appointment, commonCategory, tenant.getDefaultLanguage());
            String patientBizUrl;
            // 预约结果通知模板给患者(预约成功)
            if (AppointmentStatusEnum.NO_VISIT.getCode() == status) {
                patientBizUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.PATIENT_RESERVATION);
            } else {
                // 预约结果通知模板给患者(预约失败)
                patientBizUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                        H5Router.PATIENT_RESERVATION_FAILED, "appointmentId="+ appointment.getId());
            }

            sendSystemMessageToPatient(appointment, patientBizUrl, patient.getId(), tenantCode);
            weixinSender.sendAppointmentWeiXinMessage(tenant.getWxAppId(), tenantCode,
                    TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE, patient.getOpenId(), templateData, templateId, patientBizUrl);

        } else {
            return;
        }
        // 操作人是医助
        if (sendDoctor) {
            R<Doctor> doctorR = doctorApi.get(appointment.getDoctorId());
            //  预约结果通知模板给医生(已通过（已由医助处理）)
            if (!doctorR.getIsSuccess()) {
                return;
            }
            Doctor doctor = doctorR.getData();
            if (Objects.isNull(doctor) || StrUtil.isEmpty(doctor.getOpenId())) {
                return;
            }
            List<WxMpTemplateData> templateData = AppointmentMessageTemplate.appointmentTemplateMap.get(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE)
                    .initDoctor(appointment, isNursing, commonCategory, tenant.getDefaultLanguage());
            weixinSender.sendAppointmentWeiXinMessage(tenant.getWxAppId(), tenantCode,
                    TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE, doctor.getOpenId(), templateData,  templateId,null);
        }

    }

    /**
     * 清除医生的 已过期的预约
     * @param doctorIds
     */
    @Override
    public void clearAppoint(List<Long> doctorIds) {

        UpdateWrapper<Appointment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("doctor_id", doctorIds);
        updateWrapper.eq("status", AppointmentStatusEnum.VISIT_EXPIRED.getCode());
        updateWrapper.set("patient_delete_mark", 1);
        baseMapper.update(new Appointment(), updateWrapper);

    }


    /**
     * 检查医生的号源在 这个时间段是否还有剩余
     */
    private void checkDoctorCodeNumber(Appointment appointment) {
        Long doctorId = appointment.getDoctorId();
        AppointConfig config = appointConfigMapper.selectOne(Wraps.<AppointConfig>lbQ()
                .eq(AppointConfig::getDoctorId, doctorId)
                .last(" limit 0,1 ")
        );
        if (Objects.isNull(config)) {
            throw new BizException("您的号源设置异常，请重新确认是否同意预约");
        }
        LocalDate appointDate = appointment.getAppointDate();
        int week = appointDate.getDayOfWeek().getValue();
        Integer totalNum = getTotalNum(week, appointment.getTime(), config);
        Integer count = baseMapper.selectCount(Wraps.<Appointment>lbQ()
                .eq(Appointment::getDoctorId, doctorId)
                .eq(Appointment::getAppointDate, appointment.getAppointDate())
                .eq(Appointment::getTime, appointment.getTime())
                .in(Appointment::getStatus, AppointmentStatusEnum.NO_VISIT.getCode(), AppointmentStatusEnum.VISITED.getCode()));
        if (count == null) {
            count = 0;
        }
        if (totalNum - count <= 0) {
            throw new BizException("该时段已约满，是否增加1个号源并通过审核");
        }
    }


    private void cancelRedisExceed(Appointment appointment, String tenantCode) {
        LocalDateTime appointmentDate = appointment.getAppointmentDate();
        String appointRedisKey = getAppointmentRedisKey(appointmentDate);
        String member = getRedisMember(tenantCode, appointment.getId());
        redisTemplate.boundSetOps(appointRedisKey).remove(member);
    }

    /**
     * 审核时，当预约 状态已经被修改成下列状态是，产生异常
     * @param status
     */
    private void auditStatusError(Integer status) {
        if (AppointmentStatusEnum.CANCEL_VISIT.getCode() == status) {
            throw new BizException("预约被取消。审核失败");
        }
        if (AppointmentStatusEnum.AUDIT_FAILED.getCode() == status) {
            throw new BizException("预约已被拒绝。无需审核");
        }
        if (AppointmentStatusEnum.VISIT_EXPIRED.getCode() == status) {
            throw new BizException("预约已过期。无需审核");
        }
    }

    /**
     * @title 更新预约记录之前获取的预约记录锁
     * @author 杨帅 
     * @updateTime 2023/3/29 14:42 
     * @throws
     */
    private String getAppointmentLock(Long appointmentId) {
        return appointmentId.toString() + ":lock";
    }

    /**
     * 获取redis中的KEY
     * @param appointmentDate
     * @return
     */
    public String getAppointmentRedisKey(LocalDateTime appointmentDate) {
        String appointRedisKey = DateUtil.format(appointmentDate, "yyyyMMddHHmm");
        return Appointment.class.getSimpleName() + appointRedisKey;
    }

    /**
     * 获取redis中的member
     * @param tenantCode
     * @param id
     * @return
     */
    private String getRedisMember(String tenantCode, Long id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("tenantCode", tenantCode);
        jsonObject.set("id", id);
        return jsonObject.toString();
    }

    /**
     * @Author yangShuai
     * @Description 获取总号源 多少
     * @Date 2021/1/28 14:44
     *
     * @param week  从 1 开始。 周日为7
     * @param config
     * @return java.lang.Integer
     */
    @Override
    public Integer getTotalNum(Integer week, Integer time, AppointConfig config) {

        Integer totalNum = 0;
        switch (week) {
            case 1 :
                totalNum = time == 1 ? config.getNumOfMondayMorning() : config.getNumOfMondayAfternoon();
                break;
            case 2 :
                totalNum = time == 1 ? config.getNumOfTuesdayMorning() : config.getNumOfTuesdayAfternoon();
                break;
            case 3 :
                totalNum = time == 1 ? config.getNumOfWednesdayMorning() : config.getNumOfWednesdayAfternoon();
                break;
            case 4 :
                totalNum = time == 1 ? config.getNumOfThursdayMorning() : config.getNumOfThursdayAfternoon();
                break;
            case 5 :
                totalNum = time == 1 ? config.getNumOfFridayMorning() : config.getNumOfFridayAfternoon();
                break;
            case 6 :
                totalNum = time == 1 ? config.getNumOfSaturdayMorning() : config.getNumOfSaturdayAfternoon();
                break;
            case 7 :
                totalNum = time == 1 ? config.getNumOfSundayMorning() : config.getNumOfSundayAfternoon();
                break;
        }
        return totalNum;

    }
}
