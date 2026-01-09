package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.drugs.PatientDayDrugsMapper;
import com.caring.sass.nursing.dto.drugs.CalendarVo;
import com.caring.sass.nursing.dto.drugs.CheckInNumVo;
import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.service.drugs.PatientDayDrugsService;

import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * <p>
 * 业务实现类
 * 患者每天的用药量记录（一天生成一次）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class PatientDayDrugsServiceImpl extends SuperServiceImpl<PatientDayDrugsMapper, PatientDayDrugs> implements PatientDayDrugsService {

    @Override
    public boolean save(PatientDayDrugs model) {
        LocalDate now = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0 ,0 ,0, 0);
        model.setCreateTime(localDateTime);
        baseMapper.insert(model);
        return true;
    }

    @Override
    public PatientDayDrugs createPatientDayDrugs(NursingPlanPatientBaseInfoDTO patientBaseInfo) {

        LbqWrapper<PatientDayDrugs> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(PatientDayDrugs::getPatientId, patientBaseInfo.getId());
        LocalDate now = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0 ,0 ,0, 0);
        lbqWrapper.eq(PatientDayDrugs::getCreateTime, localDateTime);
        PatientDayDrugs selectOne = baseMapper.selectOne(lbqWrapper);
        if (Objects.nonNull(selectOne)) {
            return selectOne;
        }
        selectOne = new PatientDayDrugs();
        selectOne.setCheckinedNumber(0);
        selectOne.setTakeDrugsCountOfDay(0.0F);
        selectOne.setPatientId(patientBaseInfo.getId());
        selectOne.setDoctorId(patientBaseInfo.getDoctorId());
        selectOne.setServiceAdvisorId(patientBaseInfo.getServiceAdvisorId());
        selectOne.setCreateTime(localDateTime);
        selectOne.setDrugsCountOfDay(0.0F);

        LbqWrapper<PatientDrugs> drugsLbqWrapper = new LbqWrapper<>();
        drugsLbqWrapper.eq(PatientDrugs::getPatientId, patientBaseInfo.getId());
        drugsLbqWrapper.eq(PatientDrugs::getStatus, 0);


        selectOne.setCheckinNumberTotal(0);
        baseMapper.insert(selectOne);
        return selectOne;
    }


    /**
     * @Author yangShuai
     * @Description 用药日历
     * @Date 2020/11/11 15:18
     *
     * @return com.caring.sass.nursing.dto.drugs.CalendarVo
     */
    @Override
    public CalendarVo calendar(String time, Long patientId) {
        CalendarVo calendarVo = new CalendarVo();
        List<PatientDayDrugs> list = baseMapper.calendar(patientId, time);
        calendarVo.setCalendar(list);
        calendarVo.setCheckInNumVo(checkInNumVo(patientId));
        return calendarVo;
    }

    /**
     * 查询患者开始时间和结束时间之间的打卡情况
     * @param startTime
     * @param now
     * @param patientId
     * @return
     */
    @Override
    public CalendarVo calendar(LocalDate startTime, LocalDate now, Long patientId) {
        CalendarVo calendarVo = new CalendarVo();
        LocalDateTime min = LocalDateTime.of(startTime, LocalTime.of(0,0,0));
        LocalDateTime max = LocalDateTime.of(now, LocalTime.of(23,59,59));
        LbqWrapper<PatientDayDrugs> wrapper = Wraps.<PatientDayDrugs>lbQ().gt(SuperEntity::getCreateTime, min)
                .le(SuperEntity::getCreateTime, max)
                .eq(PatientDayDrugs::getPatientId, patientId)
                .orderByDesc(SuperEntity::getCreateTime);
        List<PatientDayDrugs> list = baseMapper.selectList(wrapper);
        calendarVo.setCalendar(list);
        CheckInNumVo numVo = new CheckInNumVo();
        numVo.setToDayCheckIn(0);
        if (CollUtil.isNotEmpty(list)) {
            PatientDayDrugs drugs = list.get(0);
            if (drugs.getCreateTime().toLocalDate().equals(LocalDate.now())) {
                numVo.setToDayCheckIn(drugs.getStatus());
            }
        }
        calendarVo.setCheckInNumVo(numVo);
        return calendarVo;
    }



    public CheckInNumVo checkInNumVo(Long patientId) {
        CheckInNumVo numVo = new CheckInNumVo();
        List<PatientDayDrugs> listCount = baseMapper.getToday(patientId, DateUtil.format(new Date(), "yyyy-MM-dd"));
        numVo.setToDayCheckIn(0);
        if (CollectionUtil.isNotEmpty(listCount)) {
            PatientDayDrugs patientDayDrugs = listCount.get(0);
            numVo.setToDayCheckIn(patientDayDrugs.getStatus());
        }
        return numVo;
    }


    @Override
    public List<PatientDayDrugs> getPatientDayDrugs(LocalDate now) {
        return baseMapper.getPatientDayDrugs(now);
    }

    /**
     * 患者 在这天是否有吃药推送
     * @param patientId
     * @param drugsId
     * @param now
     * @return
     */
    @Override
    public boolean existPatientDrugsToday(Long patientId, Long drugsId, LocalDate now) {
        LbqWrapper<PatientDayDrugs> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(PatientDayDrugs::getPatientId, patientId);
        LocalDateTime localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0 ,0 ,0, 0);
        lbqWrapper.eq(PatientDayDrugs::getCreateTime, localDateTime);
        Integer integer = baseMapper.selectCount(lbqWrapper);
        if (integer == null || integer == 0) {
            return false;
        } else {
            return true;
        }
    }
}
