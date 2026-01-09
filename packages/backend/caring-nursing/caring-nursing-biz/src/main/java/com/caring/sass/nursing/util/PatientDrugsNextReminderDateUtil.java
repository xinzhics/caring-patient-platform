package com.caring.sass.nursing.util;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class PatientDrugsNextReminderDateUtil {

    /**
     * 计算最近一次的推送时间
     * @param model
     * @param timeSettingList
     */
    public static LocalDateTime getPatientDrugsNextReminderDate(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettingList, LocalDateTime now) {
        Integer cycleDuration = model.getCycleDuration();
        PatientDrugsTimePeriodEnum timePeriod = model.getTimePeriod();
        LocalDate startTakeMedicineDate = model.getStartTakeMedicineDate();
        // 计算 患者吃药时间 到 当前时间下。患者最近一次推送的时间。
        switch (timePeriod) {
            case day: {
                return dayTime(cycleDuration, startTakeMedicineDate, now, timeSettingList);
            }
            case hour: {
                return hourTime(cycleDuration, startTakeMedicineDate, now, timeSettingList);
            }
            case moon: {
                return moonTime(cycleDuration, startTakeMedicineDate, now, timeSettingList);
            }
            case week: {
                return weekTime(cycleDuration, startTakeMedicineDate, now, timeSettingList);
            }
            default:
                return null;
        }

    }

    /**
     * 计算最近一次的推送时间
     * @param model
     * @param timeSettingList
     */
    public static LocalDateTime getPatientDrugsNextReminderDate(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettingList) {
            LocalDateTime now = LocalDateTime.now();
            return getPatientDrugsNextReminderDate(model, timeSettingList, now);

    }



    /**
     * 计算 当前所在周期的吃药开始时间
     * @param cycleDuration
     * @param startTakeMedicineDate
     * @return
     */
    protected static LocalDate getCycleStartTime(Integer cycleDuration, LocalDate startTakeMedicineDate, int periodCoefficient, LocalDateTime now) {
        // 计算周期长度。
        //
        int i = cycleDuration * periodCoefficient;

        // 计算 今天 和开始吃药时间中间的 差值
        LocalDate localDate = now.toLocalDate();
        Duration duration = Duration.between(startTakeMedicineDate.atStartOfDay(), localDate.atStartOfDay());
        long days = duration.toDays();
        // 找出 今天所在周期的开始日期
        LocalDate cycleStartTakeMedicineDate;
        if (days == 0) {
            // 本周期的吃药日期是今天
            // 开始吃药 时间 还是  startTakeMedicineDate
            cycleStartTakeMedicineDate = startTakeMedicineDate;
        } else {
            long l = days / i;
            if (l < 1) {
                // 还在第一个周期
                // 开始吃药 时间 还是  startTakeMedicineDate
                cycleStartTakeMedicineDate = startTakeMedicineDate;
            } else {
                cycleStartTakeMedicineDate = startTakeMedicineDate.plusDays(l * i);
            }
        }
        return cycleStartTakeMedicineDate;
    }

    /**
     * 获取下次的开始推送时间
     * @param cycleStartTime
     * @param timeSettingList
     * @param now
     * @return 返回具体的时间就是推送时间。返回null表示本周期没有需要推送的时间。需要计算出下一个周期的开始时间和推送时间
     */
    protected static LocalDateTime getNextPushTime(LocalDate cycleStartTime, List<PatientDrugsTimeSetting> timeSettingList, LocalDateTime now) {

        for (PatientDrugsTimeSetting setting : timeSettingList) {
            Integer dayOfTheCycle = setting.getDayOfTheCycle();
            LocalTime triggerTimeOfTheDay = setting.getTriggerTimeOfTheDay();
            if (Objects.nonNull(dayOfTheCycle)) {
                LocalDate localDate = cycleStartTime.plusDays(dayOfTheCycle - 1);
                LocalDateTime dateTime = LocalDateTime.of(localDate, triggerTimeOfTheDay);
                if (now.isBefore(dateTime)) {
                    return dateTime;
                }
            } else {
                // 当周期为 天的 时候  走这里
                LocalDateTime dateTime = LocalDateTime.of(cycleStartTime, triggerTimeOfTheDay);
                if (now.isBefore(dateTime)) {
                    return dateTime;
                }
            }

        }
        return null;
    }

    /**
     * 周 为 周期维度。计算 从开始吃药到今天，接下来最近的一次推送是什么时间
     * @param cycleDuration 周期长度， 1-30
     * @param startTakeMedicineDate
     * @param timeSettingList  // 要求这个排序为 dayOfTheCycle 从小到大。 triggerTimeOfTheDay 从小到大
     * @return 返回下一次的推送时间。如果是null 表示不需要在推送了。
     */
    protected static LocalDateTime weekTime(Integer cycleDuration, LocalDate startTakeMedicineDate, LocalDateTime now, List<PatientDrugsTimeSetting> timeSettingList) {

        // 获取本周期的开始吃药日期
        LocalDate cycleStartTime = getCycleStartTime(cycleDuration, startTakeMedicineDate, 7, now);
        LocalDateTime nextPushTime = getNextPushTime(cycleStartTime, timeSettingList, now);
        if (Objects.nonNull(nextPushTime)) {
            return nextPushTime;
        } else {
            // 计算下一个周期的开始时间
            cycleStartTime = cycleStartTime.plusDays(cycleDuration * 7);
            nextPushTime = getNextPushTime(cycleStartTime, timeSettingList, now);
            if (Objects.nonNull(nextPushTime)) {
                return nextPushTime;
            } else {
                return null;
            }
        }

    }

    /**
     * 月 为 周期维度。计算 从开始吃药到今天，接下来最近的一次推送是什么时间
     * @param cycleDuration 周期系数 1 - 30
     * @param startTakeMedicineDate
     * @param timeSettingList
     * @return
     */
    protected static LocalDateTime moonTime(Integer cycleDuration, LocalDate startTakeMedicineDate, LocalDateTime now, List<PatientDrugsTimeSetting> timeSettingList) {

        LocalDate cycleStartTime = getCycleStartTime(cycleDuration, startTakeMedicineDate, 30,now);
        LocalDateTime nextPushTime = getNextPushTime(cycleStartTime, timeSettingList, now);
        if (Objects.nonNull(nextPushTime)) {
            return nextPushTime;
        } else {
            // 计算下一个周期的开始时间
            cycleStartTime = cycleStartTime.plusDays(cycleDuration * 30);
            nextPushTime = getNextPushTime(cycleStartTime, timeSettingList, now);
            if (Objects.nonNull(nextPushTime)) {
                return nextPushTime;
            } else {
                return null;
            }
        }

    }

    /**
     * 周 为 周期维度。计算 从开始吃药到今天，接下来最近的一次推送是什么时间
     * @param cycleDuration
     * @param startTakeMedicineDate
     * @param timeSettingList
     * @return
     */
    protected static LocalDateTime hourTime(Integer cycleDuration, LocalDate startTakeMedicineDate, LocalDateTime now, List<PatientDrugsTimeSetting> timeSettingList) {

        // 1小时1次。
        if (cycleDuration == 1) {
            now = now.plusHours(1).withMinute(0).withNano(0).withSecond(0);
            return now;
        }
        if (CollUtil.isEmpty(timeSettingList)) {
            return null;
        }
        PatientDrugsTimeSetting setting = timeSettingList.get(0);
        LocalTime triggerTimeOfTheDay = setting.getTriggerTimeOfTheDay();

        // 当前时间还没有到首次用药时间
        LocalDateTime cycleStartTime = LocalDateTime.of(startTakeMedicineDate, triggerTimeOfTheDay);
        if (now.isBefore(cycleStartTime)) {
            return cycleStartTime;
        }

        // 当前时间已经超过首次用药时间
        cycleStartTime = now.plusHours(cycleDuration).withMinute(triggerTimeOfTheDay.getMinute()).withSecond(0).withNano(0);
        return cycleStartTime;

    }

    /**
     * 周 为 周期维度。计算 从开始吃药到今天，接下来最近的一次推送是什么时间
     * @param cycleDuration
     * @param startTakeMedicineDate
     * @param timeSettingList
     * @return
     */
    protected static LocalDateTime dayTime(Integer cycleDuration, LocalDate startTakeMedicineDate, LocalDateTime now, List<PatientDrugsTimeSetting> timeSettingList) {

        LocalDate cycleStartTime = getCycleStartTime(cycleDuration, startTakeMedicineDate, 1, now);
        LocalDateTime nextPushTime = getNextPushTime(cycleStartTime, timeSettingList, now);
        if (Objects.nonNull(nextPushTime)) {
            return nextPushTime;
        } else {
            // 计算下一个周期的开始时间
            cycleStartTime = cycleStartTime.plusDays(cycleDuration);
            nextPushTime = getNextPushTime(cycleStartTime, timeSettingList, now);
            if (Objects.nonNull(nextPushTime)) {
                return nextPushTime;
            } else {
                return null;
            }
        }

    }



}
