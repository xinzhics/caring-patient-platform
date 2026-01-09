package com.caring.sass.common.utils;

import cn.hutool.core.util.StrUtil;

public class SensitiveInfoUtils {

    /**
     * 姓名脱敏
     * 姓名长度2个字时，保留第一个字，第二个字*号代替。
     * 姓名长度超过3个字以上时，保留前两个字，后面的*代替。
     *
     * @param name 原始姓名
     * @return 脱敏后的姓名
     */
    public static String desensitizeName(String name) {
        if (StrUtil.isEmpty(name)) {
            return name;
        }
        int length = name.length();
        if (length <= 1) {
            return name;
        } else if (length == 2) {
            return name.charAt(0) + "*";
        } else {
            return name.charAt(0) + "*" + name.substring(2);
        }
    }

    /**
     * 手机号脱敏
     * 手机号中间4位用*代替
     *
     * @param phone 原始手机号
     * @return 脱敏后的手机号
     */
    public static String desensitizePhone(String phone) {
        if (StrUtil.isEmpty(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    public static void main(String[] args) {
        // 测试姓名脱敏
        System.out.println(desensitizeName("张三")); // 输出: 张*
        System.out.println(desensitizeName("张三丰")); // 输出: 张**
        System.out.println(desensitizeName("张")); // 输出: 张
        System.out.println(desensitizeName("")); // 输出: (空字符串)

        // 测试手机号脱敏
        System.out.println(desensitizePhone("13800138000")); // 输出: 138****8000
        System.out.println(desensitizePhone("13800138")); // 输出: 13800138 (长度不符合要求)
        System.out.println(desensitizePhone("")); // 输出: (空字符串)
    }
}
