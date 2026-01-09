package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MonitorLineType
 * @Description
 * @Author yangShuai
 * @Date 2022/2/15 11:42
 * @Version 1.0
 */
@ApiModel("监测数据折线图数据范围类型")
public enum MonitorDateLineType {

    /**
     * 日
     */
    DAY,

    /**
     * 周
     */
    WEEK,

    /**
     * 月
     */
    MONTH;


    public static List<LocalDateTime> getStartDateEndDate(MonitorDateLineType monitorDateLineType) {

        LocalDateTime localDateTime = LocalDateTime.now();
        List<LocalDateTime> dateTimes = new ArrayList<>();
        switch (monitorDateLineType) {
            case DAY: {
                dateTimes.add(localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0));
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            case WEEK: {
                dateTimes.add(localDateTime.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0));
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            case MONTH: {
                dateTimes.add(localDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            default: {
                return dateTimes;
            }
        }
    }
}
