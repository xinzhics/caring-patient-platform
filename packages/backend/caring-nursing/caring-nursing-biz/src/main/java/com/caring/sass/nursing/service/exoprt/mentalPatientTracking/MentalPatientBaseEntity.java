package com.caring.sass.nursing.service.exoprt.mentalPatientTracking;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
public class MentalPatientBaseEntity {

    private String province;

    private String city;

    private String hospital;

    private String hospitalType;

    private String doctor;

    private String patientName;

    private String patientSex;

    private String patientAge;


    public void setPatientAge(String patientAge) {
        // 根据日期 计算年龄
        if (StrUtil.isEmpty(patientAge)) {
            return;
        }
        int age;
        if (patientAge.contains("-")) {
            age = getAge(patientAge, "yyyy-MM-dd");
        } else {
            age = getAge(patientAge, "yyyy/MM/dd");
        }
        if (age == -1) {
            return;
        }
        this.patientAge = age + "";
    }

    public static int getAge(String strBirthday, String format) {
        // 期望传入的日期格式为 yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            LocalDate birthDate = LocalDate.parse(strBirthday, formatter);
            LocalDate currentDate = LocalDate.now();

            if (birthDate.isAfter(currentDate)) {
                return -1; // 出生日期晚于今天
            }

            Period period = Period.between(birthDate, currentDate);
            return period.getYears();
        } catch (DateTimeParseException e) {
            // 格式转换失败，表示出生日期输入格式不正确
            return -1;
        }
    }

    public static void main(String[] args) {
        // 示例调用
        String birthday = "2000-01-01";
        int age = getAge(birthday, "yyyy-MM-dd");
        System.out.println("年龄: " + age);
    }

}
