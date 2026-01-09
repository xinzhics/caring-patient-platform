package com.caring.sass.nursing.controller.drugs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.update.LbuWrapper;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.nursing.dao.unfinished.UnfinishedPatientRecordMapper;
import com.caring.sass.nursing.dto.drugs.DrugsArrangeVo;
import com.caring.sass.nursing.dto.drugs.PatientDrugsTimePageDTO;
import com.caring.sass.nursing.dto.drugs.PatientDrugsTimeSaveDTO;
import com.caring.sass.nursing.dto.drugs.PatientDrugsTimeUpdateDTO;
import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;
import com.caring.sass.nursing.entity.unfinished.UnfinishedPatientRecord;
import com.caring.sass.nursing.service.drugs.PatientDayDrugsService;
import com.caring.sass.nursing.service.drugs.PatientDrugsTimeService;
import com.caring.sass.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 每次推送生成一条记录，（记录药量，药品）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientDrugsTime")
@Api(value = "PatientDrugsTime", tags = "每次推送生成一条记录，（记录药量，药品）")
//@PreAuth(replace = "patientDrugsTime:")
public class PatientDrugsTimeController extends SuperController<PatientDrugsTimeService, Long, PatientDrugsTime, PatientDrugsTimePageDTO, PatientDrugsTimeSaveDTO, PatientDrugsTimeUpdateDTO> {

    private final PatientDayDrugsService patientDayDrugsService;

    private final UnfinishedPatientRecordMapper unfinishedPatientRecordMapper;

    @Autowired
    ImApi imApi;

    public PatientDrugsTimeController(PatientDayDrugsService patientDayDrugsService, UnfinishedPatientRecordMapper unfinishedPatientRecordMapper) {
        this.patientDayDrugsService = patientDayDrugsService;
        this.unfinishedPatientRecordMapper = unfinishedPatientRecordMapper;
    }


    /**
     * 根据用药秒级时间戳查询
     *
     * @param drugsTimestamp 用药秒级时间戳
     * @return 患者用药
     */
    @ApiOperation("根据用药秒级时间戳查询")
    @GetMapping(value = "/queryByDrugsTimestamp")
    public R<List<PatientDrugsTime>> queryByDrugsTimestamp(@RequestParam("drugsTimestamp") @NotNull(message = "用药秒级时间戳") Long drugsTimestamp) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(drugsTimestamp, 0, ZoneOffset.of("+8"));
        LocalDateTime endTime = dateTime.plusMinutes(15);
        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            return R.success(new ArrayList<>());
        }

        LbqWrapper<PatientDrugsTime> lbqWrapper = Wraps.<PatientDrugsTime>lbQ()
                .eq(PatientDrugsTime::getPatientId,userId)
                .ge(PatientDrugsTime::getDrugsTime, dateTime)
                .lt(PatientDrugsTime::getDrugsTime, endTime);
        List<PatientDrugsTime> patientDrugsTimes = baseService.list(lbqWrapper);
        return R.success(patientDrugsTimes);
    }


    /**
     * 查询今日的用药安排
     *
     * @param drugsDay 用药日期
     */
    @ApiOperation("查询某天的用药安排(yyyy-MM-dd)")
    @GetMapping(value = "/queryByDay")
    public R<List<DrugsArrangeVo>> queryByDrugsTimestamp(
            @RequestParam("patientId") Long patientId,
            @RequestParam("drugsDay") String drugsDay) {
        LocalDateTime startTime = DateUtils.getStartTime(drugsDay);
        LocalDateTime endTime = DateUtils.getEndTime(drugsDay);

        List<PatientDrugsTime> patientDrugsTimes = baseService.queryByDrugsTimestamp(patientId, startTime, endTime);

        if (CollUtil.isEmpty(patientDrugsTimes)) {
            return R.success(null);
        }
        Map<LocalDateTime, List<PatientDrugsTime>> timeListMap = patientDrugsTimes.stream()
                .collect(Collectors.groupingBy(PatientDrugsTime::getDrugsTime));
        List<DrugsArrangeVo> drugsArrangeVos = new ArrayList<>(timeListMap.size());

        Set<Map.Entry<LocalDateTime, List<PatientDrugsTime>>> entries = timeListMap.entrySet();
        DrugsArrangeVo vo;
        int status;
        for (Map.Entry<LocalDateTime, List<PatientDrugsTime>> entry : entries) {
            vo = new DrugsArrangeVo();
            LocalDateTime dateTime = entry.getKey();
            List<PatientDrugsTime> value = entry.getValue();
            vo.setDrugsTime(dateTime);
            vo.setPatientDrugsTimes(value);
            status = 1;
            for (PatientDrugsTime drugsTime : value) {
                if (2 == drugsTime.getStatus()) {
                    status = 2;
                    break;
                }
            }
            vo.setStatus(status);
            drugsArrangeVos.add(vo);
        }
        Collections.sort(drugsArrangeVos);
        return R.success(drugsArrangeVos);
    }


    /**
     * 漏服补录打卡
     *
     * @param ids 用药id
     */
    @ApiOperation("漏服补录打卡")
    @PostMapping(value = "/reClockIn/{patientId}")
    public R<Boolean> reClockIn(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "messageId", required = false) Long messageId,
            @RequestParam("ids") Long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            return R.success();
        }

        List<Long> clockIds = Arrays.asList(ids);
        List<PatientDrugsTime> patientDrugsTimes = baseService.list(Wraps.<PatientDrugsTime>lbQ()
                .in(PatientDrugsTime::getId, clockIds));
        if (Objects.nonNull(messageId)) {
            String tenant = BaseContextHandler.getTenant();
            updateImReceipt(messageId, tenant);
        }
        if (CollUtil.isEmpty(patientDrugsTimes)) {
            return R.success();
        }
        PatientDrugsTime patientDrugsTime = patientDrugsTimes.get(0);
        LocalDateTime drugsTime = patientDrugsTime.getDrugsTime();
        missedServiceClockIn(patientId, clockIds, drugsTime);
        return R.success();
    }


    /**
     * 更新im消息的未填状态
     * @param chatId
     * @param tenantCode
     */
    public void updateImReceipt(Long chatId, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        imApi.updateImRemind(chatId);
    }

    /**
     * 用药打卡
     *
     * @param ids 用药id
     */
    @ApiOperation("用药打卡")
    @PostMapping(value = "/clockIn")
    public R<Boolean> clockIn(@RequestParam("ids") Long[] ids,
                              @RequestParam(value = "messageId", required = false) Long messageId) {
        if (Objects.isNull(ids) || ids.length == 0) {
            return R.success();
        }

        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            return R.success();
        }

        List<Long> clockIds = Arrays.asList(ids);
        List<PatientDrugsTime> patientDrugsTimes = baseService.list(Wraps.<PatientDrugsTime>lbQ()
                .in(PatientDrugsTime::getId, clockIds));
        if (CollUtil.isEmpty(patientDrugsTimes)) {
            return R.success();
        }

        // 用药时间距离当前时间超过两个小时，提示  您已错过打卡时间，请在收到消息后两小时内完成打卡
        PatientDrugsTime drugsTime = patientDrugsTimes.get(0);
        if (Objects.nonNull(drugsTime)) {
            LocalDateTime start = drugsTime.getDrugsTime();
            LocalDateTime now = LocalDateTime.now();
            boolean timeout = LocalDateTimeUtil.between(start, now, ChronoUnit.HOURS) > 2;
            if (timeout) {
                return R.fail("您已错过打卡时间，请在收到消息后两小时内完成打卡");
            }
        }
        if (Objects.nonNull(messageId)) {
            String tenant = BaseContextHandler.getTenant();
            updateImReceipt(messageId, tenant);
        }
        // 用药打卡
        missedServiceClockIn(userId, clockIds, drugsTime.getDrugsTime());
        return R.success();
    }

    /**
     * 补录的时候使用的打卡
     * 打卡。并更新 用药日期的打卡次数
     * @param patientId
     * @param clockIds
     * @param drugsTime
     * @return
     */
    @Transactional
    private boolean missedServiceClockIn(Long patientId, List<Long> clockIds, LocalDateTime drugsTime) {
        // 更新用药推送的打卡状态
        LbuWrapper<PatientDrugsTime> patientDrugsTimeLbuWrapper = Wraps.<PatientDrugsTime>lbU()
                .set(PatientDrugsTime::getStatus, 1)
                .in(PatientDrugsTime::getId, clockIds);
        baseService.update(patientDrugsTimeLbuWrapper);

        // 完成打卡，清除 未完成任务跟踪的待处理记录
        unfinishedPatientRecordMapper.delete(Wraps.<UnfinishedPatientRecord>lbQ()
                .in(UnfinishedPatientRecord::getRemindMessageId, clockIds)
                .eq(UnfinishedPatientRecord::getHandleStatus, CommonStatus.NO));

        // 根据吃药时间， 查询吃药那天的 PatientDayDrugs
        LocalDateTime startTime = drugsTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = drugsTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
        List<PatientDayDrugs> patientDayDrugsList = patientDayDrugsService.list(Wraps.<PatientDayDrugs>lbQ()
                .eq(PatientDayDrugs::getPatientId, patientId)
                .between(SuperEntity::getCreateTime, startTime, endTime));
        if (CollUtil.isEmpty(patientDayDrugsList)) {
            return true;
        }
        PatientDayDrugs dayDrugs = patientDayDrugsList.get(0);

        if (dayDrugs.getCheckinedNumber() == null) {
            dayDrugs.setCheckinedNumber(1);
        } else {
            dayDrugs.setCheckinedNumber(dayDrugs.getCheckinedNumber() + 1);
        }
        dayDrugs.setStatus(2);
        patientDayDrugsService.updateById(dayDrugs);
        return true;
    }

    /**
     * 打卡
     */
    private boolean clockIn(Long userId, List<Long> clockIds, List<PatientDrugsTime> patientDrugsTimes) {
        // 更新用药推送的打卡状态
        LbuWrapper<PatientDrugsTime> patientDrugsTimeLbuWrapper = Wraps.<PatientDrugsTime>lbU()
                .set(PatientDrugsTime::getStatus, 1)
                .in(PatientDrugsTime::getId, clockIds);
        baseService.update(patientDrugsTimeLbuWrapper);

        // 更新当天用药的信息
        List<PatientDayDrugs> patientDayDrugsList = patientDayDrugsService.list(Wraps.<PatientDayDrugs>lbQ()
                .eq(PatientDayDrugs::getPatientId, userId)
                .eq(SuperEntity::getCreateTime, LocalDateTimeUtil.beginOfDay(LocalDateTime.now())));
        if (CollUtil.isEmpty(patientDayDrugsList)) {
            return true;
        }
        PatientDayDrugs patientDayDrugs = patientDayDrugsList.get(0);
        patientDayDrugs.setStatus(2);
        patientDayDrugs.setCheckinedNumber(patientDrugsTimes.size());
        patientDayDrugsService.updateById(patientDayDrugs);
        return false;
    }


}
