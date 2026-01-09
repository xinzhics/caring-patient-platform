package com.caring.sass.nursing.service.task;

import com.caring.sass.nursing.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * @ClassName DateUuils
 * @Description
 * @Author yangShuai
 * @Date 2020/10/27 15:21
 * @Version 1.0
 */
@Slf4j
public class DateUtils {
    public static String Y_M_D_HM = "yyyy-MM-dd HH:mm";
    public static String Y_M_D = "yyyy-MM-dd";
    public static String Y_M_D_ = "yyyy/MM/dd";
    public static String YMD_ = "yyyyMMdd";
    public static String YMD_point = "yyyy.MM.dd";

    public static String date2Str(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date date2str(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date addDay(Date date, int day) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(5, day);
        Date time = cale.getTime();
        return time;
    }

    public static List<Date> thisMondayAndthisSunday(int week) {
        List<Date> list = new ArrayList();
        Calendar cal1 = Calendar.getInstance(Locale.CHINA);
        int i = cal1.get(7);
        if (i == 1) {
            --week;
        }

        cal1.add(Calendar.DATE, week * 7);
        cal1.set(Calendar.DAY_OF_WEEK, 2);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date time = cal1.getTime();
        list.add(time);
        cal1.add(5, 6);
        cal1.set(11, 23);
        cal1.set(12, 59);
        cal1.set(13, 59);
        Date time1 = cal1.getTime();
        list.add(time1);
        return list;
    }


    public static Date startDayTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static Date lastDateForDay(Date date) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.set(11, 23);
        cale.set(12, 59);
        cale.set(13, 59);
        Date time = cale.getTime();
        return time;
    }

    public static Integer getWeek(Date date2str) {
        if (date2str == null) {
            throw new RuntimeException("参数异常");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date2str);
        return instance.get(Calendar.DAY_OF_WEEK);
    }

    public static String getWeekName(Date date2str) {
        Integer i = getWeek(date2str);
        String w = "";
        switch (i) {
            case 1:
                w = I18nUtils.getMessage("appointment_Sun");
                break;
            case 2:
                w = I18nUtils.getMessage("appointment_Mon");
                break;
            case 3:
                w = I18nUtils.getMessage("appointment_Tue");
                break;
            case 4:
                w = I18nUtils.getMessage("appointment_Wed");
                break;
            case 5:
                w = I18nUtils.getMessage("appointment_Thu");
                break;
            case 6:
                w = I18nUtils.getMessage("appointment_Fri");
                break;
            case 7:
                w = I18nUtils.getMessage("appointment_Sat");
                break;
        }
        return w;
    }

    public static String get7DayTitle(Date date) {
        if (date2Str(date, Y_M_D).equals(date2Str(new Date(), Y_M_D))) {
            return I18nUtils.getMessage("appointment_today");
        } else {
            return getWeekName(date);
        }
    }


    public static List<Date> get7Day(Integer week) {
        List<Date> dates = new ArrayList<>();
        Date startDay;
        if (week == null) {
            startDay = addDay(new Date(), 0);
        } else {
            startDay = addDay(new Date(), week * 7);
        }
        for (int i = 0; i < 7; i++) {
            dates.add(DateUtils.startDayTime(addDay(startDay, i)));
        }
        return dates;
    }

    public static long getDays(LocalDate endTime, LocalDate startTime) {
        return endTime.toEpochDay() - startTime.toEpochDay();
    }

}
