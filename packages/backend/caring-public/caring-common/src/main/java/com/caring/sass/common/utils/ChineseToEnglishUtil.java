package com.caring.sass.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

public class ChineseToEnglishUtil {  

  
    // 返回中文的首字母  
    public static String getPinYinHeadChar(String str) {  
  
        String convert = "";  
        for (int j = 0; j < str.length(); j++) {  
            char word = str.charAt(j);  
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
            if (pinyinArray != null) {  
                convert += pinyinArray[0].charAt(0);  
            } else {
                if (word>=65 && word<=90) {
                    convert += word;
                }
                if (word>=97 && word<=122) {
                    convert += word;
                }
            }
        }
        return convert;  
    }


    public static String getPinYinFirstLetter(String str) {
        String yinHeadChar = getPinYinHeadChar(str);
        if (StringUtils.isNotEmptyString(yinHeadChar)) {
            return yinHeadChar.substring(0,1).toUpperCase();
        }
        return "";
    }

  
    public static void main(String[] args) {  
        String s = "所念皆星河";
        for (int i = 0; i < s.length(); i++) {
            char word = s.charAt(i);
            String[] strings = PinyinHelper.toHanyuPinyinStringArray(word);
            System.out.println(strings[0]);
        }
    }
}
