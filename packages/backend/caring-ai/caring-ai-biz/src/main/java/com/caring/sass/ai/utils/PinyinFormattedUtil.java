package com.caring.sass.ai.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinFormattedUtil {


    // 定义输出格式（全局）
    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);      // 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  // 无音调（关键！）
    }

    public static void main(String[] args) {
        System.out.println(getFormattedPinyin("王新新2"));
    }

    public static String getFormattedPinyin(String chineseName) {
        if (chineseName == null || chineseName.trim().isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        char[] chars = chineseName.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            String[] pinyinArray;
            try {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
                continue;
            }

            if (pinyinArray == null) {
                // 非汉字
                continue;
            }

            if (pinyinArray.length == 0) {
                continue;
            }

            // 取第一个读音（默认读音）
            String pinyin = pinyinArray[0]; // 已经是无音调的小写

            if (i == 0) {
                // 姓：完整拼音
                result.append(pinyin);
            } else {
                // 名：取拼音首字母
                result.append(pinyin.charAt(0));
            }
        }

        return result.toString();
    }

}
