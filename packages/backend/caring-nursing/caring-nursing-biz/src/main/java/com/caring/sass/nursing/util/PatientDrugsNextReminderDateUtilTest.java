package com.caring.sass.nursing.util;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientDrugsNextReminderDateUtilTest {

    public static void main(String[] args) {
        LocalDate startTakeMedicineDate = LocalDate.of(2022,8,18);
//        testWeek(2, startTakeMedicineDate);
//        testMoon(1, startTakeMedicineDate);
//        testDay(3, startTakeMedicineDate);
        testHour(2, startTakeMedicineDate);
    }

    /**
     * 周为周期的推送测试
     */
    public static void testWeek(Integer cycleDuration,  LocalDate startTakeMedicineDate) {
        List<PatientDrugsTimeSetting> timeSettingList = new ArrayList<>();
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(1).dayOfTheCycle(1).triggerTimeOfTheDay(LocalTime.of(11,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(2).dayOfTheCycle(2).triggerTimeOfTheDay(LocalTime.of(8,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(3).dayOfTheCycle(3).triggerTimeOfTheDay(LocalTime.of(9,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(4).dayOfTheCycle(4).triggerTimeOfTheDay(LocalTime.of(10,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(5).dayOfTheCycle(4).triggerTimeOfTheDay(LocalTime.of(11,0)).build());
        LocalDateTime dateTime = LocalDateTime.of(2022, 8, 18, 8, 0, 0);
        for (int i = 0; i < 200; ) {
            LocalDateTime localDateTime = PatientDrugsNextReminderDateUtil.weekTime(cycleDuration, startTakeMedicineDate, dateTime, timeSettingList);
            if (Objects.nonNull(localDateTime)) {
                System.out.println("今天是 "+ dateTime.toString() + "下次推送时间" + localDateTime.toString());
                dateTime = localDateTime;
            }
            i++;
        }
    }


    public static void testMoon(Integer cycleDuration, LocalDate startTakeMedicineDate) {
        List<PatientDrugsTimeSetting> timeSettingList = new ArrayList<>();
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(1).dayOfTheCycle(1).triggerTimeOfTheDay(LocalTime.of(11,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(2).dayOfTheCycle(2).triggerTimeOfTheDay(LocalTime.of(8,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(3).dayOfTheCycle(3).triggerTimeOfTheDay(LocalTime.of(9,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(4).dayOfTheCycle(4).triggerTimeOfTheDay(LocalTime.of(10,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(5).dayOfTheCycle(4).triggerTimeOfTheDay(LocalTime.of(11,0)).build());
        LocalDateTime dateTime = LocalDateTime.of(2022, 8, 18, 8, 0, 0);
        for (int i = 0; i < 200; ) {
            LocalDateTime localDateTime = PatientDrugsNextReminderDateUtil.dayTime(cycleDuration, startTakeMedicineDate, dateTime, timeSettingList);
            if (Objects.nonNull(localDateTime)) {
                System.out.println("今天是 "+ dateTime.toString() + "下次推送时间" + localDateTime.toString());
                dateTime = localDateTime;
            }
            i++;
        }
    }


    public static void testDay(Integer cycleDuration, LocalDate startTakeMedicineDate) {
        List<PatientDrugsTimeSetting> timeSettingList = new ArrayList<>();
//        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(1).triggerTimeOfTheDay(LocalTime.of(8,0)).build());
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(1).triggerTimeOfTheDay(LocalTime.of(9,0)).build());
//        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(3).triggerTimeOfTheDay(LocalTime.of(10,0)).build());
//        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(4).triggerTimeOfTheDay(LocalTime.of(11,0)).build());
//        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(5).triggerTimeOfTheDay(LocalTime.of(14,0)).build());
        LocalDateTime dateTime = LocalDateTime.of(2022, 8, 18, 8, 0, 0);
        for (int i = 0; i < 200; ) {
            LocalDateTime localDateTime = PatientDrugsNextReminderDateUtil.dayTime(cycleDuration, startTakeMedicineDate, dateTime, timeSettingList);
            if (Objects.nonNull(localDateTime)) {
                System.out.println("今天是 "+ dateTime.toString() + "下次推送时间" + localDateTime.toString());
                dateTime = localDateTime;
            }
            i++;
        }
    }



    public static void testHour(Integer cycleDuration, LocalDate startTakeMedicineDate) {
        List<PatientDrugsTimeSetting> timeSettingList = new ArrayList<>();
        timeSettingList.add(PatientDrugsTimeSetting.builder().theFirstTime(1).triggerTimeOfTheDay(LocalTime.of(9,0)).build());
        LocalDateTime dateTime = LocalDateTime.of(2022, 8, 18, 8, 0, 0);
        for (int i = 0; i < 200; ) {
            LocalDateTime localDateTime = PatientDrugsNextReminderDateUtil.hourTime(cycleDuration, startTakeMedicineDate, dateTime, timeSettingList);
            if (Objects.nonNull(localDateTime)) {
                System.out.println("今天是 "+ dateTime.toString() + "下次推送时间" + localDateTime.toString());
                dateTime = localDateTime;
            }
            i++;
        }
    }


}
