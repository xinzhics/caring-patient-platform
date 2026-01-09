package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.api.DictionaryItemApi;
import com.caring.sass.authority.api.HospitalApi;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.entity.common.Hospital;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.dto.ChatPatientPageDTO;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.nursing.constant.FormResultExportConstant;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.dao.form.*;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.form.FormResultPageDTO;
import com.caring.sass.nursing.entity.form.*;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.exoprt.mentalPatientTracking.*;
import com.caring.sass.nursing.service.exoprt.minning.ChronicUrticariaQualityLifeQuestionnaire;
import com.caring.sass.nursing.service.exoprt.minning.MinNingUrticariaExport;
import com.caring.sass.nursing.service.exoprt.minning.UrticariaActivityScoreSelfEvaluation;
import com.caring.sass.nursing.service.form.FormResultExportService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.util.PlanDict;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.DoctorPageDTO;
import com.caring.sass.user.dto.NursingStaffPageDTO;
import com.caring.sass.user.dto.PatientPageDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统 表单结果导出任务执行
 */
@Slf4j
@Component
public class FormResultExportTask {

    @Autowired
    PatientApi patientApi;

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    NursingStaffApi nursingStaffApi;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    ImApi imApi;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    UserService userService;

    @Autowired
    FormResultExportService formResultExportService;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    FormScoreRuleMapper formScoreRuleMapper;

    @Autowired
    FormFieldsGroupMapper formFieldsGroupMapper;

    @Autowired
    FormResultScoreMapper formResultScoreMapper;

    @Autowired
    DictionaryItemApi dictionaryItemApi;


    @Autowired
    HospitalApi hospitalApi;

    /**
     * 执行一个导出任务
     * @param formResultExport
     */
    public void execute(FormResultExport formResultExport, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        Long userId = BaseContextHandler.getUserId();
        User user = userService.getById(userId);
        R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
        Tenant tenant = tenantR.getData();
        if (Objects.isNull(tenant)) {
            return;
        }

        SaasGlobalThreadPool.execute(() -> {
            export(tenantCode, tenant.getName(), tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), userId, user.getName(), formResultExport);
        });

    }

    /**
     * 导出表格
     * @param tenantCode
     * @param thirdAuthorization
     * @param currentUserId
     */
    public void export(String tenantCode,
                       String tenantName,
                       String tenantDomain,
                       boolean thirdAuthorization, Long currentUserId,
                       String userName,
                       FormResultExport formResultExport) {

        BaseContextHandler.setTenant(tenantCode);
        BaseContextHandler.setUserId(currentUserId);
        BaseContextHandler.setUserType(formResultExport.getCurrentUserType());
        // 计算导出的总数
        String exportFileName = formResultExport.getExportFileName();

        String property = System.getProperty("user.dir");
        String exportType = formResultExport.getExportType();
        File exportCSVFile;
        String path;
        if (FormResultExportConstant.minNing.toString().equals(exportType)) {
            String fileName = exportFileName.substring(0, exportFileName.indexOf(".csv"));
            path = property + "/"+ fileName + formResultExport.getId() + ".csv";
        } else if (FormResultExportConstant.MENTAL_PATIENT_TRACKING.toString().equals(exportType)) {
            String fileName = exportFileName.substring(0, exportFileName.indexOf(".xlsx"));
            path = property + "/"+ fileName + formResultExport.getId() + ".xlsx";
        } else  {
            String fileName = exportFileName.substring(0, exportFileName.indexOf(".xls"));
            path = property + "/"+ fileName + formResultExport.getId() + ".xls";
        }
        exportCSVFile = new File(path);
        StringBuilder message = new StringBuilder();
        if (!exportCSVFile.exists()) {
            try {
                exportCSVFile.createNewFile();
            } catch (IOException e) {
                log.error("{}", e.fillInStackTrace().toString());
                message.append("创建文件失败");
            }
        }

        if (FormResultExportConstant.baseInfo.toString().equals(exportType)) {
            exportPatientBaseInfo(tenantCode, tenantName, userName, formResultExport, exportCSVFile, message);
        } else if (FormResultExportConstant.follow_up.toString().equals(exportType)) {
            exportPatientFormFollowUp(tenantCode, tenantName, userName, formResultExport, exportCSVFile, message);
        } else if (FormResultExportConstant.doctor.toString().equals(exportType)) {
            exportDoctor(tenantCode, tenantName, userName, formResultExport, exportCSVFile, message);
        } else if (FormResultExportConstant.nursing.toString().equals(exportType)) {
            exportNursing(tenantCode, tenantName, userName, formResultExport, exportCSVFile, message);
        } else if (FormResultExportConstant.imMsg.toString().equals(exportType)) {
            exportImMsg(tenantCode, tenantDomain, thirdAuthorization, userName, formResultExport, exportCSVFile, message);
        } else if (FormResultExportConstant.minNing.toString().equals(exportType)) {
            exportMinNingFormResult(tenantCode, currentUserId, formResultExport, exportCSVFile, message);
        } else if (FormResultExportConstant.MENTAL_PATIENT_TRACKING.toString().equals(exportType)) {
            exportMentalPatientTracking(tenantCode, formResultExport, exportCSVFile, message);
        }


    }

    /**
     * 组装一个精神病项目的患者的基本信息
     * @param record
     * @param doctorMap
     * @param hospitalMap
     * @return
     */
    private MentalPatientBaseEntity getMentalPatientBaseEntity(Patient record,  Map<Long, Doctor> doctorMap, Map<Long, Hospital> hospitalMap) {
        MentalPatientBaseEntity patientBaseEntity = new MentalPatientBaseEntity();
        patientBaseEntity.setPatientName(record.getName());
        patientBaseEntity.setPatientSex(record.getSex() == null ? "" : record.getSex().equals(0) ? "男" : record.getSex().equals(1) ? "女" : "");
        patientBaseEntity.setPatientAge(record.getBirthday());
        patientBaseEntity.setDoctor(record.getDoctorName());
        Doctor doctor = doctorMap.get(record.getDoctorId());
        if (Objects.isNull(doctor)) {
            R<Doctor> doctorR = doctorApi.get(record.getDoctorId());
            doctor = doctorR.getData();
            doctorMap.put(record.getDoctorId(), doctor);
        }
        if (Objects.nonNull(doctor)) {
            patientBaseEntity.setHospital(doctor.getHospitalName());
            if (doctor.getHospitalId() != null) {
                Hospital hospital = hospitalMap.get(doctor.getHospitalId());
                if (Objects.isNull(hospital)) {
                    R<Hospital> hospitalR = hospitalApi.getHospital(doctor.getHospitalId());
                    hospital = hospitalR.getData();
                }
                if (Objects.nonNull(hospital)) {
                    hospitalMap.put(doctor.getHospitalId(), hospital);
                    patientBaseEntity.setProvince(hospital.getProvinceName());
                    patientBaseEntity.setCity(hospital.getCityName());
                }
            }
        }
        return patientBaseEntity;
    }

    private void exportMentalPatientTracking(String tenantCode, FormResultExport formResultExport, File file,  StringBuilder message) {

        PageParams<PatientPageDTO> params = getPatientPageDTO(formResultExport);
        MentalPatientExl patientExl = new MentalPatientExl();
        XSSFWorkbook workbook = patientExl.getWorkbook();
        // 查询导出的护理计划ID
        // 约定护理计划的名称：
        // 疾病信息
        // 注射记录 he 长期跟踪用药信息
        String planName1 = "长期跟踪用药信息";
        String planName2 = "注射记录";
        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ()
                .select(SuperEntity::getId, Plan::getName)
                .in(Plan::getName, planName1, planName2)
                .eq(Plan::getStatus, CommonStatus.YES)
                .eq(Plan::getFollowUpPlanType, PlanDict.CARE_PLAN));
        List<String> planIds = new ArrayList<>();
        String zhu_she_ji_lu_plan_id = null;
        String chang_qi_yong_yao_gen_zong_plan_id = null;
        for (Plan plan : planList) {
            String planId = plan.getId().toString();
            planIds.add(planId);
            if (plan.getName().equals(planName2)) {
                zhu_she_ji_lu_plan_id = planId;
            }
            if (plan.getName().equals(planName1)) {
                chang_qi_yong_yao_gen_zong_plan_id = planId;
            }
        };

        int current = 1;
        long queryTotal = 0;
        // 患者的序号
        int serialNumber = 1;
        // 患者一共使用的最大行  使用这个行数 对患者的行 进行合并
        int valueStartRow = 2;
        boolean taskQuit = false;
        MentalPatientBaseEntity patientBaseEntity;
        List<MentalDiseaseExcelEntity> diseaseExcelEntities;
        List<MentalInjectionExcelEntity> injectionExcelEntities;
        List<MentalMedicationTracking> mentalMedicationTrackings;
        CellStyle cellStyle;
        Map<Long, Doctor> doctorMap = new HashMap<>();
        Map<Long, Hospital> hospitalMap = new HashMap<>();
        int patientColor = 0;
        while (true) {
            params.setCurrent(current);
            params.setSize(10);
            R<IPage<Patient>> exportPageWithScope = patientApi.exportPageWithScope(params);
            current++;
            Boolean success = exportPageWithScope.getIsSuccess();
            IPage<Patient> scopeData = null;
            long total;
            if (success) {
                scopeData = exportPageWithScope.getData();
                total = scopeData.getTotal();
                List<Patient> records = scopeData.getRecords();
                // 循环患者
                HSSFRow row;
                if (records.isEmpty()) {
                    break;
                }
                List<Long> patientIds = records.stream().map(SuperEntity::getId).collect(Collectors.toList());

                // 查询没有删除的疾病信息
                List<FormResult> healthRecords = formResultMapper.selectList(Wraps.<FormResult>lbQ()
                        .select(SuperEntity::getId, FormResult::getUserId, FormResult::getJsonContent, FormResult::getCreateTime)
                        .in(FormResult::getUserId, patientIds)
                        .eq(FormResult::getDeleteMark, CommonStatus.NO)
                        .eq(FormResult::getCategory, FormEnum.HEALTH_RECORD));

                // 查询注射记录，用药跟踪的表单结果
                List<FormResult> planFormResults = formResultMapper.selectList(Wraps.<FormResult>lbQ()
                        .select(SuperEntity::getId, FormResult::getUserId, FormResult::getBusinessId, FormResult::getJsonContent, FormResult::getCreateTime)
                        .in(FormResult::getUserId, patientIds)
                        .in(FormResult::getBusinessId, planIds));
                Map<Long, List<FormResult>> patientFormResultMap = planFormResults.stream().collect(Collectors.groupingBy(FormResult::getUserId));
                // 患者疾病信息
                Map<Long, List<FormResult>> patientDiseases = healthRecords.stream().collect(Collectors.groupingBy(FormResult::getUserId));
                for (Patient record : records) {
                    injectionExcelEntities = null;
                    mentalMedicationTrackings = null;
                    if (taskQuit(tenantCode, formResultExport.getId())) {
                        message.append("[redis中导出任务不存在]");
                        taskQuit = true;
                        break;
                    }
                    // 组装一个精神病项目的患者的基本信息
                    patientBaseEntity = getMentalPatientBaseEntity(record, doctorMap, hospitalMap);

                    // 患者的基本信息
                    List<FormResult> formResults = patientDiseases.get(record.getId());

                    // 组合出 疾病信息导出的信息
                    diseaseExcelEntities = MentalDiseaseExcelEntity.parseFromResult(formResults);

                    // 获取患者的 注射记录和 用药跟踪信息
                    List<FormResult> formResultList = patientFormResultMap.get(record.getId());
                    if (CollUtil.isNotEmpty(formResultList)) {
                        Map<String, List<FormResult>> businessIdMap = formResultList.stream().collect(Collectors.groupingBy(FormResult::getBusinessId));

                        // 获取注射记录信息，并组装出导出需要的信息
                        if (StrUtil.isNotEmpty(zhu_she_ji_lu_plan_id)) {
                            List<FormResult> results = businessIdMap.get(zhu_she_ji_lu_plan_id);
                            injectionExcelEntities = MentalInjectionExcelEntity.parseFromResult(results);
                        }

                        // 获取 用药跟踪 信息，并组装出导出需要的信息
                        if (StrUtil.isNotEmpty(chang_qi_yong_yao_gen_zong_plan_id)) {
                            List<FormResult> results = businessIdMap.get(chang_qi_yong_yao_gen_zong_plan_id);
                            mentalMedicationTrackings = MentalMedicationTracking.parseFromResult(results);
                        }
                    }
                    // 组装 ，整理。 导入exl 使用的这个患者的信息
                    if (patientColor == 0) {
                        cellStyle = patientExl.getCellValueStyle1();
                        patientColor = 1;
                    } else {
                        cellStyle = patientExl.getCellValueStyle2();
                        patientColor = 0;
                    }

                    valueStartRow = patientExl.setValue(valueStartRow, serialNumber, cellStyle, patientBaseEntity, diseaseExcelEntities, injectionExcelEntities, mentalMedicationTrackings);
                    serialNumber++;
                    queryTotal++;
                    // 快速更新 表格导出的 进度
                    // 总 需要导出 数量 total, 已经写入的数量
                    formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
                }
            }

            if (taskQuit) {
                break;
            }
            if (scopeData == null) {
                break;
            }
            if (current > scopeData.getPages()) {
                break;
            }
        }

        if (taskQuit) {
            file.delete();
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(formResultExport, file);

    }


    /**
     * 根据前端提供的数据。查询数据，并导出三种业务中的一种到csv表格
     * @param tenantCode
     * @param formResultExport
     * @param file
     * @param message
     */
    private void exportMinNingFormResult(String tenantCode, Long currentUserId,
                                         FormResultExport formResultExport, File file,  StringBuilder message) {

        BaseContextHandler.setTenant(tenantCode);
        String fangShi = formResultExport.getPlanIdArrayJson();
        Map<String, Object> dataScope = userService.getDataScopeById(currentUserId);
        List<Long> orgIds = (List<Long>) dataScope.get("orgIds");
        List<Plan> planList = new ArrayList<>();
        Map<String, List<String>> planMap = new HashMap<>();
        List<String> fangShiDataList = new ArrayList<>();
        List<String> planIdStrings = new ArrayList<>();
        if (fangShi.contains("荨麻疹活动评分-筛选期")) {
            List<String> planIdMapStrings = new ArrayList<>();
            fangShiDataList.add("筛选期");
            // 查询敏宁 自定义随访计划中 名字中有 筛选 的 计划。
            planList = planMapper.selectList(Wraps.<Plan>lbQ().like(Plan::getName, "筛选")
                    .eq(Plan::getStatus, CommonStatus.YES)
                    .eq(Plan::getFollowUpPlanType, PlanDict.CARE_PLAN));
            planList.forEach(item -> {
                planIdMapStrings.add(item.getId().toString());
                planIdStrings.add(item.getId().toString());
            });
            planMap.put("筛选期", planIdMapStrings);
        }
        if (fangShi.contains("荨麻疹活动评分-治疗期")) {
            List<String> planIdMapStrings = new ArrayList<>();
            fangShiDataList.add("治疗期");
            planList = planMapper.selectList(Wraps.<Plan>lbQ().like(Plan::getName, "治疗期")
                    .eq(Plan::getStatus, CommonStatus.YES)
                    .eq(Plan::getFollowUpPlanType, PlanDict.CARE_PLAN));
            planList.forEach(item -> {
                planIdMapStrings.add(item.getId().toString());
                planIdStrings.add(item.getId().toString());
            });
            planMap.put("治疗期", planIdMapStrings);
        }
        if (fangShi.contains("荨麻疹活动评分-随访期")) {
            List<String> planIdMapStrings = new ArrayList<>();
            fangShiDataList.add("随访期");
            planList = planMapper.selectList(Wraps.<Plan>lbQ().like(Plan::getName, "随访期")
                    .eq(Plan::getStatus, CommonStatus.YES)
                    .eq(Plan::getFollowUpPlanType, PlanDict.CARE_PLAN));
            planList.forEach(item -> {
                planIdMapStrings.add(item.getId().toString());
                planIdStrings.add(item.getId().toString());
            });
            planMap.put("随访期", planIdMapStrings);
        }
        if (fangShiDataList.isEmpty()) {
            message.append("不存在的导出类型");
            return;
        }
        if (CollUtil.isEmpty(planList)) {
            message.append("未查询到随访计划");
            return;
        }
        LbqWrapper<FormResult> wrapper = Wraps.<FormResult>lbQ()
                .select(FormResult::getUserId)
                .in(FormResult::getBusinessId, planIdStrings)
                .orderByAsc(FormResult::getCreateTime)
                .groupBy(FormResult::getUserId);
        if (CollUtil.isNotEmpty(orgIds)) {
            String idsJoin = ListUtils.getSqlIdsJoin(orgIds);
            wrapper.apply(" user_id in (SELECT p.id from u_user_patient p where p.org_id in ( " + idsJoin + "))");
        }
        List<FormResult> results = formResultMapper.selectList(wrapper);
        List<Long> userIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(results)) {
            results.forEach(item -> userIds.add(item.getUserId()));
        }
        boolean desensitization = checkNeedDesensitization(formResultExport);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            MinNingUrticariaExport.writeTitleToCsv(outputStream);
            // 使用 userIds 查詢患者的 基本信息。 查询患者的医生信息
            int total = userIds.size();
            int queryTotal = 0;
            if (CollUtil.isEmpty(userIds)) {
                formResultExport.setMessage(message.toString());
                uploadFile(formResultExport, file, true);
                return;
            }
            Integer serialNumber = 1;
            // 从患者的基本信息中。查询出
            for (Long userId : userIds) {
                if (taskQuit(tenantCode, formResultExport.getId())) {
                    message.append("[redis中导出任务不存在]");
                    break;
                }
                R<Patient> patientApiBaseInfoForStatisticsData = patientApi.getBaseInfoForStatisticsData(userId);
                Patient patient = patientApiBaseInfoForStatisticsData.getData();
                if (desensitization && Objects.nonNull(patient)) {
                    patient.setName(SensitiveInfoUtils.desensitizeName(patient.getName()));
                    patient.setMobile(SensitiveInfoUtils.desensitizePhone(patient.getMobile()));
                    patient.setServiceAdvisorName(SensitiveInfoUtils.desensitizeName(patient.getServiceAdvisorName()));
                    patient.setDoctorName(SensitiveInfoUtils.desensitizeName(patient.getDoctorName()));
                }
                String doctorName = "";
                if (Objects.nonNull(patient)) {
                    doctorName = patient.getDoctorName();
                }

                // 解析查出来 患者的 筛选号
                FormResult formResult = formResultMapper.selectOne(Wraps.<FormResult>lbQ()
                        .select(FormResult::getUserId, SuperEntity::getId, FormResult::getJsonContent)
                        .eq(FormResult::getCategory, FormEnum.BASE_INFO)
                        .eq(FormResult::getUserId, userId)
                        .last(" limit 0,1 "));
                String shaiXuanHao = "";
                if (Objects.nonNull(formResult)) {
                    shaiXuanHao = ExportFormFieldResultUtil.getShaiXuanHao(formResult.getJsonContent());
                    if (shaiXuanHao == null) {
                        shaiXuanHao = "";
                    }
                }

                for (String fangShiData : fangShiDataList) {
                    // 查询这个患者 在这次 导出的访视中
                    List<String> planIdsString = planMap.get(fangShiData);

                    results = formResultMapper.selectList(Wraps.<FormResult>lbQ()
                            .select(FormResult::getId, FormResult::getJsonContent, FormResult::getCreateTime, FormResult::getFirstSubmitTime)
                            .in(FormResult::getBusinessId, planIdsString)
                            .eq(FormResult::getUserId, userId)
                            .orderByAsc(FormResult::getCreateTime));

                    // 根据 查询的 随访结果。封装成 敏宁导出的数据体
                    if (fangShiData.equals("筛选期") || fangShiData.equals("治疗期")) {
                        List<UrticariaActivityScoreSelfEvaluation> selfEvaluations = MinNingUrticariaExport.buildScoreSelfEvaluation(results);
                        try {
                            serialNumber = MinNingUrticariaExport.writeDataToCsv(outputStream, fangShiData, doctorName, shaiXuanHao, serialNumber, selfEvaluations);
                        } catch (IOException e) {
                            e.printStackTrace();
                            log.error("MinNingUrticariaExport writeDataToCsv error");
                        }
                    } else {
                        List<ChronicUrticariaQualityLifeQuestionnaire> questionnaires = MinNingUrticariaExport.buildQualityLifeQuestionnaire(results);
                        try {
                            serialNumber = MinNingUrticariaExport.writeSuiFangDataToCsv(outputStream, fangShiData, doctorName, shaiXuanHao, serialNumber, questionnaires);
                        } catch (IOException e) {
                            e.printStackTrace();
                            log.error("MinNingUrticariaExport writeDataToCsv error");
                        }
                    }
                }

                queryTotal++;
                formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(formResultExport, file, true);
    }





    /**
     * 导入聊天记录
     * @param tenantCode
     * @param tenantDomain
     * @param b
     * @param userName
     * @param formResultExport
     * @param file
     * @param message
     */
    private void exportImMsg(String tenantCode, String tenantDomain, boolean b, String userName,
                             FormResultExport formResultExport, File file,  StringBuilder message) {

        ImMsgExportXls imMsgExportXls = new ImMsgExportXls();
        XSSFWorkbook wordBook = imMsgExportXls.getHSSWordBook("聊天记录表");

        boolean desensitization = checkNeedDesensitization(formResultExport);
        String title1;
        if (desensitization) {
            title1 = SensitiveInfoUtils.desensitizeName(formResultExport.getPatientName()) + "的聊天记录表信息表";
        } else {
            title1 = formResultExport.getPatientName() + "的聊天记录表信息表";
        }

        String title2 = "导出时间" + DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM) + " | 操作账号:" + userName;
        imMsgExportXls.setFieldName(title1, title2);
        BaseContextHandler.setTenant(tenantCode);
        XSSFSheet sheet = wordBook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        lastRowNum++;
        long numbering = -1;
        BaseContextHandler.setTenant(tenantCode);
        OutputStream outputStream = null;
        String cmsUrl = ApplicationDomainUtil.wxPatientBizUrl(tenantDomain, b, H5Router.CMS_DETAIL);
        imMsgExportXls.setSheetCellWidth(sheet);
        R<List<DictionaryItem>> tenantDict = dictionaryItemApi.queryTenantDict(tenantCode);
        Map<String, String> dictionaryItem = new HashMap<>();
        List<DictionaryItem> dictData = tenantDict.getData();
        if (CollUtil.isNotEmpty(dictData)) {
            dictionaryItem = dictData.stream().collect(Collectors.toMap(DictionaryItem::getCode, DictionaryItem::getName, (o1, o2) -> o2));
        }
        try {
            outputStream = new FileOutputStream(file);
            PageParams<ChatPatientPageDTO> params;
            int current = 1;
            long queryTotal = 0;
            ChatPatientPageDTO patientPageDTO = new ChatPatientPageDTO();
            patientPageDTO.setImAccount(formResultExport.getPatientImAccount());
            patientPageDTO.setCurrentUserType(formResultExport.getCurrentUserType());
            IPage<Chat> sendData = null;
            List<Chat> records = null;
            while (true) {
                params = new PageParams<>();
                params.setModel(patientPageDTO);
                params.setCurrent(current);
                params.setSize(100);
                R<IPage<Chat>> chatSend = imApi.getChatSend(params);
                current++;
                Boolean success = chatSend.getIsSuccess();
                long total;
                if (success) {
                    sendData = chatSend.getData();
                    total = sendData.getTotal();
                    if (numbering == -1) {
                        numbering = total;
                    }
                    records = sendData.getRecords();
                    if (CollUtil.isEmpty(records)) {
                        break;
                    }
                    for (Chat chat : records) {
                        queryTotal++;
                        XSSFRow row = sheet.createRow(lastRowNum);
                        imMsgExportXls.setValue(row, numbering, lastRowNum, chat, cmsUrl, dictionaryItem);
                        lastRowNum++;
                        numbering--;
                    }
                    // 快速更新 表格导出的 进度
                    // 总 需要导出 数量 total, 已经写入的数量
                    formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
                }
                if (taskQuit(tenantCode, formResultExport.getId())) {
                    message.append("[redis中任务不存在]");
                    break;
                }
                boolean lastPage = false;
                if (sendData == null) {
                    lastPage = true;
                }
                if (current > sendData.getPages()) {
                    lastPage = true;
                }
                if (lastPage) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                wordBook.write(outputStream);
                wordBook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadFile(formResultExport, file);

    }

    private void exportNursing(String tenantCode, String tenantName, String userName,
                               FormResultExport formResultExport, File file,  StringBuilder message) {
        NursingExportXls nursingExportXls = new NursingExportXls();
        XSSFWorkbook wordBook = nursingExportXls.getHSSWordBook("医助信息表");
        String title1 = tenantName + "医助信息表";
        String title2 = "时间范围: 全部 | 导出时间" + DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM) + " | 操作账号:" + userName;
        nursingExportXls.setFieldName(wordBook, title1, title2);
        BaseContextHandler.setTenant(tenantCode);
        PageParams<NursingStaffPageDTO> params;
        int current = 1;
        long queryTotal = 0;
        XSSFSheet sheet = wordBook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        boolean taskQuit = false;
        XSSFRow row;
        NursingStaffPageDTO pageDTO = new NursingStaffPageDTO();
        pageDTO.setCurrentUserType(formResultExport.getCurrentUserType());
        while (true) {
            params = new PageParams<>();
            params.setModel(pageDTO);
            params.setCurrent(current);
            params.setSize(100);
            R<IPage<NursingStaff>> exportPageWithScope = nursingStaffApi.pageWithScope(params);
            current++;
            Boolean success = exportPageWithScope.getIsSuccess();
            IPage<NursingStaff> scopeData = null;
            // 单数 双数 变换 获取不同的表格样式
            int idx = 0;
            long total;
            if (success) {
                scopeData = exportPageWithScope.getData();
                total = scopeData.getTotal();
                List<NursingStaff> records = scopeData.getRecords();
                if (CollUtil.isEmpty(records)) {
                    break;
                }
                for (NursingStaff record : records) {
                    if (taskQuit(tenantCode, formResultExport.getId())) {
                        message.append("[redis中任务不存在]");
                        taskQuit = true;
                        break;
                    }
                    lastRowNum++;
                    row = nursingExportXls.createRow(sheet ,lastRowNum);
                    CellStyle cellStyle = nursingExportXls.getCellStyle(idx);
                    if (idx == 0) {
                        idx = 1;
                    } else {
                        idx = 0;
                    }
                    nursingExportXls.setNursingValue(row, cellStyle, record);
                    queryTotal++;
                }
                // 快速更新 表格导出的 进度
                // 总 需要导出 数量 total, 已经写入的数量
                formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
            }
            if (taskQuit) {
                break;
            }
            boolean lastPage = false;
            if (scopeData == null) {
                lastPage = true;
            }
            if (current > scopeData.getPages()) {
                lastPage = true;
            }
            if (lastPage) {
                break;
            }

        }
        nursingExportXls.setSheetCellWidth(sheet);
        if (taskQuit) {
            file.delete();
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wordBook.write(outputStream);
            wordBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(formResultExport, file);



    }



    private void exportDoctor(String tenantCode, String tenantName, String userName,
                              FormResultExport formResultExport, File file,  StringBuilder message) {
        DoctorExportXls doctorExportXls = new DoctorExportXls();
        XSSFWorkbook wordBook = doctorExportXls.getHSSWordBook("医生信息表");
        String title1 = tenantName + "医生信息表";
        String title2 = "时间范围: 全部 | 导出时间" + DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM) + " | 操作账号:" + userName;
        doctorExportXls.setFieldName(wordBook, title1, title2);
        BaseContextHandler.setTenant(tenantCode);
        PageParams<DoctorPageDTO> params;
        int current = 1;
        long queryTotal = 0;
        XSSFSheet sheet = wordBook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        boolean taskQuit = false;
        XSSFRow row;
        DoctorPageDTO pageDTO = new DoctorPageDTO();
        pageDTO.setCurrentUserType(formResultExport.getCurrentUserType());
        while (true) {
            params = new PageParams<>();
            params.setModel(pageDTO);
            params.setCurrent(current);
            params.setSize(100);
            R<IPage<Doctor>> exportPageWithScope = doctorApi.pageWithScope(params);
            current++;
            Boolean success = exportPageWithScope.getIsSuccess();
            IPage<Doctor> scopeData = null;
            // 单数 双数 变换 获取不同的表格样式
            int doctorIdx = 0;
            long total;
            if (success) {
                scopeData = exportPageWithScope.getData();
                total = scopeData.getTotal();
                List<Doctor> records = scopeData.getRecords();
                if (CollUtil.isEmpty(records)) {
                    break;
                }
                for (Doctor record : records) {
                    if (taskQuit(tenantCode, formResultExport.getId())) {
                        message.append("[redis中任务不存在]");
                        taskQuit = true;
                        break;
                    }
                    lastRowNum++;
                    row = doctorExportXls.createRow(sheet ,lastRowNum);
                    CellStyle cellStyle = doctorExportXls.getCellStyle(doctorIdx);
                    if (doctorIdx == 0) {
                        doctorIdx = 1;
                    } else {
                        doctorIdx = 0;
                    }
                    doctorExportXls.setDoctorValue(row, cellStyle, record);
                    queryTotal++;
                }
                // 快速更新 表格导出的 进度
                // 总 需要导出 数量 total, 已经写入的数量
                formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
            }
            if (taskQuit) {
                break;
            }
            boolean lastPage = false;
            if (scopeData == null) {
                lastPage = true;
            }
            if (current > scopeData.getPages()) {
                lastPage = true;
            }
            if (lastPage) {
                break;
            }

        }
        doctorExportXls.setSheetCellWidth(sheet);
        if (taskQuit) {
            file.delete();
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wordBook.write(outputStream);
            wordBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(formResultExport, file);


    }

    /**
     * 设置查询患者的条件
     * @param formResultExport
     * @return
     */
    private PageParams<PatientPageDTO> getPatientPageDTO(FormResultExport formResultExport) {
        String pageQueryJson = formResultExport.getPageQueryJson();
        PageParams<PatientPageDTO> params = new PageParams<>();
        PatientPageDTO pageDTO = new PatientPageDTO();
        if (StrUtil.isNotEmpty(pageQueryJson)) {
            JSONObject object = JSONObject.parseObject(pageQueryJson);
            Object createTimeSt = object.get("createTime_st");
            Map<String, String> map = new HashMap(1);
            if (Objects.nonNull(createTimeSt)) {
                map.put("createTime_st", createTimeSt.toString());
            }
            Object createTimeEd = object.get("createTime_ed");
            if (Objects.nonNull(createTimeEd)) {
                map.put("createTime_ed", createTimeEd.toString());
            }
            pageDTO = JSON.parseObject(pageQueryJson, PatientPageDTO.class);
            params.setMap(map);
        }
        pageDTO.setCurrentUserType(formResultExport.getCurrentUserType());
        params.setModel(pageDTO);
        return params;
    }

    /**
     * 导出患者的随访计划
     * @param tenantName
     * @param userName
     * @param formResultExport
     * @param file
     */
    private void exportPatientFormFollowUp(String tenantCode, String tenantName, String userName,
                                           FormResultExport formResultExport, File file,  StringBuilder message) {
        PatientFormResultExportXls resultExportXls = new PatientFormResultExportXls();
        XSSFWorkbook wordBook = resultExportXls.getHSSWordBook("随访记录表");
        String title1 = tenantName + "会员随访记录表";
        String planIdArrayJson = formResultExport.getPlanIdArrayJson();
        List<Long> planIds = JSON.parseArray(planIdArrayJson, Long.class);
        if (CollUtil.isEmpty(planIds)) {
            message.append("[护理计划ID为空]");
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        List<Plan> plans = planMapper.selectList(Wraps.<Plan>lbQ().in(SuperEntity::getId, planIds));
        if (CollUtil.isEmpty(plans)) {
            message.append("[护理计划列表为空]");
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        Map<Long, String> planIdNameMap = plans.stream().collect(Collectors.toMap(SuperEntity::getId, Plan::getName));
        List<String> planNameList = new ArrayList<>();
        List<String> planIdsStringList = new ArrayList<>();
        for (Long planId : planIds) {
            String name = planIdNameMap.get(planId);
            if (StrUtil.isNotEmpty(name)) {
                planNameList.add(name);
                planIdsStringList.add(planId.toString());
            }
        }
        String planName = String.join("、", planNameList);
        String title2 = "数据范围: "+ planName +" | 导出时间" + DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM) + " | 操作账号:" + userName;
        List<Form> forms = new ArrayList<>();

        List<Form> formList = formMapper.selectList(Wraps.<Form>lbQ().in(Form::getBusinessId, planIdsStringList));
        Map<String, Form> formMap = formList.stream().collect(Collectors.toMap(Form::getBusinessId, item -> item, (o1, o2) -> o1));
        for (Long planId : planIds) {
            Form form = formMap.get(planId.toString());
            if (Objects.nonNull(form)) {
                forms.add(form);
            }
        }
        if (CollUtil.isEmpty(forms)) {
            message.append("[护理计划的表单为空]");
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }

        // 处理forms 。当form是评分问卷时，查询他的积分规则和分组设置
        List<Long> scoreQuesForm = new ArrayList<>();
        for (Form form : formList) {
            if (Objects.nonNull(form.getScoreQuestionnaire()) && form.getScoreQuestionnaire() == 1) {
                scoreQuesForm.add(form.getId());
            }
        }
        Map<Long, FormScoreRule> scoreRuleMap = new HashMap<>();
        Map<Long, List<FormFieldsGroup>> formFieldGroupMap = new HashMap<>();
        if (CollUtil.isNotEmpty(scoreQuesForm)) {
            List<FormScoreRule> formScoreRules = formScoreRuleMapper.selectList(Wraps.<FormScoreRule>lbQ().in(FormScoreRule::getFormId, scoreQuesForm));
            if (CollUtil.isNotEmpty(formScoreRules)) {
                scoreRuleMap = formScoreRules.stream().collect(Collectors.toMap(FormScoreRule::getFormId, item -> item, (o1, o2) -> o2));
            }
            List<FormFieldsGroup> fieldsGroups = formFieldsGroupMapper.selectList(Wraps.<FormFieldsGroup>lbQ().in(FormFieldsGroup::getFormId, scoreQuesForm));
            if (CollUtil.isNotEmpty(fieldsGroups)) {
                formFieldGroupMap = fieldsGroups.stream().collect(Collectors.groupingBy(FormFieldsGroup::getFormId));
            }
        }
        Form baseInfoForm = formMapper.selectOne(Wraps.<Form>lbQ().eq(Form::getCategory, FormEnum.BASE_INFO).last(" limit 1 "));
        if (Objects.isNull(baseInfoForm)) {
            message.append("[基本信息表单为空]");
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        resultExportXls.setTitle(wordBook, title1, title2, baseInfoForm, forms, scoreRuleMap, formFieldGroupMap);
        // 记录了每个表单的在表格中的知识写入的位置。
        Map<String, Integer> planFormFieldIdx = new HashMap<>();
        Integer lastCell = resultExportXls.setFieldName(wordBook, forms, baseInfoForm, planFormFieldIdx, scoreRuleMap, formFieldGroupMap);
        PageParams<PatientPageDTO> params = getPatientPageDTO(formResultExport);
        int current = 1;
        long queryTotal = 0;
        XSSFSheet sheet = wordBook.getSheetAt(0);
        // 患者的序号
        int patientIndex = 1;
        // 患者一共使用的最大行  使用这个行数 对患者的行 进行合并
        int lastRowNum = sheet.getLastRowNum();
        int patientStartRowNumber;
        int patientUseRowNumber = 0;
        boolean taskQuit = false;
        FormResult baseInfoFormResult;  // 患者基本信息
        while (true) {
            params.setCurrent(current);
            params.setSize(20);
            R<IPage<Patient>> exportPageWithScope = patientApi.exportPageWithScope(params);
            current++;
            Boolean success = exportPageWithScope.getIsSuccess();
            IPage<Patient> scopeData = null;
            // 单数 双数 变换 获取不同的表格样式
            int patientIdx = 0;
            long total;
            if (success) {
                scopeData = exportPageWithScope.getData();
                total = scopeData.getTotal();
                List<Patient> records = scopeData.getRecords();
                // 循环患者
                XSSFRow row;
                if (records.isEmpty()) {
                    break;
                }
                List<Long> patientIds = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
                List<FormResult> baseInfoResult = formResultMapper.selectList(Wraps.<FormResult>lbQ().in(FormResult::getUserId, patientIds).eq(FormResult::getCategory, FormEnum.BASE_INFO));
                Map<Long, FormResult> patientBaseInfoFormResult = baseInfoResult.stream().collect(Collectors.toMap(FormResult::getUserId, item -> item, (o1, o2) -> o2));
                for (Patient record : records) {
                    if (taskQuit(tenantCode, formResultExport.getId())) {
                        message.append("[redis中导出任务不存在]");
                        taskQuit = true;
                        break;
                    }
                    lastRowNum++;
                    row = resultExportXls.createRow(sheet, lastRowNum);
                    patientUseRowNumber++;
                    patientStartRowNumber = lastRowNum;
                    CellStyle cellStyle = resultExportXls.getCellStyle(patientIdx);
                    if (patientIdx == 0) {
                        patientIdx = 1;
                    } else {
                        patientIdx = 0;
                    }
                    // 先把患者信息写入进去
                    baseInfoFormResult = patientBaseInfoFormResult.get(record.getId());
                    resultExportXls.setPatientValue(row, cellStyle, patientIndex, record, baseInfoFormResult);
                    // 查询患者的 表单结果的记录。
                    int formResultStartRowIdx;
                    for (Long planId : planIds) {
                        if (taskQuit(tenantCode, formResultExport.getId())) {
                            message.append("[redis中导出任务不存在]");
                            taskQuit = true;
                            break;
                        }
                        formResultStartRowIdx = patientStartRowNumber;
                        Integer cellStartIdx = planFormFieldIdx.get(planId.toString());
                        // 一个计划一个计划的写入到表格中去
                        PageParams<FormResultPageDTO> formResultField;
                        int formResultCurrent = 1;
                        int formResultNo = 1;
                        LbqWrapper<FormResult> wrapper = Wraps.<FormResult>lbQ().eq(FormResult::getBusinessId, planId.toString())
                                .eq(FormResult::getUserId, record.getId())
                                .orderByAsc(FormResult::getCreateTime);
                        FormScoreRule formScoreRule = null;
                        List<FormFieldsGroup> formFieldsGroups = null;
                        FormResultScore resultScore;
                        while (true) {
                            if (taskQuit(tenantCode, formResultExport.getId())) {
                                message.append("[redis中导出任务不存在]");
                                taskQuit = true;
                                break;
                            }
                            formResultField = new PageParams<>();
                            formResultField.setModel(new FormResultPageDTO());
                            formResultField.setCurrent(formResultCurrent);
                            formResultField.setSize(5);
                            IPage<FormResult> formResultIPage = formResultField.buildPage();
                            formResultIPage = formResultMapper.selectPage(formResultIPage, wrapper);
                            formResultCurrent++;
                            List<FormResult> formResults = formResultIPage.getRecords();
                            if (CollUtil.isEmpty(formResults)) {
                                break;
                            }
                            FormResult result = formResults.get(0);
                            Map<Long, FormResultScore> resultScoreMap = new HashMap<>();
                            if (Objects.nonNull(result.getScoreQuestionnaire()) && result.getScoreQuestionnaire().equals(1)) {
                                List<Long> formResultIds = formResults.stream().map(SuperEntity::getId).collect(Collectors.toList());
                                List<FormResultScore> formResultScores = formResultScoreMapper.selectBatchIds(formResultIds);
                                if (CollUtil.isNotEmpty(formResultScores)) {
                                    resultScoreMap = formResultScores.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
                                }
                                formScoreRule = scoreRuleMap.get(result.getFormId());
                                formFieldsGroups = formFieldGroupMap.get(result.getFormId());
                            }

                            long formResultTotal = formResultIPage.getTotal();
                            int formResultTotalInt = Integer.parseInt(formResultTotal + "");
                            // 记录 患者的表单结果最多使用多少行
                            if (patientUseRowNumber < formResultTotalInt) {
                                patientUseRowNumber = formResultTotalInt;
                            }
                            for (FormResult formResult : formResults) {
                                resultScore = resultScoreMap.get(formResult.getId());
                                resultExportXls.setFormField(sheet, formResultStartRowIdx, cellStyle, cellStartIdx, formResultNo, formResult, formScoreRule, formFieldsGroups, resultScore);
                                formResultNo++;
                                formResultStartRowIdx++;
                            }
                        }
                        // 合并患者所在的信息行
                    }
                    // 设置每一cell的样式
                    resultExportXls.setCellStyle(sheet, patientStartRowNumber, patientUseRowNumber, cellStyle, lastCell);
                    // 设置每一列的 宽度
                    resultExportXls.setSheetCellWidth(sheet);
                    // 合并患者所在的信息行
                    resultExportXls.mergeCell(sheet, patientStartRowNumber, patientUseRowNumber);

                    patientIndex++;
                    lastRowNum+=(patientUseRowNumber-1);
                    patientUseRowNumber = 0;

                    queryTotal++;
                    // 快速更新 表格导出的 进度
                    // 总 需要导出 数量 total, 已经写入的数量
                    formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
                }
            }

            if (taskQuit) {
                break;
            }
            boolean lastPage = false;
            if (scopeData == null) {
                lastPage = true;
            }
            if (current > scopeData.getPages()) {
                lastPage = true;
            }
            if (lastPage) {
                break;
            }
        }

        if (taskQuit) {
            file.delete();
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wordBook.write(outputStream);
            wordBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(formResultExport, file);

    }


    private boolean taskQuit(String tenantCode, Long formResultExportId) {
        try {
            Boolean hasKey = redisTemplate.hasKey(SaasRedisBusinessKey.getTenantExportTaskId(tenantCode, formResultExportId));
            if (hasKey != null && !hasKey) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 检查当前用户的导出任务。数据是否要加密
     * @return
     */
    public boolean checkNeedDesensitization(FormResultExport formResultExport) {
        String userType = formResultExport.getCurrentUserType();
        if (UserType.ORGAN_ADMIN.equals(userType) || UserType.ADMIN_OPERATION.equals(userType)) {
            String tenant = BaseContextHandler.getTenant();
            R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
            if (securitySettings.getIsSuccess()) {
                Boolean data = securitySettings.getData();
                if (data == null || !data) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 导出患者的基本信息
     * @param tenantName
     * @param userName
     * @param formResultExport
     * @param file
     */
    private void exportPatientBaseInfo(String tenantCode, String tenantName, String userName,
                                       FormResultExport formResultExport, File file, StringBuilder message) {
        PatientFormExportXls formExportXls = new PatientFormExportXls();
        XSSFWorkbook wordBook = formExportXls.getHSSWordBook("基础信息表");
        String title1 = tenantName + "会员基础信息表";
        String title2 = "时间范围: 全部 | 导出时间" + DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM) + " | 操作账号:" + userName;
        String scopeArrayJson = formResultExport.getBaseInfoScopeArrayJson();
        List<String> strings = JSON.parseArray(scopeArrayJson, String.class);
        Form baseInfo = null;
        boolean hasBaseInfo = false;
        boolean hasHealthFile = false;
        Form healthFile = null;

        boolean desensitization = checkNeedDesensitization(formResultExport);

        for (String string : strings) {
            if (FormResultExportConstant.BASE_INFO.toString().equals(string)) {
                hasBaseInfo = true;
                baseInfo = formMapper.selectOne(Wraps.<Form>lbQ().eq(Form::getCategory, FormEnum.BASE_INFO).last(" limit 0,1 "));
            }
            if (FormResultExportConstant.HEALTH_RECORD.toString().equals(string)) {
                hasHealthFile = true;
                healthFile = formMapper.selectOne(Wraps.<Form>lbQ().eq(Form::getCategory, FormEnum.HEALTH_RECORD).last(" limit 0,1 "));
            }
        }
        formExportXls.setTitle(wordBook, title1, title2, baseInfo, healthFile);
        Integer lastCell = formExportXls.setFieldName(wordBook, baseInfo, healthFile);
        PageParams<PatientPageDTO> params = getPatientPageDTO(formResultExport);;
        int current = 1;
        long queryTotal = 0;
        XSSFSheet sheet = wordBook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        boolean taskQuit = false;
        while (true) {
            if (taskQuit(tenantCode, formResultExport.getId())) {
                message.append("[redis中任务不存在。任务退出执行]");
                taskQuit = true;
                break;
            }
            params.setCurrent(current);
            params.setSize(20);
            R<IPage<Patient>> exportPageWithScope = patientApi.exportPageWithScope(params);
            current++;
            Boolean success = exportPageWithScope.getIsSuccess();
            IPage<Patient> scopeData = null;
            // 单数 双数 变换 获取不同的表格样式
            int patientIdx = 0;
            long total;
            if (success) {
                scopeData = exportPageWithScope.getData();
                total = scopeData.getTotal();
                List<Patient> records = scopeData.getRecords();
                Set<Long> patientIds = records.stream().map(SuperEntity::getId).collect(Collectors.toSet());
                Map<Long, FormResult> baseInfosFormResult = new HashMap<>();
                Map<Long, FormResult> healthFilesFormResult = new HashMap<>();
                if (hasBaseInfo) {
                    List<FormResult> baseInfos = formResultMapper.selectList(Wraps.<FormResult>lbQ()
                            .select(SuperEntity::getId, FormResult::getJsonContent, FormResult::getUserId)
                            .in(FormResult::getUserId, patientIds)
                            .eq(FormResult::getCategory, FormEnum.BASE_INFO));
                    baseInfosFormResult = baseInfos.stream().collect(Collectors.toMap(FormResult::getUserId, item -> item, (o1, o2) -> o2));
                }
                if (hasHealthFile) {
                    QueryWrapper<FormResult> queryWrapper = new QueryWrapper<>();
                    queryWrapper.select(" max(id) id, user_id ");
                    queryWrapper.in("user_id", patientIds);
                    queryWrapper.eq("category", FormEnum.HEALTH_RECORD);
                    queryWrapper.eq("delete_mark", 0);
                    queryWrapper.groupBy("user_id");
                    List<Map<String, Object>> mapList = formResultMapper.selectMaps(queryWrapper);
                    List<Long> maxFormResultIds = new ArrayList<>();
                    for (Map<String, Object> objectMap : mapList) {
                        Object id = objectMap.get("id");
                        if (Objects.nonNull(id)) {
                            maxFormResultIds.add(Long.parseLong(id.toString()));
                        }
                    }
                    if (CollUtil.isNotEmpty(maxFormResultIds)) {
                        List<FormResult> healthFiles = formResultMapper.selectList(Wraps.<FormResult>lbQ()
                                .select(SuperEntity::getId, FormResult::getJsonContent, FormResult::getUserId)
                                .in(FormResult::getId, maxFormResultIds));
                        healthFilesFormResult = healthFiles.stream().collect(Collectors.toMap(FormResult::getUserId, item -> item, (o1, o2) -> o2));
                    }
                }

                // 查询这些患者的 基本信息 疾病信息。
                XSSFRow row;
                for (Patient record : records) {
                    if (taskQuit(tenantCode, formResultExport.getId())) {
                        message.append("[redis中任务不存在。任务退出执行]");
                        taskQuit = true;
                        break;
                    }
                    lastRowNum++;
                    try {
                        Long patientId = record.getId();
                        FormResult baseInfoForm = baseInfosFormResult.get(patientId);
                        FormResult healthFileFormResult = healthFilesFormResult.get(patientId);
                        row = formExportXls.createRow(sheet ,lastRowNum);
                        CellStyle cellStyle = formExportXls.getCellStyle(patientIdx);
                        if (patientIdx == 0) {
                            patientIdx = 1;
                        } else {
                            patientIdx = 0;
                        }
                        formExportXls.setValue(row, record, baseInfoForm, healthFileFormResult, desensitization);
                        formExportXls.setCellStyle(row, cellStyle, lastCell);
                        queryTotal++;
                        // 快速更新 表格导出的 进度
                        // 总 需要导出 数量 total, 已经写入的数量
                        formResultExportService.updateExportProgress(formResultExport.getId(), total, queryTotal);
                    } catch (Exception e) {
                        log.error("写入数据异常，写入患者名称: {}", record.getName());
                    }

                }
            }
            if (taskQuit) {
                break;
            }
            boolean lastPage = false;
            if (scopeData == null) {
                lastPage = true;
            }
            if (current > scopeData.getPages()) {
                lastPage = true;
            }
            if (lastPage) {
                break;
            }
        }
        if (taskQuit) {
            file.delete();
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wordBook.write(outputStream);
            wordBook.close();
        } catch (IOException e) {
            message.append("[数据写入到文件失败]");
            formResultExportService.updateExportMessage(formResultExport.getId(), message.toString());
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(formResultExport, file);


    }


    public void uploadFile(FormResultExport formResultExport, File file) {
        uploadFile(formResultExport, file, false);
    }
    /**
     * 上传文件到 华为云
     * @param formResultExport
     * @param file
     */
    public void uploadFile(FormResultExport formResultExport, File file, Boolean csv) {

        // 导出的表格上传到华为云
        try {
            String exportFileName = formResultExport.getExportFileName();
            String fileName;
            if (csv != null && csv) {
                fileName = exportFileName.substring(0, exportFileName.indexOf(".csv"));
                fileName = fileName + "_" +  formResultExport.getExportId();
            } else if (exportFileName.contains("xlsx")) {
                fileName = exportFileName.substring(0, exportFileName.indexOf(".xlsx"));
                fileName = fileName + "_" +  formResultExport.getExportId();
            } else {
                fileName = exportFileName.substring(0, exportFileName.indexOf(".xls"));
                fileName = fileName + "_" +  formResultExport.getExportId();
            }
            MockMultipartFile multipartFile = FileUtils.fileToFileItem(file);
            if (Objects.nonNull(multipartFile)) {
                R<com.caring.sass.file.entity.File> upload = fileUploadApi.uploadAppFile(3L, multipartFile, fileName);
                Boolean success = upload.getIsSuccess();
                if (success) {
                    com.caring.sass.file.entity.File uploadData = upload.getData();
                    String uploadDataUrl = uploadData.getUrl();
                    formResultExportService.updateExportResult(formResultExport.getId(), uploadDataUrl);
                }
            }
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }

    }



}
