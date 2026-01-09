package com.caring.sass.nursing.service.form.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.form.IndicatorsConditionMonitoringMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormResultPageDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.IndicatorsConditionMonitoring;
import com.caring.sass.nursing.entity.form.IndicatorsResultInformation;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.*;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.nursing.service.form.FormService;
import com.caring.sass.nursing.service.form.IndicatorsConditionMonitoringService;
import com.caring.sass.nursing.service.form.IndicatorsResultInformationService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.vo.IndicatorsConditionMonitoringVO;
import com.caring.sass.nursing.vo.IndicatorsPlanVo;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.BizAssert;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者管理-监测数据-监控指标条件表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-14
 */
@Slf4j
@Service
@AllArgsConstructor
public class IndicatorsConditionMonitoringServiceImpl extends SuperServiceImpl<IndicatorsConditionMonitoringMapper, IndicatorsConditionMonitoring> implements IndicatorsConditionMonitoringService {

    private final PlanService planService;
    private final FormService formService;
    private final FormResultService formResultService;
    private final IndicatorsResultInformationService indicatorsResultInformationService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PatientApi patientApi;
    /**
     * 查询患者管理-监测数据-监测计划
     * @param tenantCode
     * @return
     */
    @Override
    public List<IndicatorsPlanVo> getIndicatorsPlan(String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<Plan> lbqWrapper = new LbqWrapper();
        lbqWrapper.eq(Plan::getIsAdminTemplate, AdminTemplateEnum.OTHER_PLAN.getCode());
        lbqWrapper.eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.MONITORING_DATA.operateType);
        lbqWrapper.eq(Plan::getStatus, 1);
        lbqWrapper.orderByDesc(Plan::getCreateTime);
        List<Plan> plans = planService.list(lbqWrapper);
        if (CollectionUtils.isEmpty(plans)){
            return new ArrayList<>();
        }
        List<IndicatorsPlanVo> results =  plans.stream().map(i ->{
                    IndicatorsPlanVo indicatorsPlanVo= BeanUtil.toBean(i, IndicatorsPlanVo.class);
                    indicatorsPlanVo.setPlanId(i.getId());
                    indicatorsPlanVo.setPlanName(i.getName());
                    return indicatorsPlanVo;
                }
        ).collect(Collectors.toList());
        return results;
    }

    /**
     * 查询患者管理-监测数据-监测计划-监测指标及条件
     * @param tenantCode
     * @param planId
     * @return
     */
    @Override
    public List<IndicatorsConditionMonitoringVO> getIndicatorsConditionMonitoring(String tenantCode, Long planId) {
        BaseContextHandler.setTenant(tenantCode);
        List<IndicatorsConditionMonitoringVO> results = new ArrayList<>();
        Plan plan = planService.getOne(planId);
        BizAssert.notNull(plan, "计划不存在！");
        //获取当前计划的监控指标字段
        Form form = formService.getFormByRedis(null, planId.toString());
        BizAssert.notNull(form, "计划对应的表单不存在");
        List<FormField> formFields = JSON.parseArray(form.getFieldsJson(), FormField.class).stream().filter(field -> FormFieldExactType.MONITORING_INDICATORS.equals(field.getExactType())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(formFields)){
            return results;
        }
        for (FormField formField:formFields){
            IndicatorsConditionMonitoringVO indicatorsConditionMonitoringVO = new IndicatorsConditionMonitoringVO();
            indicatorsConditionMonitoringVO.setPlanId(plan.getId());
            indicatorsConditionMonitoringVO.setPlanName(plan.getName());
            indicatorsConditionMonitoringVO.setFormId(form.getId());
            indicatorsConditionMonitoringVO.setFieldId(formField.getId());
            indicatorsConditionMonitoringVO.setFieldLabel(formField.getLabel());
            indicatorsConditionMonitoringVO.setRealWriteValueRightUnit(formField.getRightUnit());
            indicatorsConditionMonitoringVO.setIndicatorsConditionMonitorings(doGetIndicatorsCondition(tenantCode,plan.getId(),form.getId(),formField.getId()));
            results.add(indicatorsConditionMonitoringVO);
        }
        return results;
    }

    /**
     * 获取指定监控字段的监控条件数据
     * @param tenantCode
     * @param planId
     * @param formId
     * @param fieldId
     * @return
     */
    private List<IndicatorsConditionMonitoring> doGetIndicatorsCondition(String tenantCode,Long planId,Long formId,String fieldId){
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<IndicatorsConditionMonitoring> lbqWrapper = new LbqWrapper();
        lbqWrapper.eq(IndicatorsConditionMonitoring::getPlanId,planId);
        lbqWrapper.eq(IndicatorsConditionMonitoring::getFormId,formId);
        lbqWrapper.eq(IndicatorsConditionMonitoring::getFieldId,fieldId);
        lbqWrapper.orderByAsc(IndicatorsConditionMonitoring::getCreateTime);
        return this.list(lbqWrapper);
    }

    /**
     * 监测数据-监测计划-监测指标 -立即生效
     * @param tenantCode
     * @return
     */
    @Override
    public Boolean synIndicatorsConditionMonitoringTask(String tenantCode) {
        //标记任务进行中
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch() + ":" + tenantCode, MonitoringTaskType.RUNING.operateType);
        // 执行同步任务信息完整度的逻辑
        SaasGlobalThreadPool.execute(() -> doSynInformationIntegrityMonitoringTask(tenantCode));
        return Boolean.TRUE;
    }

    /**
     * 监测数据-监测计划-监测指标 -监控表单 表单结果变化触发
     * @param model       提交的表单
     * @param tenantCode  租户ID
     * @return
     */
    @Override
    public Boolean synIndicatorsConditionMonitoringTaskByFormChange(FormResult model, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        //获取患者管理-监测数据-监控指标条件信息
        LbqWrapper<IndicatorsConditionMonitoring> lbqWrapper = new LbqWrapper();
        lbqWrapper.eq(IndicatorsConditionMonitoring::getFormId,model.getFormId());
        lbqWrapper.orderByAsc(IndicatorsConditionMonitoring::getCreateTime);
        List<IndicatorsConditionMonitoring> indicatorsConditionMonitoringgs = list(lbqWrapper);
        if (CollectionUtils.isEmpty(indicatorsConditionMonitoringgs) || ObjectUtils.isEmpty(model)) {
            return Boolean.TRUE;
        }
        List<FormResult> models = new ArrayList<>();
        models.add(model);
        //患者单个表单提交或者更新时，先删除原来的异常数据
        LbqWrapper<IndicatorsResultInformation> lbqIndicatorsResultInformationWrapper = new LbqWrapper();
        lbqIndicatorsResultInformationWrapper.clear();
        lbqIndicatorsResultInformationWrapper.eq(IndicatorsResultInformation::getHandleStatus,HandleStatusEnum.UN_HANDLE.getCode());
        lbqIndicatorsResultInformationWrapper.eq(IndicatorsResultInformation::getFormResultId,model.getId());
        indicatorsResultInformationService.remove(lbqIndicatorsResultInformationWrapper);
        generateIndicatorsResultInformation(tenantCode,model.getFormId(),indicatorsConditionMonitoringgs,models);
        return Boolean.TRUE;
    }

    /** 进行分批整理后逐条条件匹配与触发异常监控
     * 租户编码
     * @param tenantCode
     */
    private void doSynInformationIntegrityMonitoringTask(String tenantCode){
        BaseContextHandler.setTenant(tenantCode);
        try {
            //立即生效前、删除所有未处理的异常。
            LbqWrapper<IndicatorsResultInformation> lbqIndicatorsResultInformationWrapper = new LbqWrapper();
            lbqIndicatorsResultInformationWrapper.eq(IndicatorsResultInformation::getHandleStatus,HandleStatusEnum.UN_HANDLE.getCode());
            indicatorsResultInformationService.remove(lbqIndicatorsResultInformationWrapper);
            //获取患者管理-监测数据-监控指标条件信息
            LbqWrapper<IndicatorsConditionMonitoring> lbqWrapper = new LbqWrapper();
            lbqWrapper.orderByAsc(IndicatorsConditionMonitoring::getCreateTime);
            List<IndicatorsConditionMonitoring> indicatorsConditionMonitoringgs = list(lbqWrapper);
            if (CollectionUtils.isEmpty(indicatorsConditionMonitoringgs)) {
                return;
            }
            //获取监控的表单
            Map<Long, List<IndicatorsConditionMonitoring>> monitoringFormConditionMap = indicatorsConditionMonitoringgs.stream().collect(Collectors.groupingBy(IndicatorsConditionMonitoring::getFormId));
            //表单结果与监控条件匹配
            for (Long monitoringFormId:monitoringFormConditionMap.keySet()){
                PageParams<FormResultPageDTO> pagesQ = new PageParams<>();
                pagesQ.setCurrent(1L);
                pagesQ.setSize(100L);
                pagesQ.setModel(FormResultPageDTO.builder().build());
                LbqWrapper<FormResult> lbqFormResultWrapper = new LbqWrapper();
                lbqFormResultWrapper.eq(FormResult::getFormId,monitoringFormId);
                IPage<FormResult> pagesR = formResultService.page(pagesQ.buildPage(),lbqFormResultWrapper);
                if (!ObjectUtils.isEmpty(pagesR) && !CollectionUtils.isEmpty(pagesR.getRecords())) {
                    //总页数
                    long pages = pagesR.getPages();
                    //第一页的任务执行
                    generateIndicatorsResultInformation(tenantCode,monitoringFormId, monitoringFormConditionMap.get(monitoringFormId), pagesR.getRecords());
                    //第一页之后的任务执行
                    for (int i = 1; i < pages; i++) {
                        pagesQ.setCurrent(i + 1);
                        pagesR = formResultService.page(pagesQ.buildPage(),lbqFormResultWrapper);
                        if (!ObjectUtils.isEmpty(pagesR) && !CollectionUtils.isEmpty(pagesR.getRecords())) {
                            // TODO 若效率不行，可在此进行多线程处理，翻页执行的时候单独走个线程去执行，每个线程执行指定页的数据。
                            generateIndicatorsResultInformation(tenantCode,monitoringFormId, monitoringFormConditionMap.get(monitoringFormId), pagesR.getRecords());
                        } else {
                            log.error("执行同步任务患者管理-监测数据-监控指标的逻辑 未查询到表单ID【"+monitoringFormId+"】表单结果 第【" + pagesQ.getCurrent() + "】页 信息！");
                        }
                    }
                } else {
                    log.error("执行同步任务患者管理-监测数据-监控指标的逻辑  未查询到表单ID【"+monitoringFormId+"】表单结果 第【" + pagesQ.getCurrent() + "】页 信息！");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("执行同步任务患者管理-监测数据-监控指标的逻辑 失败！", e);
        }finally {
            //执行完成任务标记
            redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch() + ":" + tenantCode, MonitoringTaskType.FINISH.operateType);
        }

    }

    /**
     * 检验指定的自定义表单监控结果是否异常
     * @param tenantCode 租户ID
     * @param monitoringFormId 监控表单ID
     * @param indicatorsConditionMonitorings 监控表单条件
     * @param formResults 需要检验的提交的表单结果
     *
     */
    private void generateIndicatorsResultInformation(String tenantCode,Long monitoringFormId,List<IndicatorsConditionMonitoring> indicatorsConditionMonitorings,List<FormResult> formResults){
        List<IndicatorsResultInformation> indicatorsResultInformations = new ArrayList<>();
        //监控字段条件
        Map<String, List<IndicatorsConditionMonitoring>> indicatorsConditionMonitoringsMap = indicatorsConditionMonitorings.stream().collect(Collectors.groupingBy(IndicatorsConditionMonitoring::getFieldId));
        for (FormResult formResult:formResults){
            //当前表单结果监控字段 数据集
            List<FormField> formResultFields = JSON.parseArray(formResult.getJsonContent(), FormField.class).stream().filter(field -> FormFieldExactType.MONITORING_INDICATORS.equals(field.getExactType())).collect(Collectors.toList());;
            //监控字段数据集转map
            if (CollectionUtils.isEmpty(formResultFields)){
                continue;
            }
            Map<String,FormField> formResultFieldsMap = formResultFields.stream().collect(Collectors.toMap(FormField::getId,FormField->FormField));
            //目前指挥配置监控字段 只有一个
            for (String fieldId:formResultFieldsMap.keySet()){
                //没有配置条件 跳过
                if (!indicatorsConditionMonitoringsMap.containsKey(fieldId)){
                    continue;
                }
                List<IndicatorsConditionMonitoring> conditions= indicatorsConditionMonitoringsMap.get(fieldId);
                if (CollectionUtils.isEmpty(conditions)){
                    continue;
                }
                //进行条件判断，获取结果匹配结果
                isAccordCondition(conditions,formResult,formResultFieldsMap.get(fieldId),indicatorsResultInformations);
            }
        }
        //批量新增异常结果
        if (!CollectionUtils.isEmpty(indicatorsResultInformations)){
            List<Long> patientIds= indicatorsResultInformations.stream().map(IndicatorsResultInformation::getPatientId).distinct().collect(Collectors.toList());
            //获取患者已经医生名称
            R<List<Patient>> patientsR = patientApi.listByIds(patientIds);
            if (patientsR.getIsSuccess() && !CollectionUtils.isEmpty(patientsR.getData())){
                Map<Long,Patient> patientMap = patientsR.getData().stream().collect(Collectors.toMap(Patient::getId,Patient->Patient));
                for (IndicatorsResultInformation indicatorsResultInformation:indicatorsResultInformations){
                    if (patientMap.get(indicatorsResultInformation.getPatientId())==null){
                        log.error("患者信息异常！无法获取 患者【"+indicatorsResultInformation.getPatientId()+"】的信息！");
                        continue;
                    }
                    indicatorsResultInformation.setPatientName(patientMap.get(indicatorsResultInformation.getPatientId()).getName());
                    indicatorsResultInformation.setAvatar(patientMap.get(indicatorsResultInformation.getPatientId()).getAvatar());
                    indicatorsResultInformation.setDoctorId(patientMap.get(indicatorsResultInformation.getPatientId()).getDoctorId());
                    indicatorsResultInformation.setDoctorName(patientMap.get(indicatorsResultInformation.getPatientId()).getDoctorName());
                    indicatorsResultInformation.setNursingId(patientMap.get(indicatorsResultInformation.getPatientId()).getServiceAdvisorId());
                }
                //过滤掉患者不存在的填写异常数据。
                indicatorsResultInformations = indicatorsResultInformations.stream().filter(i-> patientMap.get(i.getPatientId())!=null).collect(Collectors.toList());

            }
            //生成当前条件下，表单结果监测异常数据。
            indicatorsResultInformationService.saveBatch(indicatorsResultInformations);
        }
    }

    /**
     * 判断表单字段监控值是否符合监控条件 ，获取结果匹配结果
     * @param conditions 监控条件
     * @param formResult  表单填写的表单结果
     * @param formField  表单填写的表单字段结果
     * @param indicatorsResultInformations 异常结果集
     */
    private void isAccordCondition(List<IndicatorsConditionMonitoring> conditions,FormResult formResult,FormField formField,List<IndicatorsResultInformation> indicatorsResultInformations){
        //监控条件为空，不进行监控匹配判断
        if (CollectionUtils.isEmpty(conditions)){
            log.error("监控条件为空，不进行监控匹配判断 formField = "+ JSON.toJSONString(formField));
            return ;
        }
        //监控表单字段值未填写，不进行监控匹配判断
        if ("null".equals(BeanUtil.toBean(formField, FormFieldDto.class).parseValue())){
            log.error("监控表单字段值未填写，不进行监控匹配判断 formField = "+ JSON.toJSONString(formField));
            return ;
        }
        for (IndicatorsConditionMonitoring indicatorsConditionMonitoring:conditions){
            //获取填写值
            BigDecimal bigDecimalWriteFormvalue = new BigDecimal(BeanUtil.toBean(formField, FormFieldDto.class).parseValue());
            //最大条件是否满足
            Boolean maxConditionFalg = false;
            //最小条件是否满足
            Boolean minConditionFalg = false;
            //最大值条件判断
            if (StringUtils.isNotEmptyString(indicatorsConditionMonitoring.getMaxConditionSymbol()) && StringUtils.isNotEmptyString(indicatorsConditionMonitoring.getMaxConditionValue()) ){
                BigDecimal bigDecimalMaxConditionValue = new BigDecimal(indicatorsConditionMonitoring.getMaxConditionValue());
                int result = bigDecimalWriteFormvalue.compareTo(bigDecimalMaxConditionValue);
//                    result = -1,表示bigDecimalWriteFormvalue小于bigDecimalMaxConditionValue；
//                    result = 0,表示bigDecimalWriteFormvalue等于bigDecimalMaxConditionValue；
//                    result = 1,表示bigDecimalWriteFormvalue大于bigDecimalMaxConditionValue；
                if (indicatorsConditionMonitoring.getMaxConditionSymbol().equals(SymbolTypeEnum.LT.operateType) && result==-1){
                    maxConditionFalg = true;
                }else if (indicatorsConditionMonitoring.getMaxConditionSymbol().equals(SymbolTypeEnum.LE.operateType) && (result==-1 || result==0)){
                    maxConditionFalg = true;
                }else if (indicatorsConditionMonitoring.getMaxConditionSymbol().equals(SymbolTypeEnum.EQ.operateType) && result==0){
                    maxConditionFalg = true;
                }
            }else {
                maxConditionFalg = true;
            }
            //最小值条件判断
            if (StringUtils.isNotEmptyString(indicatorsConditionMonitoring.getMinConditionSymbol()) && StringUtils.isNotEmptyString(indicatorsConditionMonitoring.getMinConditionValue()) ){
                BigDecimal bigDecimalMinConditionValue = new BigDecimal(indicatorsConditionMonitoring.getMinConditionValue());
                int result = bigDecimalMinConditionValue.compareTo(bigDecimalWriteFormvalue);
                if (indicatorsConditionMonitoring.getMinConditionSymbol().equals(SymbolTypeEnum.LT.operateType) && result==-1){
                    minConditionFalg = true;
                }else if (indicatorsConditionMonitoring.getMinConditionSymbol().equals(SymbolTypeEnum.LE.operateType) && (result==-1 || result==0)){
                    minConditionFalg = true;
                }else if (indicatorsConditionMonitoring.getMinConditionSymbol().equals(SymbolTypeEnum.EQ.operateType) && result==0){
                    minConditionFalg = true;
                }
            }else {
                    minConditionFalg = true;
            }
            //是否存在发生异常，并处理过   存在|不存在  true|false
            Boolean exitException = false;
            LbqWrapper<IndicatorsResultInformation> lbqWrapper = new LbqWrapper();
            lbqWrapper.eq(IndicatorsResultInformation::getHandleStatus,HandleStatusEnum.AL_HANDLE.getCode());
            lbqWrapper.eq(IndicatorsResultInformation::getFormResultId,formResult.getId());
            int exitExceptionCount = indicatorsResultInformationService.count(lbqWrapper);
            if (exitExceptionCount>0){
                exitException = true;
            }

            if (maxConditionFalg && minConditionFalg && !exitException){
                IndicatorsResultInformation indicatorsResultInformation = new IndicatorsResultInformation();
                indicatorsResultInformation.setPatientId(formResult.getUserId());
//                indicatorsResultInformation.setPatientName("");
//                indicatorsResultInformation.setDoctorId(null);
//                indicatorsResultInformation.setDoctorName("");
                indicatorsResultInformation.setHandleStatus(HandleStatusEnum.UN_HANDLE.getCode());
                indicatorsResultInformation.setClearStatus(ClearStatusEnum.UN_CLEAR.getCode());
                indicatorsResultInformation.setIndicatorsConditionId(indicatorsConditionMonitoring.getId());
                indicatorsResultInformation.setPlanId(indicatorsConditionMonitoring.getPlanId());
                indicatorsResultInformation.setFormId(indicatorsConditionMonitoring.getFormId());
                indicatorsResultInformation.setFormResultId(formResult.getId());
                indicatorsResultInformation.setFieldId(indicatorsConditionMonitoring.getFieldId());
                indicatorsResultInformation.setFieldLabel(indicatorsConditionMonitoring.getFieldLabel());
                indicatorsResultInformation.setRealWriteValue(bigDecimalWriteFormvalue.toString());
                indicatorsResultInformation.setRealWriteValueRightUnit(formField.getRightUnit());
                indicatorsResultInformation.setFormWriteTime(formResult.getCreateTime());
                indicatorsResultInformation.setMaxConditionSymbol(indicatorsConditionMonitoring.getMaxConditionSymbol());
                indicatorsResultInformation.setMaxConditionValue(indicatorsConditionMonitoring.getMaxConditionValue());
                indicatorsResultInformation.setMinConditionSymbol(indicatorsConditionMonitoring.getMinConditionSymbol());
                indicatorsResultInformation.setMinConditionValue(indicatorsConditionMonitoring.getMinConditionValue());
                indicatorsResultInformations.add(indicatorsResultInformation);
                //满足一次条件，则其他条件不需要了
                break;
            }
        }
    }
}
