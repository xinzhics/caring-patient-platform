package com.caring.sass.ai.article;

import cn.hutool.http.HttpUtil;
import com.caring.sass.common.constant.ApplicationDomainUtil;

public class SaasCmsUtils {


    /**
     * 通知saas的cms。删除资源的引用
     * @param dataId
     * @param cmsType
     */
    public static void deleteSaasStudioCms(String dataId, String cmsType) {
        String string = ApplicationDomainUtil.apiUrl() + "/api/cms/studioCms/anno/deleteArticleData/{articleDataId}";
        string = string.replace("{articleDataId}", dataId);
        string += "?authToken=b8774901-7273-44d2-a10c-29c0aafef869&studioCmsType=" + cmsType;
        try {
            HttpUtil.createGet(string).execute();
        } catch (Exception e) {
        }

    }


}
