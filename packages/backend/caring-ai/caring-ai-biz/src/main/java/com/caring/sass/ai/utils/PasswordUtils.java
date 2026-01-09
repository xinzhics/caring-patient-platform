package com.caring.sass.ai.utils;


public class PasswordUtils {

    /**
     * 验证密码是否符合规则
     * @param password 密码字符串
     * @return 符合规则返回 true，否则返回 false
     */
    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        // 正则匹配各类字符
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isWhitespace(c)) { // 排除空格
                hasSymbol = true;
            } else {
                // 包含空格或其他非法字符时直接返回 false
                return false;
            }
        }

        // 判断是否至少包含两类字符
        int categoryCount = 0;
        if (hasLetter) categoryCount++;
        if (hasDigit) categoryCount++;
        if (hasSymbol) categoryCount++;

        return categoryCount >= 2;
    }

}
