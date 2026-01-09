package com.caring.sass.nursing.dto.form;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.ApiModel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName MonitorDayType
 * @Description
 * @Author yangShuai
 * @Date 2022/2/15 11:42
 * @Version 1.0
 */
@ApiModel("监测数据折线图数据范围类型")
public enum MonitorDayType {

    /**
     * 全部
     * 患者关注日期 到 今天
     */
    All,

    /**
     * 单日
     */
    OneDay,

    /**
     * 7天
     * 今天往前推7天
     */
    Day7,

    /**
     * 30天
     * 今天往前推30填
     */
    Day30,

    /**
     * 90天
     * 今天往前推30填
     */
    Day90,


    /**
     * 自定义
     */
    customize;

    public static List<LocalDateTime> getXDate(LocalDateTime startTime,  LocalDateTime endTime) {
        LocalDateTime endDate = endTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
        Duration between = LocalDateTimeUtil.between(startTime, endTime);
        //相差的时间间隔天数为：
        long betweenDays = between.toDays();
        List<LocalDateTime> localDateTimes = new ArrayList<>(Integer.parseInt(betweenDays + ""));
        while (true) {
            if (!startTime.isAfter(endDate)) {
                localDateTimes.add(startTime);
                startTime = startTime.plusDays(1);
            } else {
                break;
            }
        }
        return localDateTimes;
    }

    /**
     * 获取趋势图的开始时间 结束时间
     * @param patientCompleteEnterGroupTime 患者的入组完成时间
     * @param monitorDayType 选择的时间范围
     * @param customizeStartDate 自定义的开始时间
     * @param customizeEndDate 自定义的结束时间
     * @return
     */
    public static List<LocalDateTime> getStartDateEndDate(
            LocalDateTime patientCompleteEnterGroupTime,
            MonitorDayType monitorDayType,
            LocalDate customizeStartDate, LocalDate customizeEndDate) {

        LocalDateTime localDateTime = LocalDateTime.now();
        List<LocalDateTime> dateTimes = new ArrayList<>();
        switch (monitorDayType) {
            case All: {
                dateTimes.add(patientCompleteEnterGroupTime);
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            case Day7: {
                LocalDateTime dateTime = localDateTime.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0);
                dateTimes.add(dateTime);
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            case Day30: {
                LocalDateTime dateTime = localDateTime.minusDays(29).withHour(0).withMinute(0).withSecond(0).withNano(0);
                dateTimes.add(dateTime);
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            case Day90: {
                LocalDateTime dateTime = localDateTime.minusDays(89).withHour(0).withMinute(0).withSecond(0).withNano(0);
                dateTimes.add(dateTime);
                dateTimes.add(localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0));
                return dateTimes;
            }
            case customize: {
                if (Objects.isNull(customizeStartDate)) {
                    throw new BizException("自定义的开始时间不能为空");
                }
                if (Objects.isNull(customizeEndDate)) {
                    throw new BizException("自定义的结束时间不能为空");
                }
                LocalDateTime dateTime = LocalDateTime.of(customizeStartDate, LocalTime.MIN);
                dateTimes.add(dateTime);
                LocalDateTime endDateTime = LocalDateTime.of(customizeEndDate, LocalTime.MAX);
                dateTimes.add(endDateTime);
                return dateTimes;
            }
            default: {
                return dateTimes;
            }
        }



    }


}
