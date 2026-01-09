package com.caring.sass.nursing.util;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class PatientDrugsEndDateUtil {


    /**
     * 使用 周期开始时间。 周期次数。周期维度，计算剩余药量可用的周期最后结束时间
     * @param cycleStartTimeDay 周期的开始时间
     * @param divide 可用周期数
     * @return 最后一个周期的结束日期
     */
    public static LocalDateTime getLastCycleEndTime(LocalDateTime cycleStartTimeDay, Integer cycleDuration, BigDecimal divide, PatientDrugsTimePeriodEnum timePeriod) {
        switch (timePeriod) {
            case day: {
                return  getLastCycleEndTime(cycleStartTimeDay, divide, 1, timePeriod);
            }
            case hour: {
                return getLastCycleEndTime(cycleStartTimeDay, divide, cycleDuration, timePeriod);
            }
            case moon: {
                return getLastCycleEndTime(cycleStartTimeDay, divide, 30, timePeriod);
            }
            case week: {
                return getLastCycleEndTime(cycleStartTimeDay, divide, 7, timePeriod);
            }
            default:
                return null;
        }

    }

    /**
     * 获取周期的最后开始时间
     * @param cycleStartTimeDay 周期开始时间
     * @param divide 可用周期数
     * @param periodCoefficient 周期长度
     * @return
     */
    private static LocalDateTime getLastCycleEndTime(LocalDateTime cycleStartTimeDay, BigDecimal divide, int periodCoefficient, PatientDrugsTimePeriodEnum timePeriod) {

        if (timePeriod.equals(PatientDrugsTimePeriodEnum.hour)) {
            int i = divide.intValue();
            int i1 = i * periodCoefficient;
            LocalDateTime localDateTime = cycleStartTimeDay.plusHours(i1);
            return localDateTime;
        } else {
            // 计算周期的一共的天数
            int i = divide.intValue();
            int i1 = i * periodCoefficient;
            return cycleStartTimeDay.plusDays(i1 - 1);
        }
    }

    /**
     * 获取 这个药品的 当前周期的开始时间
     * @param model
     * @param timeSettingList
     * @return
     */
    public static LocalDateTime getCycleStartTimeDay(PatientDrugs model,
                                                     List<PatientDrugsTimeSetting> timeSettingList,
                                                     LocalDateTime now) {
        Integer cycleDuration = model.getCycleDuration();
        PatientDrugsTimePeriodEnum timePeriod = model.getTimePeriod();
        LocalDate startTakeMedicineDate = model.getStartTakeMedicineDate();
        // 计算 患者吃药时间 到 当前时间下。患者最近一次推送的时间。
        LocalDate cycleStartTime = null;
        switch (timePeriod) {
            case day: {
                cycleStartTime = getCycleStartTime(cycleDuration, startTakeMedicineDate, 1, now);
                return LocalDateTime.of(cycleStartTime, LocalTime.of(0,0,0,0));
            }
            case hour: {
                return getHourDayCycleStartTime(cycleDuration, startTakeMedicineDate,  now,  timeSettingList);
            }
            case moon: {
                cycleStartTime = getCycleStartTime(cycleDuration, startTakeMedicineDate, 30, now);
                return LocalDateTime.of(cycleStartTime, LocalTime.of(0,0,0,0));
            }
            case week: {
                cycleStartTime = getCycleStartTime(cycleDuration, startTakeMedicineDate, 7, now);
                return LocalDateTime.of(cycleStartTime, LocalTime.of(0,0,0,0));
            }
            default:
                return null;
        }

    }


    /**
     * 如果吃药周期是小时。 获取本次周期的开始时间
     * @param cycleDuration
     * @param startTakeMedicineDate
     * @param now
     * @param timeSettingList
     * @return
     */
    protected static LocalDateTime getHourDayCycleStartTime(Integer cycleDuration, LocalDate startTakeMedicineDate, LocalDateTime now, List<PatientDrugsTimeSetting> timeSettingList) {

        // 1小时1次。
        if (cycleDuration == 1) {
            now = now.withMinute(0).withNano(0).withSecond(0);
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
        Duration duration = Duration.between(cycleStartTime, now);
        long toHours = duration.toHours();
        if (toHours == 0) {
            cycleStartTime = now;
            return cycleStartTime;
        } else {
            // 首次吃药时间还没过
            if (toHours < 0) {
                return cycleStartTime;
            } else {
                // 首次吃药时间过了，
                long l = toHours / cycleDuration;
                return cycleStartTime.plusHours(l * cycleDuration);
            }
        }
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
     * 计算过去的哪一个周期结束的时候，把药吃完了。吃完药的日期是什么时候
     *
     * @param cycleStartTimeDay 当前周期的开始时间
     * @param cycleDuration 周期的长度
     * @param lastDose 多吃的药量
     * @param cycleNeedDose 每个周期要吃的药量
     * @param timePeriod 周期的维度
     * @return
     */
    public static LocalDate getPastLastCycleEndTime(LocalDateTime cycleStartTimeDay,
                                                    Integer cycleDuration, BigDecimal lastDose,
                                                    BigDecimal cycleNeedDose, PatientDrugsTimePeriodEnum timePeriod) {

        if (timePeriod.equals(PatientDrugsTimePeriodEnum.hour)) {
            BigDecimal divide = lastDose.divide(cycleNeedDose, 0, BigDecimal.ROUND_DOWN);
            LocalDateTime localDateTime = cycleStartTimeDay.plusHours(divide.intValue() * cycleDuration);
            return localDateTime.toLocalDate();
        } else {
            BigDecimal divide = lastDose.divide(cycleNeedDose, 0, BigDecimal.ROUND_DOWN);
            int i = 0;
            switch (timePeriod) {
                case day:
                    i = cycleDuration;
                    LocalDateTime localDateTime = cycleStartTimeDay.plusDays(-1).plusDays(divide.intValue() * i);
                    return localDateTime.toLocalDate();
                case moon:
                    i = cycleDuration * 30;
                    break;
                case week:
                    i = cycleDuration * 7;
                    break;
            }
            LocalDateTime localDateTime = cycleStartTimeDay.plusDays(-1).plusDays(divide.intValue() * i);
            return localDateTime.toLocalDate();
        }
    }


    /**
     *
     * 计算剩余的药量在哪一天吃完， 或最可能在哪天吃完药。
     * @param lastDose
     * @param cycleStartTimeDay
     * @param patientDrug
     * @return
     */
    public static LocalDate getMedicineRunOutTime(BigDecimal lastDose, LocalDateTime cycleStartTimeDay, PatientDrugs patientDrug) {

        Float dose = patientDrug.getDose();

        // 计算剩余药量还可以吃几次。四舍五入 小数点后 直接 不要。
        BigDecimal availableTimes = lastDose.divide(new BigDecimal(dose), BigDecimal.ROUND_DOWN);
        // 剩余药量不足使用一次
        if (availableTimes.intValue() == 0) {
            // 那么此时就已经药品用完了。
            // 上一个周期最后一次用药之后就是最后的用药时间

            return LocalDate.now();
        }

        // 使用 可用药次数 / 周期内用药次数
        PatientDrugsTimePeriodEnum timePeriod = patientDrug.getTimePeriod();
        // 可用的周期数
        BigDecimal cycleNumber = availableTimes.divide(new BigDecimal(patientDrug.getNumberOfDay()), BigDecimal.ROUND_DOWN);
        // 剩余药品不足一个周期。 那本周期的开始时间就是药品用完时间
        if (cycleNumber.intValue() == 0) {
            return cycleStartTimeDay.toLocalDate();
        }
//        if (cycleNumber.intValue() == 0) {
//            if (timePeriod.equals(PatientDrugsTimePeriodEnum.week) || timePeriod.equals(PatientDrugsTimePeriodEnum.moon)) {
//                // 剩余次数不足一个周期。
//                PatientDrugsTimeSetting setting = settingList.get(availableTimes.intValue() - 1);
//                // 周期内的第几天可以把药吃完。
//                Integer theCycle = setting.getDayOfTheCycle();
//                return cycleStartTimeDay.plusDays(theCycle -1).toLocalDate();
//            }
//        }
        Integer cycleDuration = patientDrug.getCycleDuration();
        switch (timePeriod) {
            case week: {
                // 周期天数
                int cycleDay = cycleNumber.intValue() * 7 * cycleDuration;
                // 这些周期吃了多少次
                int cycleUseNumber = cycleNumber.intValue() * patientDrug.getNumberOfDay();
                // 还剩下多少次在最后一个周期吃
                int i = availableTimes.intValue() - cycleUseNumber;
                LocalDateTime lastCycleStartDateTime = cycleStartTimeDay.plusDays(cycleDay);
                // 最后一个周期的开始时间就是 余药不足提醒时间
                if (i <= 0) {
                    return lastCycleStartDateTime.plusDays(-1).toLocalDate();
                }
                return lastCycleStartDateTime.toLocalDate();
            }
            case moon: {
                // 周期天数
                int cycleDay = cycleNumber.intValue() * 30 * cycleDuration;
                // 这些周期吃了多少次
                int cycleUseNumber = cycleNumber.intValue() * patientDrug.getNumberOfDay();
                // 还剩下多少次在最后一个周期吃
                int i = availableTimes.intValue() - cycleUseNumber;
                LocalDateTime lastCycleStartDateTime = cycleStartTimeDay.plusDays(cycleDay);
                if (i <= 0) {
                    return lastCycleStartDateTime.plusDays(-1).toLocalDate();
                }
                return lastCycleStartDateTime.toLocalDate();
            }
            case day: {
                // 剩余次数不足一个周期。
                if (cycleNumber.intValue() == 0) {
                    return cycleStartTimeDay.toLocalDate();
                } else {
                    // 周期天数
                    int cycleDay = cycleNumber.intValue() * cycleDuration;
                    // 这些周期吃了多少次
                    int cycleUseNumber = cycleNumber.intValue() * patientDrug.getNumberOfDay();
                    // 还剩下多少次在最后一个周期吃
                    int i = availableTimes.intValue() - cycleUseNumber;
                    // 最后一个周期的开始时间
                    LocalDateTime lastCycleStartDateTime = cycleStartTimeDay.plusDays(cycleDay);
                    if (i <= 0) {
                        return lastCycleStartDateTime.plusDays(-1).toLocalDate();
                    }
                    return lastCycleStartDateTime.toLocalDate();
                }
            }
            case hour: {
                // 剩余次数不足一个周期。
                if (cycleNumber.intValue() == 0) {
                    return cycleStartTimeDay.toLocalDate();
                    // 1小时1次用药和 N小时一次用药
//                    if (cycleDuration == 1) {
                        // 一天几次用药， 药不足一个周期吃， 那今天药就不够吃了。
//                        return LocalDate.now();
//                    } else {
                        // N小时天吃一次药。
                        // 周期内只吃一次药。 药又不够一个周期？
                        // 直接被 row 237 处逻辑拦截
//                        return LocalDate.now();
//                    }
                } else {
                    // 周期小时数
                    int cycleHours = cycleNumber.intValue() * cycleDuration;
                    // 这些周期吃了多少次
//                    int cycleUseNumber = cycleNumber.intValue() * patientDrug.getNumberOfDay();
                    // 还剩下多少次在最后一个周期吃
//                    int i = availableTimes.intValue() - cycleUseNumber;
                    // 最后一个周期的开始时间
                    LocalDateTime lastCycleStartDateTime = cycleStartTimeDay.plusHours(cycleHours);
                    return lastCycleStartDateTime.toLocalDate();
                    // 直接没有剩下的次数了。 上个周期药就吃药了。
//                    if (i <= 0) {
//                        return lastCycleStartDateTime.plusHours(-cycleDuration).toLocalDate();
//                    }
//                    return lastCycleStartDateTime.toLocalDate();
                }
            }
        }
        return null;

    }

    public static void main(String[] args) {
        BigDecimal decimal = new BigDecimal(5);
        BigDecimal divide = decimal.divide(new BigDecimal(6), BigDecimal.ROUND_DOWN);
        System.out.println(divide.intValue());
    }
}
