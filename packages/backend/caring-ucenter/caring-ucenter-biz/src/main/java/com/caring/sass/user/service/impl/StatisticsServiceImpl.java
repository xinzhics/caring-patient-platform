package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.dto.StatisticsDiagnosticResult;
import com.caring.sass.user.dto.StatisticsPatientDto;
import com.caring.sass.user.dto.TenantStatisticsYData;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @ClassName StatisticeServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2020/11/27 9:42
 * @Version 1.0
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PatientMapper patientMapper;

    private final FormApi formApi;

    private final NursingStaffService nursingStaffService;

    private final DoctorService doctorService;

    private final PatientService patientService;

    public StatisticsServiceImpl(PatientMapper patientMapper, FormApi formApi, NursingStaffService nursingStaffService, DoctorService doctorService,
                                 PatientService patientService) {
        this.patientMapper = patientMapper;
        this.formApi = formApi;
        this.nursingStaffService = nursingStaffService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * @return int
     * @Author yangShuai
     * @Description 统计会员下患者的总人数
     * @Date 2020/11/27 9:45
     */
    @Override
    public List<StatisticsPatientDto> countPatient(Long objId, Long doctorId, Map<String, String> dictionaryMap) {
        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        List<StatisticsPatientDto> statisticsPatientDtos = new ArrayList<>();
        Integer integer = patientMapper.selectCount(lbqWrapper);
        StatisticsPatientDto patientDto = new StatisticsPatientDto();
        patientDto.setKey("total");
        patientDto.setName(dictionaryMap.get("patient") != null ? dictionaryMap.get("patient") + "总数" : "患者总数");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);

        // 本月第一天的0点
        LocalDate start = LocalDate.now();
        LocalDateTime startMouth = LocalDateTime.of(start.getYear(), start.getMonth(), 1, 0, 0, 0, 0);

        // 下个月的第一天 0点
        LocalDate end = LocalDate.now();
        end = end.plusMonths(1);
        LocalDateTime endMouth = LocalDateTime.of(end.getYear(), end.getMonth(), 1, 0, 0, 0, 0);

        lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        lbqWrapper.eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL);
        integer = patientMapper.selectCount(lbqWrapper);
        patientDto = new StatisticsPatientDto();
        patientDto.setKey("registeredPatient");
        patientDto.setName(dictionaryMap.get("register") != null && dictionaryMap.get("patient") != null ? dictionaryMap.get("register") + dictionaryMap.get("patient") : "注册患者");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);

        lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        lbqWrapper.eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL);
        lbqWrapper.between(Patient::getCompleteEnterGroupTime, startMouth, endMouth);
        integer = patientMapper.selectCount(lbqWrapper);
        patientDto = new StatisticsPatientDto();
        patientDto.setKey("registeredPatientCurrentMonth");
        patientDto.setName(dictionaryMap.get("register") != null && dictionaryMap.get("patient") != null ?
                dictionaryMap.get("register") + dictionaryMap.get("patient") + "本月新增" : "注册患者本月新增");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);

        lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        lbqWrapper.eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE);
        integer = patientMapper.selectCount(lbqWrapper);
        patientDto = new StatisticsPatientDto();
        patientDto.setKey("noRegisteredPatient");
        patientDto.setName(dictionaryMap.get("register") != null ? "未完成" + dictionaryMap.get("register") : "未完成注册");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);

        lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        lbqWrapper.eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE);
        lbqWrapper.between(Patient::getCreateTime, startMouth, endMouth);
        integer = patientMapper.selectCount(lbqWrapper);
        patientDto = new StatisticsPatientDto();
        patientDto.setKey("noRegisteredPatientCurrentMonth");
        patientDto.setName(dictionaryMap.get("register") != null ? "未完成" + dictionaryMap.get("register") + "本月新增" : "未完成注册本月新增");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);


        lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        lbqWrapper.eq(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE);
        integer = patientMapper.selectCount(lbqWrapper);
        patientDto = new StatisticsPatientDto();
        patientDto.setKey("noSubscribePatient");
        patientDto.setName(dictionaryMap.get("patient") != null ?  "取关" + dictionaryMap.get("patient") : "取关患者");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);

        lbqWrapper = new LbqWrapper<>();
        if (Objects.nonNull(objId)) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, objId);
        }
        if (Objects.nonNull(doctorId)) {
            lbqWrapper.eq(Patient::getDoctorId, doctorId);
        }
        lbqWrapper.eq(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE);
        lbqWrapper.between(Patient::getUnSubscribeTime, startMouth, endMouth);
        integer = patientMapper.selectCount(lbqWrapper);
        patientDto = new StatisticsPatientDto();
        patientDto.setKey("noSubscribePatientCurrentMonth");
        patientDto.setName("取关本月新增");
        patientDto.setCount(integer);
        statisticsPatientDtos.add(patientDto);

        return statisticsPatientDtos;
    }

    /**
     * @return int
     * @Author yangShuai
     * @Description 统计会员下 患者性别 男女人数
     * @Date 2020/11/27 9:46
     */
    @Override
    public List<StatisticsPatientDto> countPatientSex(Long nursingStaffId, Long doctorId) {
        if (Objects.nonNull(nursingStaffId)) {
            return patientMapper.countSexGroupBySex(nursingStaffId);
        }
        if (Objects.nonNull(doctorId)) {
            return patientMapper.countDoctorPatientSexGroupBySex(doctorId);
        }
        return null;
    }

    /**
     * @return java.util.List<com.caring.sass.user.dto.StatisticsPatientDto>
     * @Author yangShuai
     * @Description 统计专员下患者的诊断类型
     * @Date 2020/11/27 10:00
     */
    @Override
    public List<StatisticsPatientDto> countPatientDiagnosisId(Long objId) {
        List<StatisticsPatientDto> patientDtoList = patientMapper.countPatientDiagnosisId(objId);
        String tenant = BaseContextHandler.getTenant();
        String categorys = FormEnum.HEALTH_RECORD.getCode();
        R<List<Form>> apiFromByCategory = formApi.getFromByCategory(tenant, categorys);
        JSONObject diagnose = null;
        if (apiFromByCategory.getIsSuccess()) {
            List<Form> categoryData = apiFromByCategory.getData();
            for (Form categoryDatum : categoryData) {
                String fieldsJson = categoryDatum.getFieldsJson();
                JSONArray jsonArray = JSONArray.parseArray(fieldsJson);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSONObject.parseObject(o.toString());
                    String exactType = jsonObject.getString("exactType");
                    if (FormFieldExactType.DIAGNOSE.equals(exactType)) {
                        diagnose = jsonObject;
                        break;
                    }
                }
                if (diagnose != null) {
                    break;
                }
            }
        }
        // 根据表单组件的 诊断类型 匹配统计结果
        List<StatisticsPatientDto> statistics = new ArrayList<>();
        if (diagnose != null) {
            JSONArray options = diagnose.getJSONArray("options");
            options.forEach((line) -> {
                JSONObject jsonObject = (JSONObject) line;
                String id = jsonObject.getString("id");
                String attrValue = jsonObject.getString("attrValue");
                Boolean hasOption = false;
                StatisticsPatientDto patientDto = new StatisticsPatientDto();
                for (StatisticsPatientDto statisticsPatientDto : patientDtoList) {
                    if (statisticsPatientDto.getKey().equals(id)) {
                        patientDto.setName(attrValue);
                        patientDto.setKey(id);
                        patientDto.setCount(statisticsPatientDto.getCount());
                        statistics.add(patientDto);
                        hasOption = true;
                    }
                }
                if (!hasOption) {
                    patientDto = new StatisticsPatientDto();
                    patientDto.setKey(id);
                    patientDto.setName(attrValue);
                    patientDto.setCount(0);
                    statistics.add(patientDto);
                }
            });
            return statistics;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @return java.util.List<com.caring.sass.user.dto.StatisticsPatientDto>
     * @Author yangShuai
     * @Description 统计患者年龄段数量
     * @Date 2020/11/27 10:32
     */
    @Override
    public List<StatisticsPatientDto> countPatientAge(Long objId, Long doctorId) {
        List<StatisticsPatientDto> patientDtoList;
        if (Objects.nonNull(objId)) {
            patientDtoList = patientMapper.countPatientAge(objId);
        } else if (Objects.nonNull(doctorId)) {
            patientDtoList = patientMapper.countDoctorPatientAge(doctorId);
        } else {
            throw new BizException("医助医生id不能都为空");
        }
        StatisticsPatientDto.Age[] values = StatisticsPatientDto.Age.values();
        for (StatisticsPatientDto.Age value : values) {
            boolean has = false;
            for (StatisticsPatientDto patientDto : patientDtoList) {
                if (patientDto.getKey().equals(value.getCode())) {
                    patientDto.setName(value.getDesc());
                    has = true;
                    break;
                }
            }
            if (!has) {
                StatisticsPatientDto patientDto = new StatisticsPatientDto();
                patientDto.setKey(value.getCode());
                patientDto.setName(value.getDesc());
                patientDto.setCount(0);
                patientDtoList.add(patientDto);
            }
        }
        return patientDtoList;
    }


    @Override
    public Integer findTotalPatient(LbqWrapper<Patient> queryWrapper) {
        return patientMapper.selectCount(queryWrapper);
//        return patientMapper.selectCount(queryWrapper.eq(Patient::getStatus, 1));
    }

    public Integer findLoginDoctor(LbqWrapper<Doctor> queryWrapper) {
        return doctorService.count(queryWrapper);
    }

    @Override
    public Integer findTotalNursingStaff(LbqWrapper<NursingStaff> queryWrapper) {
        return nursingStaffService.count(queryWrapper);
    }

    @Override
    public Integer findTotalDoctor(LbqWrapper<Doctor> queryWrapper) {
        return doctorService.count(queryWrapper);
    }

    /**
     * 填充一下患者状态统计为null的问题
     * @param statusRet
     */
    private void fillZeroIfStatusNull(List<Map<String, Object>> statusRet) {
        // 检查数组是否含有某个元素
        boolean flag0 = false, flag1 = false,  flag2 = false;
        for (Map<String, Object> m : statusRet) {
            Integer status = Convert.toInt(m.get("status"));
            if (Objects.nonNull(status)) {
                if (status == 0) {
                    flag0 = true;
                    continue;
                }
                if (status == 1) {
                    flag1 = true;
                }
                if (status == 2) {
                    flag2 = true;
                }
            }
        }
        if (!flag0) {
            Map<String, Object> map0 = new HashMap<>(2);
            map0.put("status", 0);
            map0.put("total", 0);
            statusRet.add(map0);
        }
        if (!flag1) {
            Map<String, Object> map1 = new HashMap<>(2);
            map1.put("status", 1);
            map1.put("total", 0);
            statusRet.add(map1);
        }
        if (!flag2) {
            Map<String, Object> map2 = new HashMap<>(2);
            map2.put("status", 2);
            map2.put("total", 0);
            statusRet.add(map2);
        }
    }


    /**
     * 如果为空，填充0
     */
    private void fillZeroIfNull(List<Map<String, Object>> statusRet) {
        // 检查数组是否含有某个元素
        boolean flag0 = false, flag1 = false;
        for (Map<String, Object> m : statusRet) {
            Integer isCompleteEnterGroup = Convert.toInt(m.get("isCompleteEnterGroup"));
            if (Objects.nonNull(isCompleteEnterGroup)) {
                if (isCompleteEnterGroup == 0) {
                    flag0 = true;
                    continue;
                }
                if (isCompleteEnterGroup == 1) {
                    flag1 = true;
                }
            }
        }
        if (!flag0) {
            Map<String, Object> map0 = new HashMap<>(2);
            map0.put("isCompleteEnterGroup", 0);
            map0.put("total", 0);
            statusRet.add(map0);
        }
        if (!flag1) {
            Map<String, Object> map1 = new HashMap<>(2);
            map1.put("isCompleteEnterGroup", 1);
            map1.put("total", 0);
            statusRet.add(map1);
        }
    }

    /**
     * 统计诊断类型
     * @param orgIds
     * @param nursingId
     * @param doctorId
     */
    @Override
    public StatisticsDiagnosticResult diagnosticTypeStatistics(List<Long> orgIds, Long nursingId, Long doctorId) {

        R<JSONArray> diagnosis = formApi.getDiagnosis();
        // 诊断类型
        List<String> diagnoiIds = new ArrayList<>();
        List<String> xName = new ArrayList<>();
        int total = 0;
        List<TenantStatisticsYData> yDataList = new ArrayList<>();
        StatisticsDiagnosticResult result = new StatisticsDiagnosticResult();
        result.setYDataList(yDataList);
        if (diagnosis.getIsSuccess()) {
            JSONArray diagnosisData = diagnosis.getData();
            if (CollUtil.isNotEmpty(diagnosisData)) {
                for (Object datum : diagnosisData) {
                    if (Objects.nonNull(datum)) {
                        JSONObject parse = JSON.parseObject(JSON.toJSONString(datum));
                        Object id = parse.get("id");
                        if (Objects.nonNull(id)) {
                            diagnoiIds.add(id.toString());
                            Object name = parse.get("name");
                            xName.add(name.toString());
                        }
                    }
                }
            }
            if (CollUtil.isNotEmpty(diagnoiIds)) {
                // 统计一下 诊断类型下 的患者分组
                QueryWrap<Patient> wrapAllPush = Wraps.<Patient>q()
                        .select("diagnosis_id as diagnosisId", "count(*) countNum")
                        .in("diagnosis_id", diagnoiIds)
                        .groupBy("diagnosis_id");
                if (CollUtil.isNotEmpty(orgIds)) {
                    wrapAllPush.in("org_id", orgIds);
                }
                if (Objects.nonNull(nursingId)) {
                    wrapAllPush.eq("service_advisor_id", nursingId);
                }
                if (Objects.nonNull(doctorId)) {
                    wrapAllPush.eq("doctor_id", doctorId);
                }
                List<Map<String, Object>> all = patientMapper.selectMaps(wrapAllPush);
                Map<String, Object> diagnosisIdCountMap = new HashMap<>();
                for (Map<String, Object> t : all) {
                    diagnosisIdCountMap.put(Convert.toStr(t.get("diagnosisId")), t.get("countNum"));
                }
                result.setXName(xName);
                TenantStatisticsYData yData = new TenantStatisticsYData();
                yData.setName("数量");
                List<Integer> yDataNumber = new ArrayList<>();
                for (String diagnoiId : diagnoiIds) {
                    Object o = diagnosisIdCountMap.get(diagnoiId);
                    if (Objects.nonNull(o)) {
                        int anInt = Integer.parseInt(o.toString());
                        total += anInt;
                        yDataNumber.add(anInt);
                    } else {
                        yDataNumber.add(0);
                    }
                }
                yData.setyData(yDataNumber);
                yDataList.add(yData);
                TenantStatisticsYData yData2 = new TenantStatisticsYData();
                yData2.setName("占比");
                List<Integer> proportions = new ArrayList<>();
                for (String diagnoiId : diagnoiIds) {
                    Object o = diagnosisIdCountMap.get(diagnoiId);
                    if (Objects.nonNull(o)) {
                        proportions.add(BigDecimalUtils.proportion(new BigDecimal(o.toString()), new BigDecimal(total)));
                    } else {
                        proportions.add(0);
                    }
                }
                yData2.setyData(proportions);
                yDataList.add(yData2);
            }
        }
        return result;
    }

    @Override
    public JSONObject statisticPatientByDoctorId(Long userId, String dimension) {

        if ("all".equals(dimension)) {
            Doctor doctor = doctorService.getBaseDoctorAndImOpenById(userId);
            JSONArray countDiagnosisId;
            Integer total;
            List<Map<String, Object>> statusRet;
            JSONObject ret = new JSONObject();

            // 独立医生
            if (doctor != null && doctor.getIndependence() != null && doctor.getIndependence().equals(1)) {
                countDiagnosisId = patientService.countDiagnosisId(userId, UserType.UCENTER_DOCTOR);
                total = patientMapper.selectCount(Wraps.<Patient>lbQ()
                        .eq(Patient::getDoctorId, userId));
                ret.put("total", total);
                // 按患者状态查询所有患者
                statusRet = patientMapper.selectMaps(Wrappers.<Patient>query()
                        .select("status_ as status", "count(*) as total")
                        .groupBy("status_")
                        .eq("doctor_id", userId));
            } else {
                // 和非独立医生不一样
                countDiagnosisId = patientService.countDiagnosisId(userId, UserType.UCENTER_DOCTOR_GROUP);
                // 查询医生所有的患者
                String sql = " doctor_id in (select doctor_id from u_user_doctor_group where group_id in " +
                        "(select group_id from u_user_doctor_group where doctor_id = "+ userId+"))";
                total = patientMapper.selectCount(Wraps.<Patient>lbQ()
                        .apply(sql));
                ret.put("total", total);

                // 按患者状态查询所有患者
                statusRet = patientMapper.selectMaps(Wrappers.<Patient>query()
                        .select("status_ as status", "count(*) as total")
                        .groupBy("status_")
                        .apply(sql));
            }
            fillZeroIfStatusNull(statusRet);
            fillZeroIfNull(statusRet);
            ret.put("status", statusRet);

            ret.put("diagnosis", countDiagnosisId);
            return ret;
        } else {
            JSONObject ret = new JSONObject();
            // 查询医生所有的患者
            Integer total = patientMapper.selectCount(Wraps.<Patient>lbQ()
                    .eq(Patient::getDoctorId, userId));
            ret.put("total", total);

            // 按患者状态查询所有患者
            List<Map<String, Object>> statusRet = patientMapper.selectMaps(Wrappers.<Patient>query()
                    .select("status_ as status", "count(*) as total")
                    .groupBy("status_")
                    .eq("doctor_id", userId));

//            fillZeroIfNull(statusRet);
            fillZeroIfStatusNull(statusRet);
            ret.put("status", statusRet);
            JSONArray countDiagnosisId = patientService.countDiagnosisId(userId, UserType.UCENTER_DOCTOR);
            ret.put("diagnosis", countDiagnosisId);
            return ret;
        }
    }

    /**
     * saas 2.3 以前代码。
     * @param doctorId
     * @return
     */
    @Deprecated
    @Override
    public JSONObject statisticPatientByDoctorId(Long doctorId) {
        JSONObject ret = new JSONObject();
        // 查询医生所有的患者
        Integer total = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getDoctorId, doctorId));
        ret.put("total", total);

        // 按患者状态查询所有患者
        List<Map<String, Object>> statusRet = patientMapper.selectMaps(Wrappers.<Patient>query()
                .select("is_complete_enter_group as isCompleteEnterGroup", "count(*) as total")
                .groupBy("is_complete_enter_group")
                .eq("doctor_id", doctorId));

        fillZeroIfNull(statusRet);
        ret.put("status", statusRet);

        // 按诊断类型查询所有患者
        List<Map<String, Object>> diagnosisRet = patientMapper.selectMaps(Wrappers.<Patient>query()
                .select("diagnosis_name AS 'diagnosisName'", "count(*) as total")
                .isNotNull("diagnosis_name")
                .eq("doctor_id", doctorId)
                .groupBy("diagnosis_name"));
        ret.put("diagnosis", diagnosisRet);
        return ret;
    }

    @Override
    public JSONObject statisticDashboard(Long doctorId) {
        JSONObject ret = new JSONObject();
        // 患者总数
        Integer total = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getDoctorId, doctorId));
        ret.put("total", total);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beginOfMonth = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);

        // 注册患者
        Integer subscribeNum = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL)
                .eq(Patient::getDoctorId, doctorId));
        ret.put("subscribeNum", subscribeNum);

        // 注册转化率
        String conversionRate = "0%";
        if (total != 0) {
            conversionRate = NumberUtil.formatPercent(NumberUtil.div(subscribeNum, total, 2).doubleValue(), 2);
        }
        ret.put("conversionRate", conversionRate);

        // 本月新增注册患者
        Integer thisMonthSubscribeNum = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL)
                .between(Patient::getCreateTime, beginOfMonth, now)
                .eq(Patient::getDoctorId, doctorId));
        ret.put("thisMonthSubscribeNum", thisMonthSubscribeNum);

        // 未完成注册
        Integer unSubscribeNum = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE)
                .eq(Patient::getDoctorId, doctorId));
        ret.put("unSubscribeNum", unSubscribeNum);
        // 本月未完成注册
        Integer thisMonthUnSubscribeNum = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE)
                .between(Patient::getCreateTime, beginOfMonth, now)
                .eq(Patient::getDoctorId, doctorId));
        ret.put("thisMonthUnSubscribeNum", thisMonthUnSubscribeNum);

        // 取消关注
        Integer cancelSubscribe = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE)
                .eq(Patient::getDoctorId, doctorId));
        ret.put("cancelSubscribe", cancelSubscribe);
        // 本月取消关注
        Integer thisMonthCancelSubscribe = patientMapper.selectCount(Wraps.<Patient>lbQ()
                .eq(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE)
                .between(Patient::getCreateTime, beginOfMonth, now)
                .eq(Patient::getDoctorId, doctorId));
        ret.put("thisMonthCancelSubscribe", thisMonthCancelSubscribe);

        // 统计男女比例
        List<Map<String, Object>> sexDistribution = patientMapper.selectMaps(Wrappers.<Patient>query()
                .select("IF(sex=0, '男', '女') as sex", "count(*) as total")
                .groupBy("sex")
                .isNotNull("sex")
                .eq("doctor_id", doctorId));
        ret.put("sexDistribution", sexDistribution);


        // 统计年龄段分布
//        List<Map<String, Object>> ageDistribution = patientMapper.selectMaps(Wrappers.<Patient>query()
//                .select("CASE\n" +
//                        "\tWHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 18 THEN \"18岁以下\" \n" +
//                        "\tWHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 25 THEN \"18~24岁\" \n" +
//                        "\tWHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 30 THEN \"25~30岁\" \n" +
//                        "  WHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 50 THEN \"31~50岁\" \n" +
//                        "\tELSE \"51岁以上\" \n" +
//                        "\tEND AS 'age' ,\n" +
//                        "\tcount( 1 ) AS 'total' ")
//                .groupBy("age")
//                .isNotNull("birthday")
//                .eq("doctor_id", doctorId));
//        ret.put("ageDistribution", ageDistribution);

        List<StatisticsPatientDto> patientDtos = countPatientAge(null, doctorId);
        ret.put("ageDistribution", patientDtos);

        // 症状分布
        List<Map<String, Object>> diagnosisDistribution = patientMapper.selectMaps(Wrappers.<Patient>query()
                .select("diagnosis_name AS 'diagnosisName'", "count(*) as total")
                .eq("doctor_id", doctorId)
                .isNotNull("diagnosis_name")
                .groupBy("diagnosis_name"));
        ret.put("diagnosisDistribution", diagnosisDistribution);
        return ret;
    }
}
