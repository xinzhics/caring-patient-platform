package com.caring.sass.user.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dto.StatisticsDiagnosticResult;
import com.caring.sass.user.dto.StatisticsPatientDto;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Statistics
 * @Description
 * @Author yangShuai
 * @Date 2020/11/27 9:39
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("statistics")
@Api(value = "Statistics", tags = "统计患者")
public class StatisticsController {

    private final StatisticsService statisticsService;

    private final UserService userService;

    DictionaryItemService dictionaryItemService;

    private final CacheRepository cacheRepository;

    public StatisticsController(StatisticsService statisticsService,
                                UserService userService,
                                DictionaryItemService dictionaryItemService,
                                CacheRepository cacheRepository) {
        this.statisticsService = statisticsService;
        this.dictionaryItemService = dictionaryItemService;
        this.userService = userService;
        this.cacheRepository = cacheRepository;
    }

    @ApiOperation("医助基础信息统计")
    @GetMapping("/{nursingStaffId}")
    public R countNursingStaffPatient(@PathVariable("nursingStaffId") Long nursingStaffId) {

        List<DictionaryItem> dictionaryItems = dictionaryItemService.list(Wraps.lbQ());
        Map<String, String> dictionaryMap = new HashMap<>();
        for (DictionaryItem item : dictionaryItems) {
            dictionaryMap.put(item.getCode(), item.getName());
        }

        List<StatisticsPatientDto> countPatient = statisticsService.countPatient(nursingStaffId, null, dictionaryMap);

        List<StatisticsPatientDto> patientDtoList = statisticsService.countPatientSex(nursingStaffId, null);

        List<StatisticsPatientDto> patientDiagnosisId = statisticsService.countPatientDiagnosisId(nursingStaffId);

        List<StatisticsPatientDto> patientAge = statisticsService.countPatientAge(nursingStaffId, null);

        JSONObject js = new JSONObject();
        js.put("personCount", countPatient);

        if (!CollectionUtils.isEmpty(patientDtoList)) {
            js.put("man", 0);
            js.put("woman", 0);
            for (StatisticsPatientDto patientDto : patientDtoList) {
                if (patientDto.getKey() != null && patientDto.getKey().equals("0")) {
                    js.put("man", patientDto.getCount());
                }
                if (patientDto.getKey() != null && patientDto.getKey().equals("1")) {
                    js.put("woman", patientDto.getCount());
                }
            }
        }

        js.put("diagnosis", patientDiagnosisId);

        js.put("age", patientAge);

        return R.success(js);
    }

    @ApiOperation("医生基础信息统计")
    @GetMapping("countDoctorPatient/{doctorId}")
    public R countDoctorPatient(@PathVariable("doctorId") Long doctorId) {
        List<DictionaryItem> dictionaryItems = dictionaryItemService.list(Wraps.lbQ());
        Map<String, String> dictionaryMap = new HashMap<>();
        for (DictionaryItem item : dictionaryItems) {
            dictionaryMap.put(item.getCode(), item.getName());
        }

        List<StatisticsPatientDto> countPatient = statisticsService.countPatient(null, doctorId, dictionaryMap);

        List<StatisticsPatientDto> patientDtoList = statisticsService.countPatientSex(null, doctorId);

        List<StatisticsPatientDto> patientAge = statisticsService.countPatientAge(null, doctorId);

        JSONObject js = new JSONObject();
        js.put("personCount", countPatient);

        if (!CollectionUtils.isEmpty(patientDtoList)) {
            js.put("man", 0);
            js.put("woman", 0);
            for (StatisticsPatientDto patientDto : patientDtoList) {
                if (patientDto.getKey() != null && patientDto.getKey().equals("0")) {
                    js.put("man", patientDto.getCount());
                }
                if (patientDto.getKey() != null && patientDto.getKey().equals("1")) {
                    js.put("woman", patientDto.getCount());
                }
            }
        }

        js.put("age", patientAge);

        return R.success(js);
    }

    @ApiOperation(value = "项目管理员患者诊断类型统计")
    @GetMapping("/diagnosticTypeStatistics/{userId}")
    public R<StatisticsDiagnosticResult> diagnosticTypeStatistics(@PathVariable Long userId) {
        Map<String, Object> m = userService.getDataScopeById(userId);
        List<Long> orgIds = (List<Long>) m.get("orgIds");
        StatisticsDiagnosticResult diagnosticResult = statisticsService.diagnosticTypeStatistics(orgIds, null, null);
        return R.success(diagnosticResult);

    }

    @ApiOperation(value = "医助患者诊断类型统计")
    @GetMapping("/diagnosticTypeStatisticsNursing/{nursingId}")
    public R<StatisticsDiagnosticResult> diagnosticTypeStatisticsNursing(@PathVariable("nursingId") Long nursingId) {
        StatisticsDiagnosticResult diagnosticResult = statisticsService.diagnosticTypeStatistics(null, nursingId, null);
        return R.success(diagnosticResult);

    }


    @ApiOperation(value = "医生患者诊断类型统计")
    @GetMapping("/diagnosticTypeStatisticsDoctor/{doctorId}")
    public R<StatisticsDiagnosticResult> diagnosticTypeStatisticsDoctor(@PathVariable("doctorId") Long doctorId) {
        StatisticsDiagnosticResult diagnosticResult = statisticsService.diagnosticTypeStatistics(null, null, doctorId);
        return R.success(diagnosticResult);

    }

    @ApiOperation(value = "项目管理员用户统计", notes = "项目管理员用户统计")
    @GetMapping("/userStatistics")
    public R<Map<String, Object>> userStatistics() {
        Map<String, Object> m = userService.getDataScopeById(BaseContextHandler.getUserId());
        List<Long> orgIds = (List<Long>) m.get("orgIds");
        List<DictionaryItem> dictionaryItems = dictionaryItemService.list(Wraps.lbQ());
        Map<String, String> dictionaryMap = new HashMap<>();
        for (DictionaryItem item : dictionaryItems) {
            dictionaryMap.put(item.getCode(), item.getName());
        }
        Integer totalDoctor = statisticsService.findTotalDoctor(Wraps.<Doctor>lbQ().in(Doctor::getOrganId, orgIds));
        Integer totalNursingStaff = statisticsService.findTotalNursingStaff(Wraps.<NursingStaff>lbQ().in(NursingStaff::getOrganId, orgIds));

        // 会员数
        Integer totalPatient = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds));

        // 昨日新增
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.plusDays(-1);
        Integer lastDayPatient = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .between(Patient::getCreateTime, lastDay.with(LocalTime.MIN), lastDay.with(LocalTime.MAX)));
        // 本月新增
        LocalDateTime firstDayInMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        Integer thisMonthPatient = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .between(Patient::getCreateTime, firstDayInMonth.with(LocalTime.MIN), now));

        Integer loginDoctorYesterday = statisticsService.findLoginDoctor(Wraps.<Doctor>lbQ().in(Doctor::getOrganId, orgIds)
                .between(Doctor::getFirstLoginTime, lastDay.with(LocalTime.MIN), lastDay.with(LocalTime.MAX)));

        Integer loginDoctorMonth = statisticsService.findLoginDoctor(Wraps.<Doctor>lbQ().in(Doctor::getOrganId, orgIds)
                .between(Doctor::getFirstLoginTime, firstDayInMonth.with(LocalTime.MIN), now));

        // 完成注册
        Integer completeEnterGroup = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .eq(Patient::getStatus, 1));
        // 未完成注册
        Integer notCompleteEnterGroup = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .eq(Patient::getStatus, 0));
        List<Map<String, Object>> completeEnterGroupList = new ArrayList<>();
        String register = "register";
        String notregister = "notregister";
        Map<String, Object> m1 = MapUtil.of("name", StrUtil.isEmpty(dictionaryMap.get(register)) ? "已注册" : dictionaryMap.get(register));
        m1.put("value", completeEnterGroup);
        completeEnterGroupList.add(m1);

        Map<String, Object> m2 = MapUtil.of("name", StrUtil.isEmpty(dictionaryMap.get(notregister)) ? "未注册" : dictionaryMap.get(notregister));
        m2.put("value", notCompleteEnterGroup);
        completeEnterGroupList.add(m2);
        int totalEnterGroup = completeEnterGroup + notCompleteEnterGroup;
        BigDecimal s;
        if (totalEnterGroup == 0) {
            s = new BigDecimal(0);
        } else {
            s = NumberUtil.div(new BigDecimal(completeEnterGroup), new BigDecimal(completeEnterGroup + notCompleteEnterGroup));
        }
        // 注册占比
        String completeEnterGroupPercent = NumberUtil.formatPercent(s.doubleValue(), 2);

        // 取关占比
        // 已注册取关患者数
        Integer noSubscribePatient = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .eq(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE)
                .eq(Patient::getIsCompleteEnterGroup, 1));
        // 未注册取关患者数
        Integer noSubscribeEntryPatient = statisticsService.findTotalPatient(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .eq(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE)
                .eq(Patient::getIsCompleteEnterGroup, 0));
        List<Map<String, Object>> followList = new ArrayList<>();
        Map<String, Object> m3 = MapUtil.of("name", StrUtil.isEmpty(dictionaryMap.get(register)) ? "已注册取关" : dictionaryMap.get(register) + "取关");
        m3.put("value", noSubscribePatient);
        followList.add(m3);

        Map<String, Object> m4 = MapUtil.of("name",  StrUtil.isEmpty(dictionaryMap.get(notregister)) ? "未注册取关" : dictionaryMap.get(notregister) + "取关");
        m4.put("value", noSubscribeEntryPatient);
        followList.add(m4);
        int noSubscribe = noSubscribePatient + noSubscribeEntryPatient;

        // 会员取关率
        BigDecimal noSub;
        if (totalPatient.equals(0)) {
            noSub = new BigDecimal(0);
        } else {
            noSub = NumberUtil.div(new BigDecimal(noSubscribePatient + noSubscribeEntryPatient), new BigDecimal(totalPatient));
        }
        String noSubPercent = NumberUtil.formatPercent(noSub.doubleValue(), 2);

        Map<String, Object> ret = new HashMap<>();
        ret.put("totalDoctor", totalDoctor);
        ret.put("totalNursingStaff", totalNursingStaff);
        ret.put("totalPatient", totalPatient);
        ret.put("lastDayPatient", lastDayPatient);
        ret.put("loginDoctorYesterday", loginDoctorYesterday);
        ret.put("loginDoctorMonth", loginDoctorMonth);
        ret.put("thisMonthPatient", thisMonthPatient);
        ret.put("completeEnterGroupList", completeEnterGroupList);
        ret.put("followList", followList);
        ret.put("noSubPercent", noSubPercent);
        ret.put("totalEnterGroup", totalEnterGroup);
        ret.put("completeEnterGroupPercent", completeEnterGroupPercent);
        ret.put("noSubscribe", noSubscribe);
        return R.success(ret);
    }



    @ApiOperation(value = "根据医生id统计患者信息2.3以前接口", notes = "根据医生id统计患者信息")
    @GetMapping(value = "/statisticPatientByDoctorId/{doctorId}")
    public R<JSONObject> statisticPatientByDoctorId(@PathVariable("doctorId") Long doctorId) {
        JSONObject ret = statisticsService.statisticPatientByDoctorId(doctorId);
        return R.success(ret);
    }


    @ApiOperation(value = "医生患者统计dashboard", notes = "医生患者统计dashboard")
    @GetMapping(value = "/statisticDashboard/{doctorId}")
    public R<JSONObject> statisticDashboard(@PathVariable("doctorId") Long doctorId) {
        JSONObject ret = statisticsService.statisticDashboard(doctorId);
        return R.success(ret);
    }

}
