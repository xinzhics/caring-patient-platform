package com.caring.sass.ai.entity.dto;

import io.swagger.annotations.ApiModelProperty;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * @className: DateGroupEnum
 * @author: 杨帅
 * @date: 2024/3/28
 */
public enum DateGroupEnum {

    TODAY("今天"),

    YESTERDAY("昨天"),

    THIS_WEEK("本周"),

    LAST_WEEK("上周"),

    THIS_MONTH("本月"),

    LAST_MONTH("上月"),

    TWO_MONTHS_AGO("2个月前"),

    THREE_MONTHS_AGO("3个月前"),

    SIX_MONTHS_AGO("6个月前"),

    THIS_YEAR("今年"),

    LAST_YEAR("去年"),
    ;
    @ApiModelProperty(value = "时间")
    private String desc;

    DateGroupEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public static void main(String[] args) {

        LocalDateTime now = LocalDateTime.of(LocalDate.of(2024,12,30), LocalTime.now());
        for (int i = 0; i < 800; i++) {
            LocalDateTime plusDays = now.plusDays(-i);
            String ascription = DateGroupEnum.calculateDateAscription(plusDays);
            System.out.println(plusDays.toString() + "::" + ascription);
        }

    }


    /**
     * 计算 localDateTime 属于上面哪个最近的分组
     * 如果都不属于，则返回年份
     *
     * 比如： 2024年3月28日 属于 TODAY， 那么就不再计算是否属于本周等
     *
     * @param localDateTime
     * @return
     */
    public static String calculateDateAscription(LocalDateTime localDateTime) {

        LocalDate today = LocalDate.now();
        LocalDate targetDate = localDateTime.toLocalDate();

        // 检查今天
        if (Objects.equals(today, targetDate)) {
            return TODAY.getDesc();
        }

        // 检查昨天
        if (today.plusDays(-1).equals(targetDate)) {
            return YESTERDAY.getDesc();
        }

        // 检查本周
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        // 日期等于周一或者等于周日。或者日期 是在周一和周日之间。都是本周
        if (targetDate.isEqual(monday) || targetDate.isEqual(sunday) ||
                (targetDate.isBefore(sunday) && targetDate.isAfter(monday))) {
            return THIS_WEEK.getDesc();
        }

        // 检查上周
        LocalDate lastWeekStart = today.minusWeeks(1).with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = today.minusWeeks(1).with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        if (!lastWeekStart.isAfter(targetDate) && !targetDate.isAfter(lastWeekEnd)) {
            return LAST_WEEK.getDesc();
        }

        // 检查本月
        int year = today.getYear();
        if (year == targetDate.getYear() && today.getMonth() == targetDate.getMonth()) {
            return THIS_MONTH.getDesc();
        }

        // 检查上月
        LocalDate lastMonthStart = today.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = today.minusDays(1).with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
        if (!lastMonthStart.isAfter(targetDate) && !targetDate.isAfter(lastMonthEnd)) {
            return LAST_MONTH.getDesc();
        }

        // 检查2个月前、3个月前、6个月前
        for (int i = 2; i <= 6; i++) {
            if (today.minusMonths(i).getYear() == targetDate.getYear() && today.minusMonths(i).getMonth() == targetDate.getMonth()) {
                switch (i) {
                    case 2:
                        return TWO_MONTHS_AGO.getDesc();
                    case 3:
                    case 4:
                    case 5:
                        return THREE_MONTHS_AGO.getDesc();
                    case 6:
                        return SIX_MONTHS_AGO.getDesc();
                }
            }
        }

        // 检查今年
        if (year == targetDate.getYear()) {
            return THIS_YEAR.getDesc();
        }

        // 检查去年
        year--;
        if (year == targetDate.getYear()) {
            return LAST_YEAR.getDesc();
        }

        return targetDate.getYear() + "年";
    }


}
