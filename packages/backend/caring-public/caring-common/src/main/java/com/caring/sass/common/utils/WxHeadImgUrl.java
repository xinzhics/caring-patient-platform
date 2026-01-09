package com.caring.sass.common.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 微信头像工具
 * <p>
 * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
 * https://developers.weixin.qq.com/doc/offiaccount/User_Management/Get_users_basic_information_UnionID.html#UinonId
 *
 * @author xinzh
 */
public class WxHeadImgUrl {

    private static final String[] size = {"0", "46", "64", "96", "132"};

    public static String size640(String originUrl) {
        if (StrUtil.isBlank(originUrl)) {
            return originUrl;
        }
        int index = originUrl.lastIndexOf("/");
        if (index == -1) {
            return originUrl;
        }

        String str = originUrl.substring(index + 1);
        if (StrUtil.isBlank(str)) {
            return originUrl;
        }
        boolean b = ArrayUtil.contains(size, str);
        if (!b) {
            return originUrl;
        }
        StringBuilder stringBuilder = new StringBuilder(originUrl);
        stringBuilder.replace(index + 1, originUrl.length(), "0");
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        String a = "http://thirdwx.qlogo.cn/mmopen/ajNVdqHZLLDzeKmiceAPnrJAzJFiag2LQt9o1yrjzHTibpvpTrR9hKiaxkR2SRIOWg2pFTtBpqK8eXt2k6mwwtXY6g/132";
        System.out.println(size640(a));
    }
}
