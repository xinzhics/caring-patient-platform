package com.caring.sass.msgs.utils;

import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    static String dateToStr(String dataStr) {
        if (org.springframework.util.StringUtils.isEmpty(dataStr)) return null;
        if (dataStr.length() == 21) {
            dataStr = dataStr.substring(0, 19);
        }
        return dataStr;
    }

    static Date format(String dataStr) {
        if (org.springframework.util.StringUtils.isEmpty(dataStr)) return null;
        if (dataStr.length() == 21) {
            dataStr = dataStr.substring(0, 19);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    static int getDiffDays(Date beginDate, Date endDate) {
        if ((beginDate == null) || (endDate == null)) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }
        long diff = (endDate.getTime() - beginDate.getTime()) / 86400000L;
        int days = new Long(diff).intValue();
        return days;
    }




}
