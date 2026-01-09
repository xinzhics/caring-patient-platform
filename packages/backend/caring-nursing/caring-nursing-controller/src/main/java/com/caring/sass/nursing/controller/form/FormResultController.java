package com.caring.sass.nursing.controller.form;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.nursing.constant.MonitorQueryDateDTO;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.form.FormResultQueryMapper;
import com.caring.sass.nursing.dao.plan.PatientCustomPlanTimeMapper;
import com.caring.sass.nursing.dto.blood.BloodPressDTO;
import com.caring.sass.nursing.dto.blood.BloodPressTrendResult;
import com.caring.sass.nursing.dto.form.*;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.FormResultQuery;
import com.caring.sass.nursing.entity.form.PatientFormFieldReference;
import com.caring.sass.nursing.entity.plan.PatientCustomPlanTime;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.nursing.service.form.FormService;
import com.caring.sass.nursing.service.form.PatientFormFieldReferenceService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.vo.InjectionCalendar;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.repeat.annotation.RepeatSubmit;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 表单填写结果表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Validated
@RestController
@RequestMapping("/formResult")
@Api(value = "FormResult", tags = "表单填写结果表")
//@PreAuth(replace = "formResult:")
public class FormResultController extends SuperController<FormResultService, Long, FormResult, FormResultPageDTO, FormResultSaveDTO, FormResultUpdateDTO> {

    private final FormService formService;

    private final PlanService planService;

    private final PatientApi patientApi;

    private final PatientCustomPlanTimeMapper patientCustomPlanTimeMapper;

    private final PatientFormFieldReferenceService patientFormFieldReferenceService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    FormResultQueryMapper formResultQueryMapper;

    public FormResultController(FormService formService, PlanService planService, PatientApi patientApi,
                                PatientCustomPlanTimeMapper patientCustomPlanTimeMapper,
                                PatientFormFieldReferenceService patientFormFieldReferenceService) {
        this.formService = formService;
        this.planService = planService;
        this.patientCustomPlanTimeMapper = patientCustomPlanTimeMapper;
        this.patientApi = patientApi;
        this.patientFormFieldReferenceService = patientFormFieldReferenceService;
    }

    public void encryptedCurrentData(String tenantCode) {

        FormResultMapper resultMapper = SpringUtils.getBean(FormResultMapper.class);
        BaseContextHandler.setTenant(tenantCode);
        Page<FormResultQuery> page = new Page<>();
        int current = 1;
        page.setCurrent(current);
        page.setSize(50);
        LbqWrapper<FormResultQuery> lbqWrapper = Wraps.<FormResultQuery>lbQ();
        lbqWrapper.eq(FormResultQuery::getEncrypted, CommonStatus.NO);
        lbqWrapper.select(SuperEntity::getId, FormResultQuery::getJsonContent);
        do {
            formResultQueryMapper.selectPage(page, lbqWrapper);
            List<FormResultQuery> pageRecords = page.getRecords();
            if (CollUtil.isNotEmpty(pageRecords)) {
                FormResult result;
                for (FormResultQuery record : pageRecords) {
                    result = new FormResult();
                    result.setId(record.getId());
                    result.setJsonContent(record.getJsonContent());
                    result.setEncrypted(CommonStatus.YES);
                    resultMapper.updateById(result);
                }
            }
        } while (page.getCurrent() < page.getPages());

    }
    @ApiOperation(value = "加密已有数据")
    @GetMapping("encryptedCurrentData")
    public R<Boolean> encryptedCurrentData() {
//        R<List<Tenant>> allTenant = tenantApi.getAllTenant();
//        for (Tenant datum : allTenant.getData()) {
//            String code = datum.getCode();
//            encryptedCurrentData(code);
//        }
        return R.success(true);
    }




    @ApiOperation(value = "使用消息ID查询疾病信息，不存在则返回表单")
    @GetMapping("getHealthRecordByMessageId")
    public R<FormResult> getHealthRecordByMessageId(
            @RequestParam(value = "messageId") Long messageId) {
        FormResult formResult = baseService.getFormResultByMessageId(messageId);
        if (Objects.nonNull(formResult)) {
            return R.success(formResult);
        }
        Form form = formService.getFormByRedis(FormEnum.HEALTH_RECORD, null);
        formResult = new FormResult();
        if (Objects.nonNull(form)) {
            formResult.setFormId(form.getId());
            formResult.setBusinessId(form.getBusinessId());
            String fieldsJson = form.getFieldsJson();
            formResult.setCategory(FormEnum.HEALTH_RECORD);
            formResult.setName(form.getName());
            formResult.setFormId(form.getId());
            formResult.setOneQuestionPage(form.getOneQuestionPage());
            formResult.setJsonContent(fieldsJson);
        }
        formResult.setMessageId(messageId.toString());
        return R.success(formResult);
    }


    @ApiOperation(value = "患者的 基本信息或 最新的疾病信息")
    @GetMapping("byCategory/{patientId}")
    public R<FormResult> getFormResultByCategory(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "messageId", required = false) Long messageId,
            @RequestParam("formEnum") @NotNull(message = "表单类型不能为空") FormEnum formEnum) {
        FormResult formResult;
        if (messageId != null) {
            formResult = baseService.getFormResultByMessageId(messageId);
            if (Objects.nonNull(formResult)) {
                return R.success(formResult);
            }
        }
        formResult = baseService.getBasicOrHealthFormResult(patientId, formEnum);
        return R.success(formResult);
    }


    @ApiOperation(value = "患者疾病信息列表")
    @PostMapping("healthFormResultList")
    public R<IPage<FormResult>> healthFormResultList(@RequestBody PageParams<FormResultPageDTO> pageParams) {
        FormResultPageDTO model = pageParams.getModel();
        Long userId = model.getUserId();
        IPage<FormResult> buildPage = pageParams.buildPage();
        LbqWrapper<FormResult> wrapper = Wraps.<FormResult>lbQ()
                .eq(FormResult::getCategory, FormEnum.HEALTH_RECORD)
                .eq(FormResult::getUserId, userId)
                .eq(FormResult::getDeleteMark, 0)
                .orderByDesc(FormResult::getCreateTime);
        IPage<FormResult> resultIPage = baseService.page(buildPage, wrapper);
        return R.success(resultIPage);
    }


    @ApiOperation(value = "逻辑删除表单结果（疾病信息）")
    @PutMapping("updateForDeleteFormResult/{formResultId}")
    public R<Boolean> updateForDeleteFormResult(@PathVariable("formResultId") Long formResultId) {
        baseService.updateForDeleteFormResult(formResultId);
        return R.success(true);
    }

    @ApiOperation(value = "首次分阶段保存基本信息或疾病信息")
    @PostMapping("saveFormResultStage")
    public R<FormResult> saveFormResultStage(@RequestBody FormResult formResult) {
        baseService.saveFormResultStage(formResult);
        return R.success(formResult);
    }


    @ApiOperation(value = "检测表单结果是否还存在")
    @GetMapping("checkFormResultExist")
    public R<Boolean> checkFormResultExist(@RequestParam Long formResultId) {

        int count = baseService.count(Wraps.<FormResult>lbQ().eq(SuperEntity::getId, formResultId)
                .eq(FormResult::getDeleteMark, CommonStatus.NO));
        if (count > 0) {
            return R.success(true);
        }
        return R.success(false);

    }


    @RepeatSubmit(interval = 65)
    @PostMapping
    @ApiOperation("新增")
    @Override
    public R<FormResult> save(@RequestBody @Validated FormResultSaveDTO formResultSaveDTO) {
        return super.save(formResultSaveDTO);
    }

    /**
     * 如果患者是第一次或者还没有入组。
     * 需要将注册流程不展示的组件暂时移除
     * @param fieldsJson
     * @return
     */
    public List<FormField> cancelBaseInfoOrHealthField(String fieldsJson, Boolean defaultDoctorPatient, Integer completeEnterGroup) {

        List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
        List<FormField> returnFields = new ArrayList<>(formFields.size());
        if (CollUtil.isNotEmpty(formFields)) {
            for (FormField field : formFields) {

                // 题目要求只能默认医生的患者填写。
                Boolean defaultDoctorPatientNeedWrite = field.getDefaultDoctorPatientNeedWrite();
                if (defaultDoctorPatientNeedWrite != null && defaultDoctorPatientNeedWrite) {
                    if (defaultDoctorPatient) {
                        returnFields.add(field);
                    }
                    continue;
                }
                // 为入组。
                if (completeEnterGroup == 0) {
                    // 取消 入组时 不需要填写的字段。
                    if (field.getCancelRegistrationProcessAppear() == null || !field.getCancelRegistrationProcessAppear()) {
                        returnFields.add(field);
                    }
                } else {
                    returnFields.add(field);
                }
            }
        }
        return returnFields;
    }

    @ApiOperation(value = "根据表单类型，获取 患者的 基本信息或疾病信息")
    @GetMapping("getFromResultByCategory/completeEnterGroup/{patientId}")
    public R<FormResult> getFromByCategory(
            @PathVariable("patientId") Long patientId,
            @RequestParam("completeEnterGroup") Integer completeEnterGroup,
            @RequestParam(value = "defaultDoctorPatient", required = false) Boolean defaultDoctorPatient,
            @RequestParam(value = "messageId", required = false) Long messageId,
            @RequestParam("formEnum") @NotNull(message = "表单类型不能为空") FormEnum formEnum) {
        FormResult formResult;
        if (messageId != null) {
            formResult = baseService.getFormResultByMessageId(messageId);
            if (Objects.nonNull(formResult)) {
                return R.success(formResult);
            }
        }
        String userType = BaseContextHandler.getUserType();
        if (defaultDoctorPatient == null) {
            defaultDoctorPatient = false;
        }
        // 如果当前用户不是患者。那么设置 角色 为 默认医生的患者。这样医助 医生 项目端就可以看到 默认医生患者才能看到的组件
        if (!UserType.PATIENT.equals(userType)) {
            defaultDoctorPatient = true;
            completeEnterGroup = 1;
        }
        formResult = baseService.getBasicOrHealthFormResult(patientId, formEnum);
        if (formResult != null) {
            // 判断患者有么有注册成功。没有注册成功则需要忽略一部分组件
            List<FormField> formFields = JSON.parseArray(formResult.getJsonContent(), FormField.class);
            // 处理一下。多选题的 options 和 values 比对。 存在于values中的options需要设置select属性
            baseService.handleCheckBoxResult(formFields);
            String jsonString = JSON.toJSONString(formFields);
            List<FormField> fieldList = cancelBaseInfoOrHealthField(jsonString, defaultDoctorPatient, completeEnterGroup);

            // 脱敏： 当获取数据的用书是 机构管理员或超管端临时访问时，对患者姓名，手机号组件结果脱敏
            if (baseService.checkNeedDesensitization()) {
                baseService.desensitization(fieldList);
            }
            formResult.setFieldList(JSON.parseArray(JSON.toJSONString(fieldList)));
            formResult.setJsonContent(null);
            return R.success(formResult);
        }
        formResult = new FormResult();
        Form form = formService.getFormByRedis(formEnum, null);
        if (Objects.nonNull(form)) {
            formResult.setBusinessId(form.getBusinessId());
            String fieldsJson = form.getFieldsJson();
            formResult.setCategory(formEnum);
            formResult.setName(form.getName());
            formResult.setFormId(form.getId());
            formResult.setUserId(patientId);
            formResult.setOneQuestionPage(form.getOneQuestionPage());
            List<FormField> formFields = cancelBaseInfoOrHealthField(fieldsJson, defaultDoctorPatient, completeEnterGroup);
            formResult.setFieldList(JSON.parseArray(JSON.toJSONString(formFields)));
        }
        return R.success(formResult);
    }

    @ApiOperation(value = "根据表单类型，获取 患者的 基本信息或疾病信息")
    @GetMapping("getFromResultByCategory/{patientId}")
    public R<FormResult> getFromByCategory(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "messageId", required = false) Long messageId,
            @RequestParam("formEnum") @NotNull(message = "表单类型不能为空") FormEnum formEnum) {
        NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO(ListUtil.list(false, patientId), BaseContextHandler.getTenant());
        R<List<NursingPlanPatientBaseInfoDTO>> nursingPlan = patientApi.getBaseInfoForNursingPlan(patientDTO);
        // 默认已入组
        Integer completeEnterGroup = 1;
        Boolean defaultDoctorPatient = false;
        if (nursingPlan.getIsSuccess()) {
            List<NursingPlanPatientBaseInfoDTO> data = nursingPlan.getData();
            if (CollUtil.isNotEmpty(data) && data.size() > 0) {
                NursingPlanPatientBaseInfoDTO baseInfoDTO = data.get(0);
                completeEnterGroup = baseInfoDTO.getIsCompleteEnterGroup();
                if (completeEnterGroup == null || completeEnterGroup == 0) {
                    completeEnterGroup = 0;
                }
                defaultDoctorPatient = baseInfoDTO.getDefaultDoctorPatient();
            }
        }
        return getFromByCategory(patientId, completeEnterGroup, defaultDoctorPatient, messageId, formEnum);
    }

    @ApiOperation(value = "获取自定义随访的表单")
    @GetMapping("getCustomForm/{patientId}/{planId}")
    public R<Page<FormResult>> getCustomPlan(
            @PathVariable("patientId") Long patientId,
            @PathVariable("planId") Long planId,
            @RequestParam(value = "needContent", required = false) Boolean needContent,
            @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<FormResult> page = new Page<>(current, size);
        LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ()
                .eq(FormResult::getUserId, patientId)
                .eq(FormResult::getBusinessId, planId.toString())
                .orderByDesc(FormResult::getCreateTime)
                .select(FormResult::getBusinessId, FormResult::getCategory, FormResult::getFormId, FormResult::getName, FormResult::getMessageId, FormResult::getId, FormResult::getScoreQuestionnaire,
                        FormResult::getUserId, FormResult::getCreateTime, FormResult::getJsonContent, FormResult::getUpdateTime, FormResult::getFirstSubmitTime);
        baseService.page(page, lbqWrapper);

        baseService.checkFormResultSetScore(page.getRecords(), needContent);
        return R.success(page);
    }


    @ApiOperation(value = "根据护理计划ID或计划类型或消息ID获取表单结果")
    @GetMapping("getPlanForm")
    public R<FormResult> getPlanForm(@RequestParam(value = "planId",  required = false) Long planId,
                                     @RequestParam(value = "planType",  required = false) Integer planType,
                                     @RequestParam(value = "messageId", required = false) Long messageId) {
        FormResult formResult = null;
        if (messageId != null) {
            formResult = baseService.getFormResultByMessageId(messageId);
            if (Objects.nonNull(formResult)) {
                return R.success(formResult);
            }
        }
        if (Objects.nonNull(planId)) {
            formResult = baseService.getFormResultByBusinessId(planId, messageId);
            if (Objects.nonNull(formResult)) {
                return R.success(formResult);
            }
        }
        if (Objects.nonNull(planType)) {
            // 查询计划ID。 然后查询表单 给前端
            LbqWrapper<Plan> planLbqWrapper = Wraps.<Plan>lbQ().eq(Plan::getPlanType, planType).last(" limit 0, 1");
            Plan plan = planService.getOne(planLbqWrapper);
            if (Objects.isNull(plan)) {
                throw new BizException("护理计划不存在");
            }
            formResult = baseService.getFormResultByBusinessId(plan.getId(), messageId);
            if (Objects.nonNull(formResult)) {
                return R.success(formResult);
            }
        }
        throw new BizException("查询表单失败");
    }



    @ApiOperation(value = "获取自定随访的详情")
    @GetMapping("getCustomFormDetail/{planId}")
    public R<FormResult> getCustomPlan(@PathVariable("planId") Long planId, @RequestParam(value = "messageId", required = false) Long messageId) {
        FormResult formResult = null;
        if (messageId != null) {
            formResult = baseService.getFormResultByMessageId(messageId);
        }
        if (Objects.nonNull(formResult)) {
            return R.success(formResult);
        }
        formResult = baseService.getFormResultByBusinessId(planId, messageId);
        if (Objects.isNull(formResult)) {
            throw new BizException("表单不存在");
        }
        return R.success(formResult);

    }


    @ApiOperation(value = "监测指标表单")
    @GetMapping("monitoringIndicators/{planId}")
    public R<FormResult> monitoringIndicators(@PathVariable("planId") Long planId,
                                              @RequestParam(value = "patientId", required = true) Long patientId,
                                              @RequestParam(value = "messageId", required = false) Long messageId) {
        FormResult formResult = null;
        if (messageId != null) {
            formResult = baseService.getFormResultByMessageId(messageId);
            if (Objects.nonNull(formResult)) {
                return R.success(formResult);
            }
        }
        Form form = formService.getFormByRedis(null, planId.toString());
        List<PatientFormFieldReference> fieldReferences = patientFormFieldReferenceService.list(Wraps.<PatientFormFieldReference>lbQ()
                .eq(PatientFormFieldReference::getBusinessId, planId)
                .eq(PatientFormFieldReference::getPatientId, patientId));
        Map<String, PatientFormFieldReference> referenceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(fieldReferences)) {
            referenceMap = fieldReferences.stream().collect(Collectors.toMap(PatientFormFieldReference::getFieldId, item -> item));
        }
        if (Objects.nonNull(form)) {
            String fieldsJson = form.getFieldsJson();
            if (referenceMap.size() > 0) {
                List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
                for (FormField field : formFields) {
                    PatientFormFieldReference fieldReference = referenceMap.get(field.getId());
                    if (Objects.nonNull(fieldReference)) {
                        field.setReferenceValue(fieldReference.getReferenceValue());
                        field.setTargetValue(fieldReference.getTargetValue());
                    }
                }
                fieldsJson = JSON.toJSONString(formFields);
            }
            return R.success(FormResult.builder()
                    .businessId(form.getBusinessId())
                    .category(form.getCategory())
                    .oneQuestionPage(form.getOneQuestionPage())
                    .scoreQuestionnaire(form.getScoreQuestionnaire())
                    .formId(form.getId())
                    .jsonContent(fieldsJson)
                    .messageId(messageId == null ? "" : messageId.toString())
                    .name(form.getName())
                    .build());
        }
        return R.success(null);

    }


    @ApiOperation(value = "获取患者检验数据或健康日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页大小", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型：3是检验数据，1 血压监测, 5是健康日志", paramType = "query")
    })
    @PostMapping("getCheckData/{patientId}")
    public R<Page<FormResult>> getCheckData(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "type") @NotNull(message = "类型不能为空") Integer type) {
        boolean legal = (type.equals(PlanEnum.REVIEW_REMIND.getCode())
                || type.equals(PlanEnum.HEALTH_LOG.getCode()) || type.equals(PlanEnum.BLOOD_PRESSURE.getCode()));
        if (!legal) {
            return R.fail("类型错误");
        }

        LbqWrapper<Plan> planLbqWrapper = Wraps.<Plan>lbQ().eq(Plan::getPlanType, type);
        List<Plan> planList = planService.list(planLbqWrapper);
        Page<FormResult> page = new Page<>(current, size);
        if (CollectionUtils.isEmpty(planList)) {
            return R.success(page);
        }
        Long businessId = planList.get(0).getId();
        LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ()
                .eq(FormResult::getUserId, patientId)
                .eq(FormResult::getBusinessId, businessId.toString())
                .orderByDesc(FormResult::getCreateTime);
        baseService.page(page, lbqWrapper);
        return R.success(page);
    }

    @ApiOperation("获取血压心率走势APP调用。")
    @GetMapping("loadMyBloodPressureTrendData/{patientId}")
    public R<BloodPressTrendResult> loadMyBloodPressureTrendData(@PathVariable("patientId") Long patientId,
                                                                 @RequestParam(value = "needAll", required = false) Boolean needAll) {
        List<String> cs = new ArrayList<>();
        BloodPressTrendResult results = new BloodPressTrendResult(cs, cs, cs, cs);
        try {
            if (null == needAll) {
                needAll = false;
            }
            results = baseService.getPatientBloodPressTrandDatas(patientId, needAll);
        } catch (Exception e) {
            return R.success(results);
        }
        return R.success(results);
    }

    @ApiOperation("获取血压心率走势（匹配数据）")
    @GetMapping("getPatientBloodPress/{patientId}")
    public R<List<BloodPressDTO>> getPatientBloodPress(@PathVariable("patientId") Long patientId) {
        List<BloodPressDTO> results = baseService.getPatientBloodPress(patientId);
        return R.success(results);
    }

    @ApiOperation("获取血压心率走势列表(匹配数据）")
    @GetMapping("patientBloodPressList/{patientId}")
    public R<List<Map<String, Object>>> patientBloodPressList(@PathVariable("patientId") Long patientId) {
        List<Map<String, Object>> results = baseService.patientBloodPressList(patientId);
        return R.success(results);
    }

    @ApiOperation(value = "获取一个表单结果详情且字段被格式化")
    @GetMapping("getDetail/{formResultId}")
    public R<FormResult> getDetail(@PathVariable("formResultId") Long formResultId) {
        FormResult formResult = baseService.getById(formResultId);
        List<FormField> formFields = JSON.parseArray(formResult.getJsonContent(), FormField.class);
        // 处理一下。多选题的 options 和 values 比对。 存在于values中的options需要设置select属性
        baseService.handleCheckBoxResult(formFields);
        formResult.setFieldList(formFields);
        return R.success(formResult);
    }

    @ApiOperation(value = "批量查询", notes = "批量查询")
    @PostMapping("/query")
    @SysLog("批量查询")
    @Override
    public R<List<FormResult>> query(@RequestBody FormResult data) {
        QueryWrap<FormResult> wrapper = Wraps.q(data);
        List<FormResult> formList = baseService.list(wrapper);
        // 格式数据
        return success(formList);
    }

    @ApiOperation(value = "监测数据折线图新版-含监测事件")
    @PostMapping("monitorLineChart/{patientId}")
    public R<MonitorLineChart> monitorLineChart2_0(
            @PathVariable("patientId") Long patientId,
            @RequestParam("businessId") String businessId,
            @RequestBody @Validated MonitorDayParams monitorDayParams) {

        MonitorLineChart lineChart = baseService.monitorLineChart(patientId, businessId, monitorDayParams);

        return R.success(lineChart);

    }


    @ApiOperation(value = "监测数据折线图")
    @Deprecated
    @GetMapping("monitorLineChart/{patientId}")
    public R<MonitorLineChart> monitorLineChart(
            @PathVariable("patientId") Long patientId,
            @RequestParam("businessId") String businessId,
            @RequestParam("monitorDateLineType") MonitorDateLineType monitorDateLineType) {

        MonitorLineChart lineChart = baseService.monitorLineChart(patientId, businessId, monitorDateLineType);

        return R.success(lineChart);

    }


    @ApiOperation(value = "监测数据列表倒序")
    @PostMapping("monitorFormResult")
    public R<IPage<FormResult>> monitorFormResult(@RequestBody PageParams<MonitorFormResultPageDTO> params) {

        IPage<FormResult> page = params.buildPage();
        MonitorFormResultPageDTO paramsModel = params.getModel();
        MonitorQueryDateDTO monitorQueryDate = paramsModel.getMonitorQueryDate();
        LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ()
                .eq(FormResult::getUserId, paramsModel.getUserId())
                .eq(FormResult::getBusinessId, paramsModel.getBusinessId())
                .orderByDesc(SuperEntity::getCreateTime);
        LocalDateTime maxTime;
        LocalDateTime minTime;
        if (Objects.nonNull(monitorQueryDate)) {
            LocalDate oneDayDate = paramsModel.getOneDayDate();
            if (MonitorQueryDateDTO.CURRENT_DAY.equals(monitorQueryDate) && Objects.nonNull(oneDayDate)) {
                maxTime = LocalDateTime.of(oneDayDate, LocalTime.MAX);
                minTime = LocalDateTime.of(oneDayDate, LocalTime.MIN);
            } else if (MonitorQueryDateDTO.CUSTOM_DATE.equals(monitorQueryDate)) {
                LocalDate customizeStartDate = paramsModel.getCustomizeStartDate();
                LocalDate customizeEndDate = paramsModel.getCustomizeEndDate();
                if (Objects.isNull(customizeStartDate) || Objects.isNull(customizeEndDate)) {
                    throw new BizException("请选择自定义时间范围");
                }
                minTime = LocalDateTime.of(customizeStartDate, LocalTime.MIN);
                maxTime = LocalDateTime.of(customizeEndDate, LocalTime.MAX);
            } else  {
                maxTime = MonitorQueryDateDTO.getMaxTime(monitorQueryDate);
                minTime = MonitorQueryDateDTO.getMinTime(monitorQueryDate);
            }
        } else {
            monitorQueryDate = MonitorQueryDateDTO.CURRENT_DAY;
            maxTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            minTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        }
        if (!MonitorQueryDateDTO.ALL.equals(monitorQueryDate)) {
            // 大于
            lbqWrapper.gt(FormResult::getCreateTime, minTime);
            // 小于
            lbqWrapper.lt(FormResult::getCreateTime, maxTime);
        }
        IPage<FormResult> iPage = baseService.page(page, lbqWrapper);

        Boolean needContent = paramsModel.getNeedContent();
        // 检查表单是否是评分问卷。 是评分问卷，给设置评分结果。
        baseService.checkFormResultSetScore(iPage.getRecords(), needContent);
        return R.success(iPage);
    }

    @ApiOperation(value = "查询患者表单字段的基准值和目标值")
    @GetMapping("patientFormFieldReference")
    public R<List<PatientFormFieldReference>> getPatientFormFieldReference(@RequestParam Long patientId,
                                                                     @RequestParam String businessId) {

        List<PatientFormFieldReference> references = patientFormFieldReferenceService.list(Wraps.<PatientFormFieldReference>lbQ()
                .eq(PatientFormFieldReference::getBusinessId, businessId)
                .eq(PatientFormFieldReference::getPatientId, patientId));

        return R.success(references);

    }



    @ApiOperation(value = "医生更新患者表单字段的基准值和目标值")
    @PostMapping("updatePatientFormFieldReference")
    public R<PatientFormFieldReference> updatePatientFormFieldReference(@RequestBody PatientFormFieldReference patientFormFieldReference) {

        Long patientId = patientFormFieldReference.getPatientId();
        String businessId = patientFormFieldReference.getBusinessId();
        String fieldId = patientFormFieldReference.getFieldId();
        if (Objects.nonNull(patientId) && Objects.nonNull(businessId) && Objects.nonNull(fieldId)) {
            List<PatientFormFieldReference> references = patientFormFieldReferenceService.list(Wraps.<PatientFormFieldReference>lbQ()
                    .eq(PatientFormFieldReference::getBusinessId, businessId)
                    .eq(PatientFormFieldReference::getFieldId, fieldId)
                    .eq(PatientFormFieldReference::getPatientId, patientId));
            if (CollectionUtils.isEmpty(references)) {
                patientFormFieldReferenceService.save(patientFormFieldReference);
            } else {
                PatientFormFieldReference reference = references.get(0);
                reference.setReferenceValue(patientFormFieldReference.getReferenceValue());
                reference.setTargetValue(patientFormFieldReference.getTargetValue());
                patientFormFieldReferenceService.updateAllById(reference);
            }
        }
        return R.success(patientFormFieldReference);
    }




    @ApiOperation(value = "查询填写日期的注射表单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "患者ID", value = "patientId"),
            @ApiImplicitParam(name = "计划ID", value = "planId"),
            @ApiImplicitParam(name = "日期类型", value = "type", defaultValue = "month, day"),
            @ApiImplicitParam(name = "日期", value = "localDate"),
    })
    @GetMapping("findPatientInjectionCalendarFormResult")
    public R<List<FormResult>> findPatientInjectionCalendarFormResult(@RequestParam Long patientId,
                                                                      @RequestParam Long planId,
                                                                      @RequestParam String type,
                                                                      @RequestParam LocalDate localDate) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        if ("month".equals(type)) {
            LocalDate months = localDate.withDayOfMonth(1);
            LocalDate endMonths = months.plusMonths(1);
            startTime = LocalDateTime.of(months, LocalTime.MIN);
            endTime = LocalDateTime.of(endMonths, LocalTime.MIN);
        } else {
            startTime = LocalDateTime.of(localDate, LocalTime.MIN);
            endTime = LocalDateTime.of(localDate, LocalTime.MAX);
        }

        List<FormResult> formResults = baseService.list(Wraps.<FormResult>lbQ()
                .eq(FormResult::getUserId, patientId)
                .eq(FormResult::getBusinessId, planId.toString())
                .gt(FormResult::getCreateTime, startTime)
                .lt(FormResult::getCreateTime, endTime)
                .orderByDesc(FormResult::getCreateTime));
        return R.success(formResults);
    }


    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            System.out.println(i/7);
        }
    }

    @ApiOperation(value = "患者是否允许在这个日期添加")
    @PutMapping("checkPatientAbleAddFormResult")
    public R<Integer> checkPatientAbleAddFormResult(@RequestBody PatientAbleAddFormResultDTO patientAbleAddFormResultDTO) {

        // 使用患者最新的添加记录的日期
        // 不可填写的最小时间范围 随访计划 跟踪模式 设置的天数 + 超时天数
        // 不可填写的最大时间范围 随访计划 跟踪模式 设置的天数 + 超时天数 + 超时可恢复天数
        Long planId = patientAbleAddFormResultDTO.getPlanId();
        Long patientId = patientAbleAddFormResultDTO.getPatientId();
        LocalDate localDate = patientAbleAddFormResultDTO.getLocalDate();
        FormResult formResult = baseService.getOne(Wraps.<FormResult>lbQ()
                .select(SuperEntity::getId, FormResult::getCreateTime)
                .eq(FormResult::getBusinessId, planId.toString())
                .eq(FormResult::getUserId, patientId)
                .orderByDesc(FormResult::getCreateTime).last(" limit 0,1 "));
        if (Objects.isNull(formResult)) {
            return R.success(0);
        }
        LocalDateTime createTime = formResult.getCreateTime();
        Plan plan = planService.getById(planId);
        if (plan.getPlanModel() == 1) {
            Integer nextRemind = plan.getNextRemind();
            Integer timeOutRemind = plan.getTimeOutRemind();
            Integer timeoutRecovery = plan.getTimeoutRecovery();
            if (timeoutRecovery == 0) {
                return R.success(0);
            }
            if (timeOutRemind == null) {
                timeOutRemind = 0;
            }
            LocalDate startTime = createTime.toLocalDate().plusDays(nextRemind + timeOutRemind);
            LocalDate endTime = createTime.toLocalDate().plusDays(nextRemind + timeOutRemind + timeoutRecovery);
            if (localDate.isBefore(endTime) && localDate.isAfter(startTime)) {
                Integer number = nextRemind + timeOutRemind;
                int i = number / 7;
                return R.success(i);
            }

        } else {
            return R.success(0);
        }
        return R.success(0);
    }


    @ApiOperation(value = "查询注射表单的填写日期所在的日历")
    @GetMapping("findPatientInjectionCalendar")
    public R<InjectionCalendar> findPatientInjectionCalendar(@RequestParam Long patientId, @RequestParam Long planId, @RequestParam LocalDate localDate) {

        LocalDate months = localDate.withDayOfMonth(1);
        LocalDate endMonths = months.plusMonths(1);
        LocalDateTime startTime = LocalDateTime.of(months, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(endMonths, LocalTime.MIN);

        List<FormResult> formResults = baseService.list(Wraps.<FormResult>lbQ()
                .select(FormResult::getCreateTime, SuperEntity::getId)
                .eq(FormResult::getUserId, patientId)
                .eq(FormResult::getBusinessId, planId.toString())
                .gt(FormResult::getCreateTime, startTime)
                .lt(FormResult::getCreateTime, endTime));
        Set<LocalDate> set = new HashSet<>();
        if (CollUtil.isNotEmpty(formResults)) {
            formResults.forEach(item -> set.add(item.getCreateTime().toLocalDate()));
        }
        PatientCustomPlanTime customPlanTime = patientCustomPlanTimeMapper.selectOne(Wraps.<PatientCustomPlanTime>lbQ()
                .eq(PatientCustomPlanTime::getPatientId, patientId)
                .eq(PatientCustomPlanTime::getNursingPlantId, planId));

        InjectionCalendar calendar = new InjectionCalendar();
        ArrayList<LocalDate> list = new ArrayList<>(set);
        calendar.setLocalDateList(list);
        if (Objects.nonNull(customPlanTime)) {
            calendar.setNextRemindDate(customPlanTime.getNextRemindTime().toLocalDate());
        }
        return R.success(calendar);

    }


    @ApiOperation(value = "检查数据是否存在")
    @GetMapping("checkDataExist")
    public R<Boolean> checkDataExist(@RequestParam Long id) {

        int count = baseService.count(Wraps.<FormResult>lbQ().eq(SuperEntity::getId, id).eq(FormResult::getDeleteMark, 0));
        return R.success(count > 0);

    }

}
