package com.caring.sass.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验密码是否符合规范
 */
public class PasswordUtils {

    public static void main(String[] args) {
        // 测试用例
        String testString1 = "Abc12345";
        String testString2 = "abcdefg";
        String testString3 = "12345678";
        String testString4 = "Abcdefg1";

        System.out.println(isValidString(testString1)); // 应输出true
        System.out.println(isValidString(testString2)); // 应输出false（缺少数字）
        System.out.println(isValidString(testString3)); // 应输出false（缺少字母）
        System.out.println(isValidString(testString4)); // 应输出true
    }

    /**
     * 检验密码是否符合要求
     * @param input 密码
     * @return true 符合。 false 不符合
     */
    public static boolean isValidString(String input) {
        // 校验长度
        if (input == null || input.length() < 8) {
            return false;
        }

        // 定义校验正则表达式
        // [a-zA-Z] 表示任意大小写字母，\\d 表示任意数字
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d).+$";

        // 使用Pattern和Matcher进行正则匹配
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // 校验是否同时包含至少一个字母和一个数字
        return matcher.matches();
    }
}
