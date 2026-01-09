package com.caring.sass.nursing.constant;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @className: MonitorQueryDateDTO
 * @author: 杨帅
 * @date: 2023/12/27
 */
public enum MonitorQueryDateDTO {


    ALL("ALL", "全部"),
    CURRENT_DAY("CURRENT_DAY", "当天"),
    day7("day7", "7天"),
    day30("day30", "30天"),
    moon3("moon3", "3个月"),
    moon6("moon6", "6个月"),
    year("year", "年"),
    CUSTOM_DATE("CUSTOM_DATE", "自定义时间");

    /**
     * 事件的code
     */
    private String code;

    /**
     * 事件的名称
     */
    private String name;

    MonitorQueryDateDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }


    /**
     * 最小时间
     * @param monitorQueryDate
     * @return
     */
    public static LocalDateTime getMinTime(MonitorQueryDateDTO monitorQueryDate) {
        LocalDateTime localDateTime = null;
        switch (monitorQueryDate) {
            case ALL:
                break;
            case day7:
                localDateTime = LocalDateTime.of(LocalDateTime.now().plusDays(-7).toLocalDate(), LocalTime.MIN);
                break;
            case year:
                localDateTime = LocalDateTime.of(LocalDateTime.now().plusDays(-365).toLocalDate(), LocalTime.MIN);
                break;
            case day30:
                localDateTime = LocalDateTime.of(LocalDateTime.now().plusDays(-30).toLocalDate(), LocalTime.MIN);
                break;
            case moon3:
                localDateTime = LocalDateTime.of(LocalDateTime.now().plusDays(-90).toLocalDate(), LocalTime.MIN);
                break;
            case moon6:
                localDateTime = LocalDateTime.of(LocalDateTime.now().plusDays(-180).toLocalDate(), LocalTime.MIN);
                break;
            case CURRENT_DAY:
                localDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
                break;
        }
        return localDateTime;

    }

    /**
     * 最大时间
     * @param monitorQueryDate
     * @return
     */
    public static LocalDateTime getMaxTime(MonitorQueryDateDTO monitorQueryDate) {
        if (MonitorQueryDateDTO.ALL.equals(monitorQueryDate)) {
            return null;
        }
        return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX);
    }

}
