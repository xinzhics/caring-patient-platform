package com.caring.sass.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class SassDateUtis {

    public static Integer getAge(String birth) {
        if (StrUtil.isNotBlank(birth)) {
            try {
                // 生日格式为 2023/11/02  2023-11-02
                long year = DateUtil.parse(birth).between(new Date(), DateUnit.DAY) / 365L;
                return Convert.toInt(year);
            } catch (Exception e) {
            }
        }
        return null;
    }




    public static LocalDateTime getMessageCreateTime(String createTimeString) {
        if (StrUtil.isNotEmpty(createTimeString)) {
            DateTimeFormatter timeFormatter;
            if (createTimeString.length() == 23) {
                timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            } else {
                timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }
            return LocalDateTime.parse(createTimeString, timeFormatter);
        }
        return LocalDateTime.now();
    }



    /**
     * 获取某日期所在周的星期一（ISO 周：周一为一周开始）
     */
    private static LocalDate getMondayOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    /**
     * 计算给定日期相对于基准日期是第几周
     * 周的计算方式：从 baseDate 所在周开始算第1周
     *
     * @param targetDate 目标日期（要计算的日期）
     * @return 第几周（从1开始）
     */
    public static int getWeekNumberFromBaseDate(LocalDate targetDate) {
        // 将两个日期对齐到所在周的周一（使用 ISO 周：周一为一周开始）
        LocalDate baseMonday = getMondayOfWeek(LocalDate.of(2025,6,2));
        LocalDate targetMonday = getMondayOfWeek(targetDate);

        // 计算两个周一之间相差多少周
        long weeksBetween = ChronoUnit.WEEKS.between(baseMonday, targetMonday);

        // 加1是因为从第1周开始算
        return (int) weeksBetween + 1;
    }
}
