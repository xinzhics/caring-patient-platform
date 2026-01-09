//package com.caring.sass.nursing.util;
//
//import cn.hutool.core.collection.CollUtil;
//import com.caring.sass.database.mybatis.conditions.Wraps;
//import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
//import com.caring.sass.nursing.entity.drugs.PatientDrugs;
//import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;
//
//import java.math.BigDecimal;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
///**
// * 模拟 线上各种业务场景下。推算出来的 最后一个周期的结束时间是否正确
// * 计算过去的哪一个周期结束的时候，把药吃完了。吃完药的日期是什么时候
// */
//public class PatientDrugsEndDateUtilTest {
//
//    public static void main(String[] args) {
////        testDayYuYaoBuZu();
////        testWeekYuYaoBuZu();
////        testMoonYuYaoBuZu();
//        testHourYuYaoBuZu();
//    }
//
//    public static void testHourYuYaoBuZu() {
//        // 药量
//        BigDecimal dosageCount = new BigDecimal(30);
//        // 一盒药
//        BigDecimal numberOfBoxes = new BigDecimal(1);
//        // 总药量
//        BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);
//
//        PatientDrugs patientDrug = new PatientDrugs();
//        patientDrug.setCycleDuration(1);
//        patientDrug.setNumberOfDay(1);
//        patientDrug.setDose(1.0f);
//        patientDrug.setTimePeriod(PatientDrugsTimePeriodEnum.hour);
//        patientDrug.setStartTakeMedicineDate(LocalDate.of(2022,8,26));
//        float drugsDose =  0.0f;
//        // 获取 药品已吃药量
//        // 统计patientDrugsTime 表中的已用剂量
//        // 已经吃了的总剂量
//        List<PatientDrugsTimeSetting> timeSettings = null;
//
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour) && patientDrug.getCycleDuration() > 1) {
//            // 查询他的首次吃药时间
//            timeSettings = new ArrayList<>();
//            timeSettings.add(PatientDrugsTimeSetting.builder().triggerTimeOfTheDay(LocalTime.of(8,15)).build());
//        }
//
//        // 最近的周期的开始吃药时间
//        LocalDateTime cycleStartTimeDay = PatientDrugsEndDateUtil.getCycleStartTimeDay(patientDrug, timeSettings, LocalDateTime.now());
//        System.out.println("最近的周期的开始吃药时间" + cycleStartTimeDay);
//        if (cycleStartTimeDay == null) {
//            return;
//        }
//        // 已经使用的剂量
//        BigDecimal usedDose = new BigDecimal(drugsDose);
//        // 总药量 - 已经吃的药量 = 剩余药量。
//        // 计算一个周期内吃多少药量 周期内的吃药次数*每次吃多少
//        BigDecimal cycleNeedDose;
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = needDose;
//        } else {
//            BigDecimal numberOfDay = new BigDecimal(patientDrug.getNumberOfDay());
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = numberOfDay.multiply(needDose);
//        }
//
//        // 总药量 - 已经使用的药量 = 剩余药量
//        BigDecimal lastDose = totalDosage.subtract(usedDose);
//
//        // 如果剩余药量 不足 0， 说明药吃完了。 计算购药逾期多少天了。
//        Long reminderTime= 5L;
//        LocalDate now = LocalDate.now();
//        double doubleValue = lastDose.doubleValue();
//        // 预警提条件-购药逾期 总剩余量<0 并且 超出部分大于等于一天用药量   result = -1,表示 前者小于后者  result = 0,表示 前者等于后者 result = 1,表示 前者大于后者
//        Boolean overdueWarn = false;
//        // 余药不足
//        Boolean deficiencyWarn = false;
//        // 余药不足或购药逾期的天数
//        Integer drugsAvailableDay = 0;
//        LocalDate drugsEndTime = null;
//        // 剩余药量小于0 或者 剩余药量不足使用一次
//        if (doubleValue <= 0 || lastDose.floatValue() < cycleNeedDose.floatValue()) {
//            overdueWarn = true;
//            // 还有一点药。 药还够吃 一两次 。生成购药逾期记录
//            if (doubleValue > 0 && lastDose.floatValue() > patientDrug.getDose()) {
//                drugsEndTime = cycleStartTimeDay.toLocalDate();
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            } else
//                // 没药了 。或者剩下的药不够吃一次 生成购药逾期记录
//                if (doubleValue >= 0 && lastDose.floatValue() < patientDrug.getDose()) {
//                    // 刚刚没药。
//                    if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//                        drugsEndTime = cycleStartTimeDay.plusHours(-patientDrug.getCycleDuration()).toLocalDate();
//                    } else {
//                        drugsEndTime = cycleStartTimeDay.toLocalDate().plusDays(-1);
//                    }
//                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//                } else {
//                    // 根据多吃的药量(这里这个多吃的药量是上个周期结束一共吃的药量)
//                    // 一个周期吃的药量。 和多吃的药量，算出 过去了多少个周期。 根据周期数 算出来，药品用完时间
//                    // 药品不够下一个周期吃。就放到购药预警栏目中
//                    drugsEndTime = PatientDrugsEndDateUtil.getPastLastCycleEndTime(cycleStartTimeDay, patientDrug.getCycleDuration(),
//                            lastDose, cycleNeedDose, patientDrug.getTimePeriod());
//                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//                }
//            System.out.println("生成购药逾期，药品最后用完时间"+ drugsEndTime.toString());
//            System.out.println("生成购药逾期，购药逾期时间"+ drugsAvailableDay);
//        } else {
//            // 直接算出来剩余的药量 够吃几次。
//            // 推算药品在哪一天用药后。药量不足。
//            LocalDate lastCycleEndTime = PatientDrugsEndDateUtil.getMedicineRunOutTime(lastDose, cycleStartTimeDay, patientDrug);
//            if (Objects.isNull(lastCycleEndTime)) {
//                return;
//            }
//
//            // 余药不足的提醒时间
//            // 使用用药结束时间 往前推 用药提醒天数。则为生成余药不足的日期。
//            LocalDate dateTime = lastCycleEndTime.plusDays(-reminderTime +1);
//            // 当前日期 小于 余药不足的日期， 则不需要处理。 还未到余药不足的提醒日期
//            if (now.isBefore(dateTime)) {
//                return;
//            }
//            System.out.println("生成余药不足 生成提醒的日期" + dateTime.toString());
//            deficiencyWarn = true;
//            drugsEndTime = lastCycleEndTime;
//            // 当前日期 等于 余药不足的日期， 则今天是生成余药不足预警的第一天。药品剩余天数是 提醒的天数。
//            if (now.isEqual(dateTime)) {
//                drugsAvailableDay = Integer.parseInt(reminderTime.toString());
//            }
//            // 当前日期 大于 余药不足的日期，
//            if (now.isAfter(dateTime)) {
//                // 计算今天和 dateTime.toLocalDate() 相差了几天。
//                Duration duration = Duration.between(now.atStartOfDay(), lastCycleEndTime.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + 1 + "");
//            }
//
//            if (deficiencyWarn) {
//                System.out.println("生成余药不足，剩余几天" + drugsAvailableDay);
//                System.out.println("生成余药不足 最后用药时间" + drugsEndTime);
//            }
//        }
//
//
//
//    }
//
//
//    public static void testMoonYuYaoBuZu() {
//        // 药量
//        BigDecimal dosageCount = new BigDecimal(10);
//        // 一盒药
//        BigDecimal numberOfBoxes = new BigDecimal(1);
//        // 总药量
//        BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);
//
//        PatientDrugs patientDrug = new PatientDrugs();
//        patientDrug.setCycleDuration(1);
//        patientDrug.setNumberOfDay(5);
//        patientDrug.setDose(1.0f);
//        patientDrug.setTimePeriod(PatientDrugsTimePeriodEnum.moon);
//        patientDrug.setStartTakeMedicineDate(LocalDate.of(2022,7,1));
//        float drugsDose =  5.0f;
//        // 获取 药品已吃药量
//        // 统计patientDrugsTime 表中的已用剂量
//        // 已经吃了的总剂量
//        List<PatientDrugsTimeSetting> timeSettings = null;
//
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour) && patientDrug.getCycleDuration() > 1) {
//            // 查询他的首次吃药时间
//            timeSettings = new ArrayList<>();
//            timeSettings.add(PatientDrugsTimeSetting.builder().triggerTimeOfTheDay(LocalTime.of(8,15)).build());
//        }
//
//        // 最近的周期的开始吃药时间
//        LocalDateTime cycleStartTimeDay = PatientDrugsEndDateUtil.getCycleStartTimeDay(patientDrug, timeSettings,  LocalDateTime.now());
//        System.out.println("最近的周期的开始吃药时间" + cycleStartTimeDay);
//        if (cycleStartTimeDay == null) {
//            return;
//        }
//        // 已经使用的剂量
//        BigDecimal usedDose = new BigDecimal(drugsDose);
//        // 总药量 - 已经吃的药量 = 剩余药量。
//        // 计算一个周期内吃多少药量 周期内的吃药次数*每次吃多少
//        BigDecimal cycleNeedDose;
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = needDose;
//        } else {
//            BigDecimal numberOfDay = new BigDecimal(patientDrug.getNumberOfDay());
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = numberOfDay.multiply(needDose);
//        }
//
//        // 总药量 - 已经使用的药量 = 剩余药量
//        BigDecimal lastDose = totalDosage.subtract(usedDose);
//
//        // 如果剩余药量 不足 0， 说明药吃完了。 计算购药逾期多少天了。
//        Long reminderTime= 5L;
//        LocalDate now = LocalDate.now();
//        double doubleValue = lastDose.doubleValue();
//        // 预警提条件-购药逾期 总剩余量<0 并且 超出部分大于等于一天用药量   result = -1,表示 前者小于后者  result = 0,表示 前者等于后者 result = 1,表示 前者大于后者
//        Boolean overdueWarn = false;
//        // 余药不足
//        Boolean deficiencyWarn = false;
//        // 余药不足或购药逾期的天数
//        Integer drugsAvailableDay = 0;
//        LocalDate drugsEndTime = null;
//        // 剩余药量小于0 或者 剩余药量不足使用一次
//        if (doubleValue <= 0 || lastDose.floatValue() < cycleNeedDose.floatValue()) {
//            overdueWarn = true;
//            // 还有一点药。 药还够吃 一两次 。生成购药逾期记录
//            if (doubleValue > 0 && lastDose.floatValue() > patientDrug.getDose()) {
//                drugsEndTime = cycleStartTimeDay.toLocalDate();
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            } else
//                // 没药了 。或者剩下的药不够吃一次 生成购药逾期记录
//                if (doubleValue >= 0 && lastDose.floatValue() < patientDrug.getDose()) {
//                    // 刚刚没药。
//                    if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//                        drugsEndTime = cycleStartTimeDay.plusHours(-patientDrug.getCycleDuration()).toLocalDate();
//                    } else {
//                        drugsEndTime = cycleStartTimeDay.toLocalDate().plusDays(-1);
//                    }
//                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//                } else {
//                    // 根据多吃的药量(这里这个多吃的药量是上个周期结束一共吃的药量)
//                    // 一个周期吃的药量。 和多吃的药量，算出 过去了多少个周期。 根据周期数 算出来，药品用完时间
//                    // 药品不够下一个周期吃。就放到购药预警栏目中
//                    drugsEndTime = PatientDrugsEndDateUtil.getPastLastCycleEndTime(cycleStartTimeDay, patientDrug.getCycleDuration(),
//                            lastDose, cycleNeedDose, patientDrug.getTimePeriod());
//                    Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                    drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//                }
//            System.out.println("生成购药逾期，药品最后用完时间"+ drugsEndTime.toString());
//            System.out.println("生成购药逾期，购药逾期时间"+ drugsAvailableDay);
//        } else {
//            // 直接算出来剩余的药量 够吃几次。
//            // 推算药品在哪一天用药后。药量不足。
//            LocalDate lastCycleEndTime = PatientDrugsEndDateUtil.getMedicineRunOutTime(lastDose, cycleStartTimeDay, patientDrug);
//            if (Objects.isNull(lastCycleEndTime)) {
//                return;
//            }
//
//            // 余药不足的提醒时间
//            // 使用用药结束时间 往前推 用药提醒天数。则为生成余药不足的日期。
//            LocalDate dateTime = lastCycleEndTime.plusDays(-reminderTime +1);
//            // 当前日期 小于 余药不足的日期， 则不需要处理。 还未到余药不足的提醒日期
//            if (now.isBefore(dateTime)) {
//                return;
//            }
//            System.out.println("生成余药不足 生成提醒的日期" + dateTime.toString());
//            deficiencyWarn = true;
//            drugsEndTime = lastCycleEndTime;
//            // 当前日期 等于 余药不足的日期， 则今天是生成余药不足预警的第一天。药品剩余天数是 提醒的天数。
//            if (now.isEqual(dateTime)) {
//                drugsAvailableDay = Integer.parseInt(reminderTime.toString());
//            }
//            // 当前日期 大于 余药不足的日期，
//            if (now.isAfter(dateTime)) {
//                // 计算今天和 dateTime.toLocalDate() 相差了几天。
//                Duration duration = Duration.between(now.atStartOfDay(), lastCycleEndTime.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + 1 + "");
//            }
//
//            if (deficiencyWarn) {
//                System.out.println("生成余药不足，剩余几天" + drugsAvailableDay);
//                System.out.println("生成余药不足 最后用药时间" + drugsEndTime);
//            }
//        }
//
//
//
//    }
//
//    /**
//     * 周的
//     */
//    public static void testWeekYuYaoBuZu() {
//        // 药量
//        BigDecimal dosageCount = new BigDecimal(8);
//        // 一盒药
//        BigDecimal numberOfBoxes = new BigDecimal(1);
//        // 总药量
//        BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);
//
//        PatientDrugs patientDrug = new PatientDrugs();
//        patientDrug.setCycleDuration(2);
//        patientDrug.setNumberOfDay(5);
//        patientDrug.setDose(1.0f);
//        patientDrug.setTimePeriod(PatientDrugsTimePeriodEnum.week);
//        patientDrug.setStartTakeMedicineDate(LocalDate.of(2022,8,5));
//        float drugsDose =  5.0f;
//        // 获取 药品已吃药量
//        // 统计patientDrugsTime 表中的已用剂量
//        // 已经吃了的总剂量
//        List<PatientDrugsTimeSetting> timeSettings = null;
//
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour) && patientDrug.getCycleDuration() > 1) {
//            // 查询他的首次吃药时间
//            timeSettings = new ArrayList<>();
//            timeSettings.add(PatientDrugsTimeSetting.builder().triggerTimeOfTheDay(LocalTime.of(8,15)).build());
//        }
//
//        // 最近的周期的开始吃药时间
//        LocalDateTime cycleStartTimeDay = PatientDrugsEndDateUtil.getCycleStartTimeDay(patientDrug, timeSettings,  LocalDateTime.now());
//        System.out.println("最近的周期的开始吃药时间" + cycleStartTimeDay);
//        if (cycleStartTimeDay == null) {
//            return;
//        }
//        // 已经使用的剂量
//        BigDecimal usedDose = new BigDecimal(drugsDose);
//        // 总药量 - 已经吃的药量 = 剩余药量。
//        // 计算一个周期内吃多少药量 周期内的吃药次数*每次吃多少
//        BigDecimal cycleNeedDose;
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = needDose;
//        } else {
//            BigDecimal numberOfDay = new BigDecimal(patientDrug.getNumberOfDay());
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = numberOfDay.multiply(needDose);
//        }
//
//        // 总药量 - 已经使用的药量 = 剩余药量
//        BigDecimal lastDose = totalDosage.subtract(usedDose);
//
//        // 如果剩余药量 不足 0， 说明药吃完了。 计算购药逾期多少天了。
//        Long reminderTime= 3L;
//        LocalDate now = LocalDate.now();
//        double doubleValue = lastDose.doubleValue();
//        // 预警提条件-购药逾期 总剩余量<0 并且 超出部分大于等于一天用药量   result = -1,表示 前者小于后者  result = 0,表示 前者等于后者 result = 1,表示 前者大于后者
//        Boolean overdueWarn = false;
//        // 余药不足
//        Boolean deficiencyWarn = false;
//        // 余药不足或购药逾期的天数
//        Integer drugsAvailableDay = 0;
//        LocalDate drugsEndTime = null;
//        // 剩余药量小于0 或者 剩余药量不足使用一次
//        if (doubleValue <= 0 || lastDose.floatValue() < cycleNeedDose.floatValue()) {
//            overdueWarn = true;
//            // 还有一点药。 药还够吃 一两次 。生成购药逾期记录
//            if (doubleValue > 0 && lastDose.floatValue() > patientDrug.getDose()) {
//                drugsEndTime = cycleStartTimeDay.toLocalDate();
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            } else
//            // 没药了 。或者剩下的药不够吃一次 生成购药逾期记录
//            if (doubleValue >= 0 && lastDose.floatValue() < patientDrug.getDose()) {
//                // 刚刚没药。
//                if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//                    drugsEndTime = cycleStartTimeDay.plusHours(-patientDrug.getCycleDuration()).toLocalDate();
//                } else {
//                    drugsEndTime = cycleStartTimeDay.toLocalDate().plusDays(-1);
//                }
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            } else {
//                // 根据多吃的药量(这里这个多吃的药量是上个周期结束一共吃的药量)
//                // 一个周期吃的药量。 和多吃的药量，算出 过去了多少个周期。 根据周期数 算出来，药品用完时间
//                // 药品不够下一个周期吃。就放到购药预警栏目中
//                drugsEndTime = PatientDrugsEndDateUtil.getPastLastCycleEndTime(cycleStartTimeDay, patientDrug.getCycleDuration(),
//                        lastDose, cycleNeedDose, patientDrug.getTimePeriod());
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            }
//            System.out.println("生成购药逾期，药品最后用完时间"+ drugsEndTime.toString());
//            System.out.println("生成购药逾期，购药逾期时间"+ drugsAvailableDay);
//        } else {
//            // 直接算出来剩余的药量 够吃几次。
//            // 推算药品在哪一天用药后。药量不足。
//            LocalDate lastCycleEndTime = PatientDrugsEndDateUtil.getMedicineRunOutTime(lastDose, cycleStartTimeDay, patientDrug);
//            if (Objects.isNull(lastCycleEndTime)) {
//                return;
//            }
//
//            // 余药不足的提醒时间
//            // 使用用药结束时间 往前推 用药提醒天数。则为生成余药不足的日期。
//            LocalDate dateTime = lastCycleEndTime.plusDays(-reminderTime +1);
//            // 当前日期 小于 余药不足的日期， 则不需要处理。 还未到余药不足的提醒日期
//            if (now.isBefore(dateTime)) {
//                return;
//            }
//            System.out.println("生成余药不足 生成提醒的日期" + dateTime.toString());
//            deficiencyWarn = true;
//            drugsEndTime = lastCycleEndTime;
//            // 当前日期 等于 余药不足的日期， 则今天是生成余药不足预警的第一天。药品剩余天数是 提醒的天数。
//            if (now.isEqual(dateTime)) {
//                drugsAvailableDay = Integer.parseInt(reminderTime.toString());
//            }
//            // 当前日期 大于 余药不足的日期，
//            if (now.isAfter(dateTime)) {
//                // 计算今天和 dateTime.toLocalDate() 相差了几天。
//                Duration duration = Duration.between(now.atStartOfDay(), lastCycleEndTime.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + 1 + "");
//            }
//
//            if (deficiencyWarn) {
//                System.out.println("生成余药不足，剩余几天" + drugsAvailableDay);
//                System.out.println("生成余药不足 最后用药时间" + drugsEndTime);
//            }
//        }
//
//
//
//    }
//
//
//    /**
//     * 天
//     */
//    public static void testDayYuYaoBuZu() {
//        // 药量
//        BigDecimal dosageCount = new BigDecimal(6);
//        // 一盒药
//        BigDecimal numberOfBoxes = new BigDecimal(1);
//        // 总药量
//        BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);
//
//        PatientDrugs patientDrug = new PatientDrugs();
//        patientDrug.setCycleDuration(2);
//        patientDrug.setNumberOfDay(1);
//        patientDrug.setDose(1.0f);
//        patientDrug.setTimePeriod(PatientDrugsTimePeriodEnum.day);
//        patientDrug.setStartTakeMedicineDate(LocalDate.of(2022,8,16));
//        float drugsDose = 4.0f;
//        // 获取 药品已吃药量
//        // 统计patientDrugsTime 表中的已用剂量
//        // 已经吃了的总剂量
//        List<PatientDrugsTimeSetting> timeSettings = null;
//
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour) && patientDrug.getCycleDuration() > 1) {
//            // 查询他的首次吃药时间
//            timeSettings = new ArrayList<>();
//            timeSettings.add(PatientDrugsTimeSetting.builder().triggerTimeOfTheDay(LocalTime.of(8,15)).build());
//        }
//
//        // 最近的周期的开始吃药时间
//        LocalDateTime cycleStartTimeDay = PatientDrugsEndDateUtil.getCycleStartTimeDay(patientDrug, timeSettings,  LocalDateTime.now());
//        System.out.println("最近的周期的开始吃药时间" + cycleStartTimeDay);
//        if (cycleStartTimeDay == null) {
//            return;
//        }
//        // 已经使用的剂量
//        BigDecimal usedDose = new BigDecimal(drugsDose);
//        // 总药量 - 已经吃的药量 = 剩余药量。
//        // 计算一个周期内吃多少药量 周期内的吃药次数*每次吃多少
//        BigDecimal cycleNeedDose;
//        if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = needDose;
//        } else {
//            BigDecimal numberOfDay = new BigDecimal(patientDrug.getNumberOfDay());
//            BigDecimal needDose = new BigDecimal(patientDrug.getDose());
//            // 一个周期需要用药量
//            cycleNeedDose = numberOfDay.multiply(needDose);
//        }
//
//        // 总药量 - 已经使用的药量 = 剩余药量
//        BigDecimal lastDose = totalDosage.subtract(usedDose);
//
//        // 如果剩余药量 不足 0， 说明药吃完了。 计算购药逾期多少天了。
//        Long reminderTime= 3L;
//        LocalDate now = LocalDate.now();
//        double doubleValue = lastDose.doubleValue();
//        // 预警提条件-购药逾期 总剩余量<0 并且 超出部分大于等于一天用药量   result = -1,表示 前者小于后者  result = 0,表示 前者等于后者 result = 1,表示 前者大于后者
//        Boolean overdueWarn = false;
//        // 余药不足
//        Boolean deficiencyWarn = false;
//        // 余药不足或购药逾期的天数
//        Integer drugsAvailableDay = 0;
//        LocalDate drugsEndTime = null;
//        // 剩余药量小于0 或者 剩余药量不足使用一次
//        if (doubleValue <= 0 || lastDose.floatValue() < cycleNeedDose.floatValue()) {
//            overdueWarn = true;
//            // 没药了。生成购药逾期记录
//            if (doubleValue > 0 && lastDose.floatValue() > patientDrug.getDose()) {
//                drugsEndTime = cycleStartTimeDay.toLocalDate();
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            } else
//            // 没药了。生成购药逾期记录
//            if (doubleValue >= 0 && lastDose.floatValue() < patientDrug.getDose()) {
//                // 刚刚没药。
//                if (patientDrug.getTimePeriod().equals(PatientDrugsTimePeriodEnum.hour)) {
//                    drugsEndTime = cycleStartTimeDay.plusHours(-patientDrug.getCycleDuration()).toLocalDate();
//                } else {
//                    drugsEndTime = cycleStartTimeDay.toLocalDate().plusDays(-1);
//                }
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            } else {
//                // 根据多吃的药量(这里这个多吃的药量是上个周期结束一共吃的药量)
//                // 一个周期吃的药量。 和多吃的药量，算出 过去了多少个周期。 根据周期数 算出来，药品用完时间
//                // 药品不够下一个周期吃。就放到购药预警栏目中
//                drugsEndTime = PatientDrugsEndDateUtil.getPastLastCycleEndTime(cycleStartTimeDay, patientDrug.getCycleDuration(),
//                        lastDose, cycleNeedDose, patientDrug.getTimePeriod());
//                Duration duration = Duration.between(drugsEndTime.atStartOfDay(), now.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + "");
//            }
//            System.out.println("生成购药逾期，药品最后用完时间"+ drugsEndTime.toString());
//            System.out.println("生成购药逾期，购药逾期时间"+ drugsAvailableDay);
//        } else {
//            // 直接算出来剩余的药量 够吃几次。
//            // 推算药品在哪一天用药后。药量不足。
//            LocalDate lastCycleEndTime = PatientDrugsEndDateUtil.getMedicineRunOutTime(lastDose, cycleStartTimeDay, patientDrug);
//            if (Objects.isNull(lastCycleEndTime)) {
//                return;
//            }
//            System.out.println("生成余药不足 此天吃药后药量不足" + lastCycleEndTime.toString());
//            // 余药不足的提醒时间
//            // 使用用药结束时间 往前推 用药提醒天数。则为生成余药不足的日期。
//            LocalDate dateTime = lastCycleEndTime.plusDays(-reminderTime +1);
//            // 当前日期 小于 余药不足的日期， 则不需要处理。 还未到余药不足的提醒日期
//            if (now.isBefore(dateTime)) {
//                return;
//            }
//            System.out.println("生成余药不足 生成提醒的日期" + dateTime.toString());
//            deficiencyWarn = true;
//            drugsEndTime = lastCycleEndTime;
//            // 当前日期 等于 余药不足的日期， 则今天是生成余药不足预警的第一天。药品剩余天数是 提醒的天数。
//            if (now.isEqual(dateTime)) {
//                drugsAvailableDay = Integer.parseInt(reminderTime.toString());
//            }
//            // 当前日期 大于 余药不足的日期，
//            if (now.isAfter(dateTime)) {
//                // 计算今天和 dateTime.toLocalDate() 相差了几天。
//                Duration duration = Duration.between(now.atStartOfDay(), lastCycleEndTime.atStartOfDay());
//                drugsAvailableDay = Integer.parseInt(duration.toDays() + 1 + "");
//            }
//
//            if (deficiencyWarn) {
//                System.out.println("生成余药不足，剩余几天" + drugsAvailableDay);
//                System.out.println("生成余药不足 最后用药时间" + drugsEndTime);
//            }
//        }
//
//    }
//
//
//
//}
