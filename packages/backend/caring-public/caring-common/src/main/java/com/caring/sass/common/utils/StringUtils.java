package com.caring.sass.common.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public StringUtils() {
    }

    public static boolean isNotEmptyString(String input) {
        return input != null && !"".equals(input.trim());
    }

    public static final boolean isEmpty(String sString) {
        return sString == null || sString.length() == 0;
    }

    public static final boolean isEmptyAfterTrim(String sString) {
        if (sString != null && sString.length() != 0) {
            return isEmpty(sString.trim());
        } else {
            return true;
        }
    }

    /**
     * 将字符串按字符分割，并转化为Long集合
     * @param string
     * @param regex
     * @return
     */
    public static List<Long> splitToList(String string, String regex) {
        List<Long> tagIdList = new ArrayList<>();
        if (StrUtil.isNotEmpty(string)) {
            String[] split = string.split(regex);
            for (String tagId : split) {
                tagIdList.add(Long.parseLong(tagId));
            }
        }
        return tagIdList;
    }

    public static String[] split2Array(String sInput, String sPattern) {
        return isEmptyAfterTrim(sInput) ? null : sInput.split(sPattern);
    }


    public static final String upperCaseTheFirstChar(String sString) {
        if (isEmpty(sString)) {
            return null;
        } else {
            return sString.length() > 1 ? sString.toUpperCase().charAt(0) + sString.substring(1) : sString.toUpperCase();
        }
    }

    public static final String lowerCaseTheFirstChar(String str) {
        if (isEmpty(str)) {
            return null;
        } else {
            return str.length() > 1 ? str.toLowerCase().charAt(0) + str.substring(1) : str.toLowerCase();
        }
    }

    public static boolean equalsTrue(String sValue) {
        if (sValue != null) {
            sValue = sValue.toLowerCase();
            if (sValue.equals("true") || sValue.equals("true") || sValue.equals("yes")) {
                return true;
            }
        }

        return false;
    }

    public static final String quote(String sValue) {
        return sValue == null ? sValue : "'" + sValue + "'";
    }

    public static String leftFillZero(String source, int length) {
        source = null == source ? "" : source.trim();
        int len = source.length();

        for (int i = 0; i < length - len; ++i) {
            source = "0" + source;
        }

        return source;
    }

    public static final String replace(String s, List<?> params) {
        return params == null ? s : replace(s, params.toArray());
    }

    public static final String replace(String s, Object[] args) {
        char[] c = s.toCharArray();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < c.length; ++i) {
            if (c[i] != '$') {
                sb.append(c[i]);
            } else {
                String sParamIdx = "";

                int j;
                for (j = i + 1; j < c.length && c[j] != ' '; ++j) {
                    sParamIdx = sParamIdx + c[j];
                }

                --j;

                try {
                    int idx = Integer.parseInt(sParamIdx);
                    if (idx > 0) {
                        sb.append(args[idx - 1].toString());
                        i = j;
                    } else {
                        sb.append(c[i]);
                    }
                } catch (Exception var9) {
                    sb.append(c[i]);
                }
            }
        }

        return sb.toString();
    }

    public static final String substringBetween(String str, String pos1, String pos2) {
        int p1 = str.indexOf(pos1);
        String sub = str.substring(p1 + pos1.length());
        int p2 = sub.indexOf(pos2);
        sub = sub.substring(0, p2);
        return sub;
    }

    public static final String defaultIfEmpty(String str, String defaultStr) {
        return isEmptyAfterTrim(str) ? defaultStr : str;
    }

    public static String replaceSymbol(String str, String replacement) {
        String s = str.replaceAll("[,|;\\s*]", replacement);
        s = s.replaceAll("[︰-ﾠ|、]", replacement);
        return s;
    }

    public static String format(String str, Map<String, Object> params) {
        if (isEmpty(str)) {
            return str;
        } else {
            String p = str;
            Iterator var3 = params.entrySet().iterator();

            while (var3.hasNext()) {
                Entry<String, Object> entry = (Entry) var3.next();
                if (str.contains("{{" + (String) entry.getKey() + "}}") && entry.getValue() instanceof String) {
                    p = p.replaceAll("\\{\\{" + (String) entry.getKey() + "\\}\\}", (String) entry.getValue());
                }
            }

            return p;
        }
    }

    public static String format(String str, String... params) {
        String result = str;
        if (params.length > 0) {
            for (int i = 0; i < params.length; ++i) {
                String value = "";
                if (params[i] != null) {
                    value = params[i];
                }

                result = result.replace("{" + i + "}", value);
            }
        }

        return result;
    }

    public static String array2String(String[] p, String spliter) {
        if (p == null) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < p.length; ++i) {
                if (i == 0) {
                    sb.append(p[i]);
                } else {
                    sb.append(spliter).append(p[i]);
                }
            }

            return sb.toString();
        }
    }

    public static boolean isValidMobile(String phone) {
        Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static int hash(String str, int max) {
        int hash = str.hashCode();
        return hash % max;
    }

    public static String formatUrlParam(Map<String, String> param) {
        String params = "";
        Map map = param;

        try {
            List<Entry<String, String>> itmes = new ArrayList(map.entrySet());
            Collections.sort(itmes, new Comparator<Entry<String, String>>() {
                public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                    return ((String) o1.getKey()).toString().compareTo((String) o2.getKey());
                }
            });
            StringBuffer sb = new StringBuffer();
            Iterator var5 = itmes.iterator();

            while (var5.hasNext()) {
                Entry<String, String> item = (Entry) var5.next();
                if (isNotEmptyString((String) item.getKey())) {
                    String key = (String) item.getKey();
                    String val = (String) item.getValue();
                    sb.append(key + "=" + val);
                    sb.append("&");
                }
            }

            params = sb.toString();
            if (!params.isEmpty()) {
                params = params.substring(0, params.length() - 1);
            }

            return params;
        } catch (Exception var9) {
            return "";
        }
    }

    public static String filterEmoji(String source) {
        if (isEmpty(source)) {
            return source;
        } else {
            StringBuilder buf = null;
            source = "_" + source;
            int len = source.length();

            for (int i = 0; i < len; ++i) {
                char codePoint = source.charAt(i);
                if (isEmojiCharacter(codePoint)) {
                    if (buf == null) {
                        buf = new StringBuilder(source.length());
                    }

                    buf.append(codePoint);
                }
            }

            if (buf == null) {
                return source.substring(1);
            } else if (buf.length() == len) {
                buf = null;
                return source.substring(1);
            } else {
                return buf.toString().substring(1);
            }
        }
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return codePoint == 0 || codePoint == '\t' || codePoint == '\n' || codePoint == '\r' || codePoint >= ' ' && codePoint <= '\ud7ff' || codePoint >= '\ue000' && codePoint <= '�' || codePoint >= 65536 && codePoint <= 1114111;
    }

    public static boolean notEmptyAndNotEqual(Object obj1, Object obj2) {
        if (obj2 == null) {
            return false;
        } else if (obj2.equals(obj1)) {
            return false;
        } else if (obj1 == null) {
            return true;
        } else {
            return !obj2.toString().equals(obj1.toString());
        }
    }

    public static Integer[] stringArray2IntArray(String[] p) {
        return (Integer[]) ((Integer[]) ConvertUtils.convert(p, Integer.class));
    }

    public static Long[] stringArray2LongArray(String[] p) {
        return (Long[]) ((Long[]) ConvertUtils.convert(p, Long.class));
    }
}
