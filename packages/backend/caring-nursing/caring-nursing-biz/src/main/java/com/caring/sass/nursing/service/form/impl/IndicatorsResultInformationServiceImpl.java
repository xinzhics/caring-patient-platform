package com.caring.sass.nursing.service.form.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.form.IndicatorsConditionMonitoringMapper;
import com.caring.sass.nursing.dao.form.IndicatorsResultInformationMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.form.EliminateDto;
import com.caring.sass.nursing.dto.form.MonitorUnprocessedDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.IndicatorsConditionMonitoring;
import com.caring.sass.nursing.entity.form.IndicatorsResultInformation;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.ClearStatusEnum;
import com.caring.sass.nursing.enumeration.HandleStatusEnum;
import com.caring.sass.nursing.service.form.IndicatorsResultInformationService;
import com.caring.sass.nursing.vo.*;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.utils.BizAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者监测数据结果及处理表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-14
 */
@Slf4j
@Service
public class IndicatorsResultInformationServiceImpl extends SuperServiceImpl<IndicatorsResultInformationMapper, IndicatorsResultInformation> implements IndicatorsResultInformationService {

    @Autowired
    private IndicatorsResultInformationMapper informationMapper;
    @Autowired
    private FormResultMapper formResultMapper;
    @Autowired
    private IndicatorsConditionMonitoringMapper monitoringMapper;
    @Autowired
    PatientApi patientApi;

    @Autowired
    PlanMapper planMapper;
    /**
     * 监测数据的异常患者数
     * @param tenantCode
     * @return
     */
    @Override
    public Integer monitorUnusualPatients(String tenantCode) {
        List<AbnormalDataVo> results = abnormalData(tenantCode);
        if (CollectionUtils.isEmpty(results)){
            return 0;
        }else {
            return results.stream().mapToInt(AbnormalDataVo::getPatientNumber).sum();
        }
    }
    /**
     * 各监测计划的异常数据
     *
     * @param tenantCode
     * @return
     */
    @Override
    public List<AbnormalDataVo> abnormalData(String tenantCode) {
        List<AbnormalDataVo> results=new ArrayList<>();
        //获取监控计划
        LambdaQueryWrapper<IndicatorsConditionMonitoring> lqw = new LambdaQueryWrapper<>();
        lqw.eq(IndicatorsConditionMonitoring::getTenantCode, tenantCode);
        lqw.isNotNull(IndicatorsConditionMonitoring::getMaxConditionValue)
                .or().isNotNull(IndicatorsConditionMonitoring::getMinConditionValue);
        lqw.groupBy(IndicatorsConditionMonitoring::getPlanId);
        List<IndicatorsConditionMonitoring> monitoringPlans = monitoringMapper.selectList(lqw);
        if(CollectionUtils.isEmpty(monitoringPlans)){
            return results;
        }
        //获取每个计划异常数量
        Set<Long> planIds = monitoringPlans.stream().map(IndicatorsConditionMonitoring::getPlanId).collect(Collectors.toSet());
        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ().in(SuperEntity::getId, planIds).select(SuperEntity::getId, Plan::getName));
        Map<Long, String> planNames = new HashMap<>();
        if (CollUtil.isNotEmpty(planList)) {
            planNames = planList.stream().collect(Collectors.toMap(SuperEntity::getId, Plan::getName, (o1, o2) -> o2));
        }
        for (IndicatorsConditionMonitoring monitoringPlan : monitoringPlans) {
            AbnormalDataVo result = new AbnormalDataVo();
            result.setPlanName(planNames.get(monitoringPlan.getPlanId()) == null ? monitoringPlan.getPlanName() : planNames.get(monitoringPlan.getPlanId()));
            result.setPlanId(monitoringPlan.getPlanId());
            result.setPatientNumber(informationMapper.getPatientCountByPlanIdNursingId(monitoringPlan.getPlanId(),BaseContextHandler.getUserId()));
            results.add(result);
        }
        return results;
    }



    /**
     * 异常数据未处理列表
     *
     * @param params
     * @return
     */
    @Override
    public MonitorUnprocessedAndTotal dataUnprocessedList(PageParams<MonitorUnprocessedDTO> params) {
        BizAssert.notNull(params.getModel().getPlanId(), "计划ID必填！");
        MonitorUnprocessedAndTotal result = new MonitorUnprocessedAndTotal();
        List<MonitorUnprocessedVo> vos = new ArrayList<>();
        Page page = new Page();
        page.setCurrent(params.getCurrent());
        page.setSize(params.getSize());
        MonitorUnprocessedDTO monitorUnprocessedDTO = params.getModel();
        monitorUnprocessedDTO.setNursingId(BaseContextHandler.getUserId());
        IPage<MonitorProcessedVo> pageMonitorProcessedVo = this.getBaseMapper().selectdataProcessedPageList(page, HandleStatusEnum.UN_HANDLE.getCode(), monitorUnprocessedDTO);
        List<MonitorProcessedVo> records = pageMonitorProcessedVo.getRecords();
        Map<Long, String> remarkData = new HashMap<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> patientIds = records.stream().map(MonitorProcessedVo::getPatientId).collect(Collectors.toList());
            R<Map<Long, String>> nursingPatientRemark = patientApi.findNursingPatientRemark(patientIds);
            remarkData = nursingPatientRemark.getData();
        }
        for (MonitorProcessedVo model : records) {
            MonitorUnprocessedVo vo = new MonitorUnprocessedVo();
            BeanUtils.copyProperties(model, vo);
            String s = remarkData.get(vo.getPatientId());
            if (StrUtil.isNotEmpty(s)) {
                vo.setPatientName(vo.getPatientName() + " (" +s+ ")");
            }
            List<ValueAndTimeVo> list = this.getBaseMapper().selectdataUnprocessedPageList(model.getPatientId(),HandleStatusEnum.UN_HANDLE.getCode(),monitorUnprocessedDTO);
            vo.setValueAndTimeVo(list);
            vos.add(vo);
        }
        result.setMonitorProcessedVo(vos);
        result.setTotal(pageMonitorProcessedVo.getTotal());
        result.setPlanId(params.getModel().getPlanId());
        return result;
    }

    /**
     * 异常数据已处理列表
     *
     * @param params
     * @return
     */
    @Override
    public MonitorProcessedAndTotal dataProcessedList(PageParams<MonitorUnprocessedDTO> params) {
        BizAssert.notNull(params.getModel().getPlanId(), "计划ID必填！");
        MonitorProcessedAndTotal result = new MonitorProcessedAndTotal();
        Page page = new Page();
        page.setCurrent(params.getCurrent());
        page.setSize(params.getSize());
        MonitorUnprocessedDTO monitorUnprocessedDTO = params.getModel();
        monitorUnprocessedDTO.setNursingId(BaseContextHandler.getUserId());
        IPage<MonitorProcessedVo> pageMonitorProcessedVo= this.getBaseMapper().selectdataProcessedPageList(page,HandleStatusEnum.AL_HANDLE.getCode(),monitorUnprocessedDTO);
        List<MonitorProcessedVo> records = pageMonitorProcessedVo.getRecords();
        Map<Long, String> remarkData = new HashMap<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> patientIds = records.stream().map(MonitorProcessedVo::getPatientId).collect(Collectors.toList());
            R<Map<Long, String>> nursingPatientRemark = patientApi.findNursingPatientRemark(patientIds);
            remarkData = nursingPatientRemark.getData();
        }
        for (MonitorProcessedVo model : records) {
            String s = remarkData.get(model.getPatientId());
            if (StrUtil.isNotEmpty(s)) {
                model.setPatientName(model.getPatientName() + " (" +s+ ")");
            }
        }
        result.setMonitorProcessedVo(records);
        result.setTotal(pageMonitorProcessedVo.getTotal());
        result.setPlanId(params.getModel().getPlanId());
        return result;
    }
    /**
     * 异常数据单个处理或全部处理
     *
     * @param dto
     * @return
     */
    @Override
    public R UnprocessedEliminate(EliminateDto dto) {
        LambdaUpdateWrapper<IndicatorsResultInformation> updateWrapper = new LambdaUpdateWrapper<>();
        //患者ID不为空则处理指定患者、 若为空则处理全部计划患者
        if (dto.getPatientId()!= null) {
            updateWrapper.eq(IndicatorsResultInformation::getPatientId, dto.getPatientId());
        }
        //设置操作人、操作时间、操作状态
        updateWrapper.set(IndicatorsResultInformation::getHandleUser, BaseContextHandler.getUserId());
        updateWrapper.set(IndicatorsResultInformation::getHandleStatus, HandleStatusEnum.AL_HANDLE.getCode());
        updateWrapper.set(IndicatorsResultInformation::getHandleTime,LocalDateTime.now());
        //更新指定医助、计划、状态
        updateWrapper.eq(IndicatorsResultInformation::getNursingId, BaseContextHandler.getUserId());
        updateWrapper.eq(IndicatorsResultInformation::getPlanId, dto.getPlanId());
        updateWrapper.eq(IndicatorsResultInformation::getHandleStatus,HandleStatusEnum.UN_HANDLE.getCode());
        return R.success(this.update(updateWrapper));
    }

    /**
     * 已处理异常数据全部清除
     *
     * @return
     */
    @Override
    public R ProcessedEliminate(Long planId) {
        Long nursingId = BaseContextHandler.getUserId();
        LambdaUpdateWrapper<IndicatorsResultInformation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(IndicatorsResultInformation::getNursingId, nursingId);
        updateWrapper.eq(IndicatorsResultInformation::getPlanId, planId);
        updateWrapper.eq(IndicatorsResultInformation::getHandleStatus, HandleStatusEnum.AL_HANDLE.getCode());
        updateWrapper.set(IndicatorsResultInformation::getClearStatus, ClearStatusEnum.AL_CLEAR.getCode());
        baseMapper.update(null, updateWrapper);
        return R.success();
    }

    /**
     * 统计异常数量
     *
     * @param date
     * @param planId
     * @return
     */
    @Override
    public SubmittedQuantity statisticsAbnormalQuantity(String date, Long planId) {
        SubmittedQuantity result = new SubmittedQuantity();
        //累计提交总数
        result.setTotalSubmissions(doGetTotalSubmissions(date,planId));
        //已处理异常数量
        result.setNumberExceptionsQuantity(doGetNumberExceptionsQuantity(date,planId));
        //未处理异常数量
        result.setUntreatedExceptionsQuantity(doGetUntreatedExceptionsQuantity(date,planId));
        //累计提交异常数量
        result.setSubmitExceptionQuantity(result.getNumberExceptionsQuantity()+result.getUntreatedExceptionsQuantity());
        //提交数据正常数量
        result.setNormalQuantity(result.getTotalSubmissions()-result.getSubmitExceptionQuantity());
        //已处理异常数量百分比
        result.setNumberExceptionsQuantityPercentage(getPercentage(result.getNumberExceptionsQuantity(),result.getTotalSubmissions()));
        //未处理异常数量百分比
        result.setUntreatedExceptionsQuantityPercentage(getPercentage(result.getUntreatedExceptionsQuantity(),result.getTotalSubmissions()));
        //提交数据正常数量百分比
        result.setNormalQuantityPercentage(getPercentage(result.getNormalQuantity(),result.getTotalSubmissions()));
        return result;
    }
    /**
     * 监测计划说明
     *
     * @param planId
     * @return
     */
    @Override
    public List<ExplainVo> explain(Long planId) {
        LambdaQueryWrapper<IndicatorsConditionMonitoring> lqw = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<IndicatorsResultInformation> we = new LambdaQueryWrapper<>();
        lqw.eq(IndicatorsConditionMonitoring::getPlanId, planId);
        we.eq(IndicatorsResultInformation::getPlanId, planId);
        we.groupBy(IndicatorsResultInformation::getPlanId);
        List<IndicatorsResultInformation> list = informationMapper.selectList(we);
        List<ExplainVo> explainVoList =new ArrayList<>() ;
        List<IndicatorsConditionMonitoring> onelist = monitoringMapper.selectList(lqw);
        for (IndicatorsConditionMonitoring one : onelist) {
            ExplainVo vo = new ExplainVo();
            vo.setFieldLabel(one.getFieldLabel());
            vo.setMaxConditionSymbol(one.getMaxConditionSymbol());
            vo.setMaxConditionValue(one.getMaxConditionValue());
            vo.setMinConditionSymbol(one.getMinConditionSymbol());
            vo.setMinConditionValue(one.getMinConditionValue());
            for (IndicatorsResultInformation information : list) {
                vo.setRealWriteValueRightUnit(information.getRealWriteValueRightUnit());
            }
            explainVoList.add(vo);
        }
        return explainVoList;
    }
    /**
     * 获取累计提交总数
     * @param date
     * @param planId
     * @return
     */
    private int doGetTotalSubmissions(String date, Long planId){
        LambdaQueryWrapper<FormResult> qw = new LambdaQueryWrapper<>();
        //获取现在时间月份
        if ("本月".equals(date)){
            int nowMonth =  LocalDateTime.now().getMonthValue();
            qw.between(FormResult::getCreateTime,getFirstDayOfMonth(nowMonth),getLastDayOfMonth(nowMonth));
        }else {
            String[] split = date.split("\\.");
            int moth = Integer.parseInt(split[1]);
            qw.between(FormResult::getCreateTime,getFirstDayOfMonth(moth),getLastDayOfMonth(moth));
        }
        qw.eq(FormResult::getBusinessId, planId);
        qw.apply("user_id in (SELECT id from u_user_patient where service_advisor_id ="+BaseContextHandler.getUserId()+ " )");
        return formResultMapper.selectCount(qw);
    }
    /**
     * 获取已处理异常数量
     * @param date
     * @param planId
     * @return
     */
    private int doGetNumberExceptionsQuantity(String date, Long planId){
        int moth = 0;
        if ("本月".equals(date)){
            moth =  LocalDateTime.now().getMonthValue();
        }else {
            String[] split = date.split("\\.");
            moth = Integer.parseInt(split[1]);
        }
        LambdaQueryWrapper<IndicatorsResultInformation> lqw = new LambdaQueryWrapper<>();
        lqw.eq(IndicatorsResultInformation::getPlanId, planId);
        lqw.eq(IndicatorsResultInformation::getNursingId, BaseContextHandler.getUserId());
        lqw.between(IndicatorsResultInformation::getFormWriteTime,getFirstDayOfMonth(moth),getLastDayOfMonth(moth));
        lqw.eq(IndicatorsResultInformation::getHandleStatus, HandleStatusEnum.AL_HANDLE.getCode());
        return informationMapper.selectCount(lqw);
    }
    /**
     * 获取未处理异常数量
     * @param date
     * @param planId
     * @return
     */
    private int doGetUntreatedExceptionsQuantity(String date, Long planId){
        int moth = 0;
        if ("本月".equals(date)){
            moth =  LocalDateTime.now().getMonthValue();
        }else {
            String[] split = date.split("\\.");
            moth = Integer.parseInt(split[1]);
        }
        LambdaQueryWrapper<IndicatorsResultInformation> lqw = new LambdaQueryWrapper<>();
        lqw.eq(IndicatorsResultInformation::getPlanId, planId);
        lqw.eq(IndicatorsResultInformation::getNursingId, BaseContextHandler.getUserId());
        lqw.between(IndicatorsResultInformation::getFormWriteTime,getFirstDayOfMonth(moth),getLastDayOfMonth(moth));
        lqw.eq(IndicatorsResultInformation::getHandleStatus, HandleStatusEnum.UN_HANDLE.getCode());
        return informationMapper.selectCount(lqw);
    }
    /**
     * 获取当前月第一天
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime())+" 00:00:00";
    }

    /**
     * 获取当前月最后一天:
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay=0;
        //2月的平年瑞年天数
        if(month==2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        }else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(calendar.getTime())+" 23:59:59";
    }

    /**
     * 获取百分比
     * @param part  分子
     * @param total 总数
     * @return
     */
    private String getPercentage(int part, int total){
        if (total == 0) {
            return "0%";
        }
        BigDecimal totalDecimal = new BigDecimal(total);
        BigDecimal chartDecimal = new BigDecimal(part);
        return String.valueOf(BigDecimalUtils.proportion(chartDecimal, totalDecimal))+"%";
    }
}
