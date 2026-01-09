package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.update.LbuWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.dao.drugs.DrugsConditionMonitoringMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeSettingMapper;
import com.caring.sass.nursing.dto.drugs.PatientDrugsPageDTO;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.entity.drugs.*;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.*;
import com.caring.sass.nursing.service.drugs.*;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.nursing.util.PatientDrugsEndDateUtil;
import com.caring.sass.nursing.vo.DrugsConditionMonitoringVO;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者管理-用药预警-预警条件表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Slf4j
@Service
public class DrugsConditionMonitoringServiceImpl extends SuperServiceImpl<DrugsConditionMonitoringMapper, DrugsConditionMonitoring> implements DrugsConditionMonitoringService {

    @Autowired
    DrugsResultInformationService drugsResultInformationService;


    FormResultService formResultService;

    @Autowired
    PatientDrugsService patientDrugsService;
    @Autowired
    SysDrugsService sysDrugsService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    PatientApi patientApi;
    @Autowired
    WeiXinApi weiXinApi;
    @Autowired
    TemplateMsgApi templateApi;
    @Autowired
    DatabaseProperties databaseProperties;
    @Autowired
    DictionaryItemService dictionaryItemService;
    @Autowired
    BusinessReminderLogControllerApi businessReminderLogControllerApi;

    public FormResultService getFormResultService() {
        if (formResultService == null) {
            formResultService = SpringUtils.getBean(FormResultService.class);
        }
        return formResultService;
    }

    /**
     * 查询患者管理-用药预警-预警药品及条件
     * @param tenantCode
     * @param drugsId
     * @return
     */
    @Override
    public List<DrugsConditionMonitoringVO> getDrugsConditionMonitoring(String tenantCode, Long drugsId) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<DrugsConditionMonitoring> lbqWrapper = new LbqWrapper();
        if (drugsId!=null){
            lbqWrapper.eq(DrugsConditionMonitoring::getDrugsId,drugsId);
        }
        lbqWrapper.orderByDesc(DrugsConditionMonitoring::getCreateTime);
        List<DrugsConditionMonitoring> drugsConditionMonitorings = this.list(lbqWrapper);
        if (CollectionUtils.isEmpty(drugsConditionMonitorings)){
            return new ArrayList<>();
        }
        List<DrugsConditionMonitoringVO> results =  drugsConditionMonitorings.stream().map(i ->{
                    DrugsConditionMonitoringVO drugsConditionMonitoringVO= BeanUtil.toBean(i, DrugsConditionMonitoringVO.class);
                    return drugsConditionMonitoringVO;
                }
        ).collect(Collectors.toList());
        return results;
    }
    /**
     * 患者管理-用药预警-预警药品及条件-立即生效
     * @param tenantCode
     * @param flag true|false 立即执行|定时执行
     * @return
     */
    @Override
    public Boolean synDrugsConditionMonitoringTask(String tenantCode,Boolean flag) {
        //标记任务进行中
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch() + ":" + tenantCode, MonitoringTaskType.RUNING.operateType);
        // 执行同步任务用药预警的逻辑
        SaasGlobalThreadPool.execute(() -> doSynDrugsConditionMonitoringTask(tenantCode,flag));
        return Boolean.TRUE;


    }

    /**
     * 执行同步任务用药预警的逻辑
     * @param tenantCode
     * @param flag true|false 立即执行|定时执行
     */
    private void doSynDrugsConditionMonitoringTask(String tenantCode,Boolean flag){
        BaseContextHandler.setTenant(tenantCode);
        //立即执行，删除所有未处理的预警，更新对应的患者药箱药品未可监控，然后重新生成
        if(flag){
            List<DrugsResultInformation> drugsResultInformations= drugsResultInformationService.list();
            if(!CollectionUtils.isEmpty(drugsResultInformations)){
                drugsResultInformationService.removeByIds(drugsResultInformations.stream().map(DrugsResultInformation::getId).collect(Collectors.toList()));
                for(DrugsResultInformation drugsResultInformation:drugsResultInformations){
                    LbuWrapper<PatientDrugs> lbuWrapper = new LbuWrapper<>();
                    lbuWrapper.set(PatientDrugs::getEarlyWarningMonitor,EarlyWarningMonitorEnum.CAN_MONITOR.code);
                    lbuWrapper.eq(PatientDrugs::getPatientId,drugsResultInformation.getPatientId());
                    lbuWrapper.eq(PatientDrugs::getDrugsId,drugsResultInformation.getDrugsId());
                    lbuWrapper.eq(PatientDrugs::getStatus, 0);
                    patientDrugsService.update(lbuWrapper);
                }
            }
        }
        try {
            //获取患者管理-用药预警-预警条件信息
            LbqWrapper<DrugsConditionMonitoring> lbqWrapper = new LbqWrapper();
            lbqWrapper.orderByAsc(DrugsConditionMonitoring::getCreateTime);
            List<DrugsConditionMonitoring> drugsConditionMonitorings = this.list(lbqWrapper);
            if (CollectionUtils.isEmpty(drugsConditionMonitorings)) {
                return ;
            }
            for (DrugsConditionMonitoring drugsConditionMonitoring:drugsConditionMonitorings){
                PageParams<PatientDrugsPageDTO> pagesQ = new PageParams<>();
                pagesQ.setCurrent(1L);
                pagesQ.setSize(100L);
                pagesQ.setModel(PatientDrugsPageDTO.builder().build());
                LbqWrapper<PatientDrugs> lbqFormResultWrapper = new LbqWrapper();
                lbqFormResultWrapper.eq(PatientDrugs::getEarlyWarningMonitor, EarlyWarningMonitorEnum.CAN_MONITOR.code);
                lbqFormResultWrapper.eq(PatientDrugs::getStatus, 0);
                lbqFormResultWrapper.eq(PatientDrugs::getDrugsId, drugsConditionMonitoring.getDrugsId());
                IPage<PatientDrugs> pagesR = patientDrugsService.page(pagesQ.buildPage(),lbqFormResultWrapper);

                if (!ObjectUtils.isEmpty(pagesR) && !CollectionUtils.isEmpty(pagesR.getRecords())) {
                    //总页数
                    long pages = pagesR.getPages();
                    //第一页的任务执行
                    generateDrugsResultInformation(tenantCode,drugsConditionMonitoring, pagesR.getRecords());
                    //第一页之后的任务执行
                    for (int i = 1; i < pages; i++) {
                        pagesQ.setCurrent(i + 1);
                        pagesR = patientDrugsService.page(pagesQ.buildPage(),lbqFormResultWrapper);
                        if (!ObjectUtils.isEmpty(pagesR) && !CollectionUtils.isEmpty(pagesR.getRecords())) {
                            // TODO 若效率不行，可在此进行多线程处理，翻页执行的时候单独走个线程去执行，每个线程执行指定页的数据。
                            generateDrugsResultInformation(tenantCode,drugsConditionMonitoring, pagesR.getRecords());
                        } else {
                            log.error("执行同步任务用药预警的逻辑 ，未查询到患者添加的用药信息 预警条件为 drugsConditionMonitoring【"+ JSON.toJSONString(drugsConditionMonitoring)+"】 第【" + pagesQ.getCurrent() + "】页 信息！");
                        }
                    }
                } else {
                    log.error("执行同步任务用药预警的逻辑 ，未查询到患者添加的用药信息 预警条件为 drugsConditionMonitoring【"+JSON.toJSONString(drugsConditionMonitoring)+"】 第【" + pagesQ.getCurrent() + "】页 信息！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("执行同步任务用药预警的逻辑 失败！", e);
        }finally {
            //执行完成后移除任务标记
            redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch() + ":" + tenantCode, MonitoringTaskType.FINISH.operateType);
        }
    }

    @Autowired
    PatientDrugsTimeService patientDrugsTimeService;

    @Autowired
    PatientDrugsTimeSettingMapper patientDrugsTimeSettingMapper;

    /**
     *  当前条件-患者添加的用药-是否满足生成购药预警
     * @param tenantCode
     * @param drugsConditionMonitoring
     * @param patientDrugss
     */
    private void generateDrugsResultInformation(String tenantCode,DrugsConditionMonitoring drugsConditionMonitoring,List<PatientDrugs> patientDrugss){
        BaseContextHandler.setTenant(tenantCode);
        List<DrugsResultInformation> drugsResultInformations = new ArrayList<>();
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());
        if (CollectionUtils.isEmpty(patientDrugss) || ObjectUtils.isEmpty(drugsConditionMonitoring)){
            return;
        }
        // 总药量=药量计数*盒数 总消耗量= 每天用药次数*每次剂量*用药天数（当前日期-开始吃药日期）   总剩余量 = 总药量-消耗量
        LocalDate now = LocalDate.now();
        for (PatientDrugs patientDrug:patientDrugss){
            LocalDate startTakeMedicineDate = patientDrug.getStartTakeMedicineDate();
            if (patientDrug.getNumberOfDay()==null || patientDrug.getDose()==null || ObjectUtils.isEmpty(startTakeMedicineDate)){
                log.error("药品ID【"+patientDrug.getDrugsId()+"】对应药品每天用药次数、每次剂量、开始吃药日期不存在！");
                continue;
            }
            SysDrugs sysDrugs =  sysDrugsService.getById(patientDrug.getDrugsId());
            if (ObjectUtils.isEmpty(sysDrugs)){
                log.error("药品ID【"+patientDrug.getDrugsId()+"】对应药品不存在！");
                continue;
            }
            if (sysDrugs.getDosageCount()==null || patientDrug.getNumberOfBoxes()==null){
                log.error("药品ID【"+patientDrug.getDrugsId()+"】对应药品药量计数或者盒数不存在！");
                continue;
            }
            // ~~~~~~ 换个算法
            // 计算总药量
            BigDecimal dosageCount = new BigDecimal(sysDrugs.getDosageCount());
            BigDecimal numberOfBoxes = new BigDecimal(patientDrug.getNumberOfBoxes());
            BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);
            // 获取 药品已吃药量
            // 统计patientDrugsTime 表中的已用剂量
            // 已经吃了的总剂量
            List<PatientDrugsTimeSetting> timeSettings = null;
            if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour) && patientDrug.getCycleDuration() > 1) {
                // 查询他的首次吃药时间
                timeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                        .eq(PatientDrugsTimeSetting::getPatientId, patientDrug.getPatientId())
                        .eq(PatientDrugsTimeSetting::getPatientDrugsId, patientDrug.getDrugsId()));
            }

            // 距离当前时间最早的 吃药周期开始时间。 这个时间可能已经过去，可能没有过去。
            LocalDateTime nowTime = LocalDateTime.now();
            LocalDateTime cycleStartTimeDay = PatientDrugsEndDateUtil.getCycleStartTimeDay(patientDrug, timeSettings, nowTime);
            if (cycleStartTimeDay == null) {
                continue;
            }
            // 开始吃药日期的0点是患者的吃药时间
            LocalDateTime startTakeDrugsTime = LocalDateTime.of(startTakeMedicineDate, LocalTime.of(0, 0, 0, 0));
            // 上个周期结束 已经使用的药量
            float drugsDose = patientDrugsTimeService.sumPatientDrugsDose(patientDrug.getId(), cycleStartTimeDay, startTakeDrugsTime);
            BigDecimal usedDose = new BigDecimal(drugsDose);
            // 总药量 - 已经吃的药量 = 剩余药量。
            // 计算一个周期内吃多少药量 周期内的吃药次数*每次吃多少
            BigDecimal cycleNeedDose;
            if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
                BigDecimal needDose = new BigDecimal(patientDrug.getDose());
                // 一个周期需要用药量
                cycleNeedDose = needDose;
            } else {
                BigDecimal numberOfDay = new BigDecimal(patientDrug.getNumberOfDay());
                BigDecimal needDose = new BigDecimal(patientDrug.getDose());
                // 一个周期需要用药量
                cycleNeedDose = numberOfDay.multiply(needDose);
            }

            // 总药量 - 已经使用的药量 = 剩余药量
            BigDecimal lastDose = totalDosage.subtract(usedDose);


            // 如果剩余药量 不足 0， 说明药吃完了。 计算购药逾期多少天了。
            double doubleValue = lastDose.doubleValue();
            // 预警提条件-购药逾期 总剩余量<0 并且 超出部分大于等于一天用药量   result = -1,表示 前者小于后者  result = 0,表示 前者等于后者 result = 1,表示 前者大于后者
            Boolean overdueWarn = false;
            // 余药不足
            Boolean deficiencyWarn = false;
            // 余药不足或购药逾期的天数
            Integer drugsAvailableDay = 0;
            LocalDate drugsEndTime = null;
            // 剩余药量小于0 或者 剩余药量不足一个周期
            if (doubleValue <= 0 || lastDose.floatValue() < cycleNeedDose.floatValue()) {
                overdueWarn = true;
                // 剩余药量还够吃 N 次， 但是不够这个周期吃
                if (doubleValue > 0 && lastDose.floatValue() > patientDrug.getDose()) {
                    drugsEndTime = cycleStartTimeDay.toLocalDate();
                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
                } else
                // 没药了。或者剩下的药不够吃一次 生成购药逾期记录
                if (doubleValue >= 0 && lastDose.floatValue() < patientDrug.getDose()) {
                    // 刚刚没药。
                    if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
                        drugsEndTime = cycleStartTimeDay.plusHours(-patientDrug.getCycleDuration()).toLocalDate();
                    } else {
                        drugsEndTime = cycleStartTimeDay.toLocalDate().plusDays(-1);
                    }
                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
                } else {
                    // 根据多吃的药量(这里这个多吃的药量是上个周期结束一共吃的药量)
                    // 一个周期吃的药量。 和多吃的药量，算出 过去了多少个周期。 根据周期数 算出来，药品用完时间
                    // 药品不够下一个周期吃。就放到购药预警栏目中
                    drugsEndTime = PatientDrugsEndDateUtil.getPastLastCycleEndTime(cycleStartTimeDay, patientDrug.getCycleDuration(),
                            lastDose, cycleNeedDose, patientDrug.getTimePeriod());
                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
                }
            } else {
                // 直接算出来剩余的药量 够吃几次。
                // 推算药品在哪一天用药后。药量不足。
                LocalDate lastCycleEndTime = PatientDrugsEndDateUtil.getMedicineRunOutTime(lastDose, cycleStartTimeDay, patientDrug);
                if (Objects.isNull(lastCycleEndTime)) {
                    continue;
                }
                if (drugsConditionMonitoring.getReminderTime() == null) {
                    continue;
                }
                // 余药不足的提醒时间
                // 使用用药结束时间 往前推 用药提醒天数。则为生成余药不足的日期。
                LocalDate dateTime = lastCycleEndTime.plusDays(-drugsConditionMonitoring.getReminderTime() +1);
                // 当前日期 小于 余药不足的日期， 则不需要处理。 还未到余药不足的提醒日期
                if (now.isBefore(dateTime)) {
                    continue;
                }
                deficiencyWarn = true;
                drugsEndTime = lastCycleEndTime;
                // 当前日期 等于 余药不足的日期， 则今天是生成余药不足预警的第一天。药品剩余天数是 提醒的天数。
                if (now.isEqual(dateTime)) {
                    drugsAvailableDay = Integer.parseInt(drugsConditionMonitoring.getReminderTime().toString());
                }
                // 当前日期 大于 余药不足的日期，
                if (now.isAfter(dateTime)) {
                    // 计算今天和 dateTime.toLocalDate() 相差了几天。
                    Duration duration = Duration.between(now.atStartOfDay(), lastCycleEndTime.atStartOfDay());
                    drugsAvailableDay = Integer.parseInt(duration.toDays() + 1 + "");
                }

                // 剩余药量够一个周期吃。 或者周期是 天数 或小时数
//                if (lastDose.compareTo(cycleNeedDose) >= 0 ||
//                        (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.day) && patientDrug.getCycleDuration() == 1) ||
//                        patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//
//                    // 剩余药量 / 周期用药 = 剩余药品可用周期数
//                    BigDecimal divide = lastDose.divide(cycleNeedDose, 0, BigDecimal.ROUND_UP);
//                    // 还有药，计算吃完这些药的周期结束时间 = 最后用药时间
//                    LocalDateTime lastCycleEndTime = PatientDrugsEndDateUtil.getLastCycleEndTime(cycleStartTimeDay, patientDrug.getCycleDuration(), divide, patientDrug.getTimePeriod());
//                    if (Objects.isNull(lastCycleEndTime)) {
//                        continue;
//                    }
//                    // 余药不足的提醒时间
//                    // 使用周期结束时间 往前推 用药提醒天数。则为生成余药不足的日期。
//                    LocalDateTime dateTime = lastCycleEndTime.plusDays(-drugsConditionMonitoring.getReminderTime() +1);
//                    // 当前日期 小于 余药不足的日期， 则不需要处理。 还未到余药不足的提醒日期
//                    if (now.isBefore(dateTime.toLocalDate())) {
//                        continue;
//                    }
//                    deficiencyWarn = true;
//                    drugsEndTime = lastCycleEndTime.toLocalDate();
//                    // 当前日期 等于 余药不足的日期， 则今天是生成余药不足预警的第一天。药品剩余天数是 提醒的天数。
//                    if (now.isEqual(dateTime.toLocalDate())) {
//                        drugsAvailableDay = Integer.parseInt(drugsConditionMonitoring.getReminderTime().toString());
//                    }
//                    // 当前日期 大于 余药不足的日期，
//                    if (now.isAfter(dateTime.toLocalDate())) {
//                        // 计算今天和 dateTime.toLocalDate() 相差了几天。
//                        Duration duration = Duration.between(now.atStartOfDay(), lastCycleEndTime);
//                        drugsAvailableDay = Integer.parseInt(duration.toDays() + 1 + "");
//                    }
//                }
            }

            // ~~~~~~~~

            // 总药量=药量计数*盒数
//            BigDecimal dosageCount = new BigDecimal(sysDrugs.getDosageCount());
//            BigDecimal numberOfBoxes = new BigDecimal(patientDrug.getNumberOfBoxes());
//            BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);
            //总消耗量= 每天用药次数*每次剂量*用药天数（开始吃药日期到当前日期的天数）
//            BigDecimal numberOfDay = new BigDecimal(patientDrug.getNumberOfDay());
//            BigDecimal dose = new BigDecimal(patientDrug.getDose());
//            BigDecimal useDay = new BigDecimal(getUseDay(patientDrug.getStartTakeMedicineDate(),LocalDate.now()));
//            BigDecimal totalConsume = numberOfDay.multiply(dose).multiply(useDay);
            //总剩余量 = 总药量-消耗量
//            BigDecimal totalSurplus = totalDosage.subtract(totalConsume);
            //预警提条件-购药逾期 总剩余量<0 并且 超出部分大于等于一天用药量   result = -1,表示 前者小于后者  result = 0,表示 前者等于后者 result = 1,表示 前者大于后者
//            Boolean overdueWarn = false;
//            if (totalSurplus.compareTo(new BigDecimal(0) )==-1 && totalSurplus.abs().compareTo(numberOfDay.multiply(dose))!=-1){
//                overdueWarn = true;
//            }
//            //预警提条件-余药不足 总剩余量=>0 并且 总剩余量 <= 预警天数*每天用药次数*每次剂量
//            Boolean deficiencyWarn = false;
//            if (totalSurplus.compareTo(new BigDecimal(0) )!=-1 && totalSurplus.compareTo(new BigDecimal(drugsConditionMonitoring.getReminderTime()).multiply(numberOfDay).multiply(dose))!=1){
//                deficiencyWarn = true;
//            }
//            //预警提条件-余药不足 总剩余量<0 并且 超出部分小于于一天用药量
//            if(totalSurplus.compareTo(new BigDecimal(0) )==-1 && totalSurplus.abs().compareTo(numberOfDay.multiply(dose))==-1){
//                deficiencyWarn = true;
//            }
            DrugsResultInformation drugsResultInformation = new DrugsResultInformation();
            drugsResultInformation.setId(snowflake.nextId());
            if (overdueWarn){
                drugsResultInformation.setWarningType(WarningTypeEnum.BO_DRUGS.getCode());
            }else {
                drugsResultInformation.setWarningType(WarningTypeEnum.NOT_DRUGS.getCode());
            }
            drugsResultInformation.setPatientId(patientDrug.getPatientId());
            drugsResultInformation.setDrugsEndTime(drugsEndTime);
            drugsResultInformation.setDrugsAvailableDay(drugsAvailableDay);
            //当天开始吃药并且当前药品吃完，触发余药不足。则可用天数直接置为0
//                if (patientDrug.getStartTakeMedicineDate().isEqual(LocalDate.now()) && totalSurplus.compareTo(new BigDecimal(0) )!=1 && deficiencyWarn){
//                    drugsResultInformation.setDrugsAvailableDay(0);
//                }
            drugsResultInformation.setDrugsConditionId(drugsConditionMonitoring.getId());
            drugsResultInformation.setDrugsId(sysDrugs.getId());
            drugsResultInformation.setDrugsName(sysDrugs.getName());
            drugsResultInformation.setSpec(sysDrugs.getSpec());
            drugsResultInformation.setManufactor(sysDrugs.getManufactor());
            drugsResultInformation.setReminderTime(drugsConditionMonitoring.getReminderTime());
            drugsResultInformation.setBuyingMedicineUrl(drugsConditionMonitoring.getBuyingMedicineUrl());
            drugsResultInformation.setTemplateMsgId(drugsConditionMonitoring.getTemplateMsgId());
            drugsResultInformation.setTemplateMsgSendStatus(TemplateMsgSendStatusEnum.UN_SEND.getCode());
            drugsResultInformations.add(drugsResultInformation);
        }
        if (CollectionUtils.isEmpty(drugsResultInformations)){
            return;
        }
        List<Long> patientIds= drugsResultInformations.stream().map(DrugsResultInformation::getPatientId).distinct().collect(Collectors.toList());
        //获取患者已经医生名称
        R<List<Patient>> patientsR = patientApi.listByIds(patientIds);
        if (patientsR.getIsSuccess() && !CollectionUtils.isEmpty(patientsR.getData())){
            Map<Long,Patient> patientMap = patientsR.getData().stream().collect(Collectors.toMap(Patient::getId, Patient->Patient));
            //过滤掉患者不存在的填写异常数据。
            drugsResultInformations = drugsResultInformations.stream().filter(i-> patientMap.get(i.getPatientId())!=null).collect(Collectors.toList());
            for (DrugsResultInformation drugsResultInformation:drugsResultInformations){
                if (patientMap.get(drugsResultInformation.getPatientId())==null){
                    log.error("患者信息异常！无法获取 患者【"+drugsResultInformation.getPatientId()+"】的信息！");
                    continue;
                }
                drugsResultInformation.setPatientName(patientMap.get(drugsResultInformation.getPatientId()).getName());
                drugsResultInformation.setAvatar(patientMap.get(drugsResultInformation.getPatientId()).getAvatar());
                drugsResultInformation.setDoctorId(patientMap.get(drugsResultInformation.getPatientId()).getDoctorId());
                drugsResultInformation.setDoctorName(patientMap.get(drugsResultInformation.getPatientId()).getDoctorName());
                drugsResultInformation.setNursingId(patientMap.get(drugsResultInformation.getPatientId()).getServiceAdvisorId());
            }
        }
        //创建药品预警
        drugsResultInformationService.saveBatch(drugsResultInformations);
        //更新患者添加的用药监听状态
        Map<String,DrugsResultInformation>  drugsResultInformationsMap = new HashMap<>();
        for (DrugsResultInformation drugsResultInformation:drugsResultInformations){
            drugsResultInformationsMap.put(drugsResultInformation.getPatientId()+""+drugsResultInformation.getDrugsId(),drugsResultInformation);
        }
        List<Long> patientDrugsIds=  patientDrugss.stream().filter(i->drugsResultInformationsMap.get(i.getPatientId()+""+i.getDrugsId())!=null).map(PatientDrugs::getId).distinct().collect(Collectors.toList());
        LbuWrapper<PatientDrugs> lbuWrapper = new LbuWrapper();
        lbuWrapper.in(PatientDrugs::getId,patientDrugsIds);
        lbuWrapper.set(PatientDrugs::getEarlyWarningMonitor,EarlyWarningMonitorEnum.NO_NEED_MONITOR.code);
        patientDrugsService.update(lbuWrapper);
        return;
    }

    /**
     * 获取两个日期天数差 （包含首尾日期当天）
     * @param start
     * @param end
     * @return
     */
    private long getUseDay(LocalDate start,LocalDate end){
        return start.until(end, ChronoUnit.DAYS)+1L;
    }

    /**
     * 剩余药量可用（逾期）天数
     * @param drugsEndTime 药量用完时间
     * @param totalSurplus 总剩余量
     * @param dayCount     每日用药量
     * @return
     */
    private long getDrugsAvailableDay(LocalDate drugsEndTime,BigDecimal totalSurplus,BigDecimal dayCount){
        long result = 0L;
        //逾期天数计算
        if (drugsEndTime.until(LocalDate.now(), ChronoUnit.DAYS)>=0 && totalSurplus.compareTo(new BigDecimal(0))==-1 && totalSurplus.abs().compareTo(dayCount)==1){
            result = drugsEndTime.until(LocalDate.now(), ChronoUnit.DAYS);
            return result < 0 ? (-result)+1 :result+1;
        }
        //剩余药量可用天数计算
        result = totalSurplus.divide(dayCount,0, BigDecimal.ROUND_UP).longValue();
        return result < 0 ? -result :result;
    }
    /**
     * 药量用完时间
     * @param startTakeMedicineDate 开始吃药时间
     * @param dosageCount 总数量
     * @param dayCount    每日用药量
     * @return
     */
    private LocalDate getDrugsEndTime(LocalDate startTakeMedicineDate,BigDecimal dosageCount,BigDecimal dayCount){
        return startTakeMedicineDate.plusDays(dosageCount.divide(dayCount,0,BigDecimal.ROUND_UP).longValue());
    }

    /**
     * 患者管理-用药预警-预警药品及条件 -患者更新药箱中药品
     * @param model
     * @param tenantCode
     * @return
     */
    @Override
    public Boolean synDrugsConditionMonitoringTaskPatientDrugsChange(PatientDrugs model, String tenantCode) {
        //若状态是历史用药则删除相关预警信息
        BaseContextHandler.setTenant(tenantCode);
        model = patientDrugsService.getById(model.getId());
        if (!ObjectUtils.isEmpty(model) && PatientDrugsStatusEnum.ON.getCode()!=model.getStatus()){
            List<PatientDrugs> patientDrugss = new ArrayList<>();
            patientDrugss.add(model);
            this.synDeleteDrugsResultInformation(patientDrugss,tenantCode);
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }

    /**
     * 患者管理-用药预警-患者停药删除相关预警信息
     * @param patientDrugs
     * @param tenantCode
     * @return
     */
    @Override
    public Boolean synDeleteDrugsResultInformation(List<PatientDrugs> patientDrugs, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        if (CollectionUtils.isEmpty(patientDrugs)){
            return Boolean.TRUE;
        }
        patientDrugs.stream().forEach(i->{
            LbqWrapper<DrugsResultInformation> lbqWrapper = new LbqWrapper();
            lbqWrapper.eq(DrugsResultInformation::getPatientId,i.getPatientId());
            lbqWrapper.eq(DrugsResultInformation::getDrugsId,i.getDrugsId());
            drugsResultInformationService.remove(lbqWrapper);
        });
        return Boolean.TRUE;
    }

    /**
     * 患者管理-用药预警-推送购药|逾期预警模板消息
     * @param tenant
     * @return
     */
    @Override
    public Boolean pushBuyDrugsWarningMsgTask(Tenant tenant) {
        BaseContextHandler.setTenant(tenant.getCode());
        //获取需要发送的预警信息
        LbuWrapper<DrugsResultInformation> lbuWrapper = new LbuWrapper();
        lbuWrapper.eq(DrugsResultInformation::getTemplateMsgSendStatus,TemplateMsgSendStatusEnum.UN_SEND.getCode());
//        lbuWrapper.isNotNull(DrugsResultInformation::getTemplateMsgId);
        List<DrugsResultInformation> drugsResultInformations = drugsResultInformationService.list(lbuWrapper);
        if (CollectionUtils.isEmpty(drugsResultInformations)){
            return Boolean.TRUE;
        }
        //批量发送预警模板消息
        List<Long> ids = batchSendTemplateMessage(tenant,drugsResultInformations);
        //标记已发送的预警信息
        if (!CollectionUtils.isEmpty(ids)){
            lbuWrapper.clear();
            lbuWrapper.in(DrugsResultInformation::getId,ids);
            lbuWrapper.set(DrugsResultInformation::getTemplateMsgSendStatus,TemplateMsgSendStatusEnum.AL_SEND.getCode());
            drugsResultInformationService.update(lbuWrapper);
        }
        return Boolean.TRUE;
    }

    /**
     * 批量发送预警模板消息
     * @param tenant
     * @param drugsResultInformations
     * @return 模板消息发送成功的预警信息id
     */
    private List<Long> batchSendTemplateMessage(Tenant tenant, List<DrugsResultInformation> drugsResultInformations){
        BaseContextHandler.setTenant(tenant.getCode());
        List<Long> ids = new ArrayList<>();
        //发送模板消息
        if (CollectionUtils.isEmpty(drugsResultInformations)){
            return ids;
        }
        //
        List<Long> patientIds= drugsResultInformations.stream().map(DrugsResultInformation::getPatientId).distinct().collect(Collectors.toList());
        //获取患者信息
        R<List<Patient>> patientsR = patientApi.listByIds(patientIds);
        Map<Long,Patient> patientMap = new HashMap<>();
        if (patientsR.getIsSuccess() && !CollectionUtils.isEmpty(patientsR.getData())){
            patientMap = patientsR.getData().stream().collect(Collectors.toMap(Patient::getId, Patient->Patient));
        }
        String tenantName = tenant.getName();
        String role = dictionaryItemService.findDictionaryItemName(DictionaryItemService.DOCTOR);
        for (DrugsResultInformation drugsResultInformation:drugsResultInformations){
            Patient patient = patientMap.get(drugsResultInformation.getPatientId());
            if (ObjectUtils.isEmpty(patient)){
                log.error("发送模板消息 异常!患者不存在！,patientId={}",drugsResultInformation.getPatientId());
                continue;
            }
            // 企业服务号
            if (tenant.isCertificationServiceNumber()) {
                SendTemplateMessageForm sendTemplateMessageForm  = null;
                try {
                    sendTemplateMessageForm = getSendTemplateMessageForm(tenant,drugsResultInformation, patient);
                    if (ObjectUtils.isEmpty(sendTemplateMessageForm)){
                        log.error("生成消息内容失败！,tenant={},drugsResultInformation={},patient={}",tenant,drugsResultInformation, patient);
                        continue;
                    }
                } catch (Exception e) {
                    log.error("生成消息内容失败！,tenant={},drugsResultInformation={},patient={}",tenant,drugsResultInformation, patient);
                    e.printStackTrace();
                    continue;
                }
                try {
                    log.info("发送模板消息 开始！入参 sendTemplateMessageForm={}",sendTemplateMessageForm);
                    R<String> message = weiXinApi.sendTemplateMessage(sendTemplateMessageForm);
                    if (message.getIsSuccess()){
                        ids.add(drugsResultInformation.getId());
                    }
                    log.info("发送模板消息 结束！返回 message={}",message);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("批量发送预警模板消息 异常！drugsResultInformation={},sendTemplateMessageForm={}",drugsResultInformation,sendTemplateMessageForm);
                }
            } else {
                // 个人服务号发送短信
                String smsParams = BusinessReminderType.getPatientDrugPurchaseWarning(tenantName,
                        patient.getDoctorName(), role, drugsResultInformation.getDrugsName());
                String wxPatientBizUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.BUY_DRUGS_URL, drugsResultInformation.getDrugsId()));
                // 创建一条 今日待办消息 的推送任务
                BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                        .mobile(patient.getMobile())
                        .wechatRedirectUrl(wxPatientBizUrl)
                        .diseasesType(tenant.getDiseasesType() == null ? TenantDiseasesTypeEnum.other.toString() : tenant.getDiseasesType().toString())
                        .type(BusinessReminderType.PATIENT_DRUG_PURCHASE_WARNING)
                        .tenantCode(tenant.getCode())
                        .queryParams(smsParams)
                        .patientId(patient.getId())
                        .status(0)
                        .openThisMessage(0)
                        .finishThisCheckIn(0)
                        .build();
                businessReminderLogControllerApi.sendNoticeSms(logSaveDTO);
                ids.add(drugsResultInformation.getId());
            }

        }
        return ids;
    }


    /**
     * 获取模板消息发送内容
     * @param tenant
     * @param drugsResultInformation
     */
    private SendTemplateMessageForm getSendTemplateMessageForm(Tenant tenant,DrugsResultInformation drugsResultInformation,Patient patient){
        R<TemplateMsgDto> templateMsgDtoR;
        if (Objects.isNull(drugsResultInformation.getTemplateMsgId())) {
            templateMsgDtoR = templateApi.getCommonCategoryServiceWorkOrderMsg(tenant.getCode(), null, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
        } else {
            templateMsgDtoR = templateApi.getCommonCategoryServiceWorkOrderMsg(tenant.getCode(), drugsResultInformation.getTemplateMsgId(), null);
        }
        if (templateMsgDtoR.getIsSuccess() && !ObjectUtils.isEmpty(templateMsgDtoR.getData())){
            return buildTemplate(templateMsgDtoR.getData(),patient.getId(),patient.getOpenId(),patient.getWxAppId(),tenant,drugsResultInformation, patient.getName());
        }
        return null;
    }

    /**
     * 生产模板消息
     * @param templateMsgDto
     * @param patientId
     * @param openId
     * @param appId
     * @param tenant
     * @param drugsResultInformation
     * @param name
     * @return
     */
    private SendTemplateMessageForm buildTemplate(TemplateMsgDto templateMsgDto, Long patientId,
                                                  String openId, String appId, Tenant tenant, DrugsResultInformation drugsResultInformation,  String name) {
        // 线程中有的话。就可以不传了
        SendTemplateMessageForm messageForm = new SendTemplateMessageForm();
        messageForm.setWxAppId(appId);
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        message.setTemplateId(templateMsgDto.getTemplateId());
        message.setToUser(openId);
        String url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), String.format(H5Router.BUY_DRUGS_URL, drugsResultInformation.getDrugsId()));
        message.setUrl(url);
        List<WxMpTemplateData> data = new ArrayList();
        // 发现用的是 类目模板。
        if (templateMsgDto.getCommonCategory()) {
            data = CommonTemplateServiceWorkModel.buildWxMpTemplateData(name, I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.DRUGS_RESULT_INFORMATION, tenant.getDefaultLanguage()));
            message.setData(data);
            messageForm.setTemplateMessage(message);
            return messageForm;
        }

        List<TemplateMsgFields> msgFieldsList = templateMsgDto.getFields();
        boolean needForm = false;
        for (TemplateMsgFields field : msgFieldsList) {
            if (field.getType() == 1) {
                needForm = true;
                break;
            }
        }
        List<FormFieldDto> allFields = new ArrayList<>(50);
        if (needForm) {
            List<FormResult> list = getFormResultService().getBasicAndLastHealthFormResult(patientId);
            // 查询 患者填写的 基本消息和疾病信息 表单。
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(list)) {
                for (FormResult formResult : list) {
                    List<FormFieldDto> formFields = JSON.parseArray(formResult.getJsonContent(), FormFieldDto.class);
                    allFields.addAll(formFields);
                }
            }
        }
        for (TemplateMsgFields fields : msgFieldsList) {
            if (fields.getType() != null && 0 != fields.getType()) {
                if (fields.getType() == 1) {
                    StringBuilder sb = new StringBuilder();
                    // 将 模板中 关联表单属性的 数据 从表单中拉取要填充的结果。
                    for (FormFieldDto allField : allFields) {
                        if (allField.getId() != null && allField.getId().equals(fields.getValue())) {
                            // 从表单中获取用户填写的结果。 并设置到推送模板的值中
                            String values = allField.parseResultValues();
                            sb.append(values);
                        }
                    }
                    // 添加一条
                    data.add(new WxMpTemplateData(fields.getAttr(), sb.toString(), fields.getColor()));
                } else if (fields.getType() == 2) {
                    // 模板为 时间时，
                    data.add(new WxMpTemplateData(fields.getAttr(), DateUtils.date2Str(new Date(), DateUtils.Y_M_D_HM), fields.getColor()));
                }
            } else {
                data.add(new WxMpTemplateData(fields.getAttr(), fields.getValue(), fields.getColor()));
            }
        }

        MsgPatientSystemMessageSaveDTO systemMessageSaveDTO = new MsgPatientSystemMessageSaveDTO(TemplateMessageIndefiner.BUY_DRUGS_REMINDER,
                null,
                url,
                patientId,
                LocalDateTime.now(),
                drugsResultInformation.getDrugsName(),
                tenant.getCode());
        systemMessageSaveDTO.createPushContent( null, null, null);
        patientSystemMessageApi.saveSystemMessage(systemMessageSaveDTO);
        message.setData(data);
        messageForm.setTemplateMessage(message);
        return messageForm;
    }

    @Autowired
    MsgPatientSystemMessageApi patientSystemMessageApi;

    @Override
    public void synUpdateBuyDrugsWarningTask(String code) {
        BaseContextHandler.setTenant(code);
        List<DrugsResultInformation> list = drugsResultInformationService.list(Wraps.lbQ());
        if (CollUtil.isNotEmpty(list)) {
            for (DrugsResultInformation resultInformation : list) {
                if (resultInformation.getWarningType().equals(WarningTypeEnum.NOT_DRUGS.getCode())) {
                    Integer drugsAvailableDay = resultInformation.getDrugsAvailableDay();
                    if (drugsAvailableDay - 1 <= 0) {
                        resultInformation.setWarningType(WarningTypeEnum.BO_DRUGS.getCode());
                        resultInformation.setDrugsAvailableDay(1);
                    } else {
                        resultInformation.setDrugsAvailableDay(drugsAvailableDay - 1);
                    }
                } else {
                    Integer drugsAvailableDay = resultInformation.getDrugsAvailableDay();
                    resultInformation.setDrugsAvailableDay(drugsAvailableDay + 1);
                }
            }
            drugsResultInformationService.updateBatchById(list);
        }


    }
}
