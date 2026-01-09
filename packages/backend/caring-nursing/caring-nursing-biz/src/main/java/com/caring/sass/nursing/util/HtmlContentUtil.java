package com.caring.sass.nursing.util;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.nursing.entity.plan.PlanCmsReminderLog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.Objects;

/**
 * @ClassName HtmlContentUtil
 * @Description
 * @Author yangShuai
 * @Date 2021/12/20 15:09
 * @Version 1.0
 */
public class HtmlContentUtil {

    private static String[] titleTags = {"title", "h1", "h2"};

    public static void main(String[] args) {
        String cmslink = "https://mp.weixin.qq.com/s/jSQmnX34DebqE0UopuPesQ";
        String htmlContent = getHtmlContent(cmslink);
        PlanCmsReminderLog planCmsReminderLog = new PlanCmsReminderLog();
        getContent(htmlContent, planCmsReminderLog);
        System.out.println(planCmsReminderLog.getCmsTitle());
        System.out.println(planCmsReminderLog.getIcon());
    }

    /**
     * 请求网站。拿到网站的内容
     * @param cmsLink
     * @return
     */
    public static String getHtmlContent(String cmsLink) {
        HttpClient httpClient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
        HttpGet httpGet = new HttpGet(cmsLink);
        httpGet.setConfig(requestConfig);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 获取网站中需要的内容 标题， 缩略图
     *
     * @param html
     * @param planCmsReminderLog
     * @return
     */
    public static PlanCmsReminderLog getContent(String html, PlanCmsReminderLog planCmsReminderLog) {
        if (StringUtils.isNotEmptyString(html) && Objects.nonNull(planCmsReminderLog)) {

            Document doc;
            try {
                doc = Jsoup.parse(html);
            } catch (Exception var9) {
                return null;
            }
            String title = null;
            Elements titles;
            for (String titleTag : titleTags) {
                titles = doc.getElementsByTag(titleTag);
                if (CollUtil.isNotEmpty(titles)) {
                    for (Element e : titles) {
                        title = e.text();
                        if (StringUtils.isNotEmptyString(title)) {
                            break;
                        }
                    }
                }
                if (StringUtils.isNotEmptyString(title)) {
                    break;
                }
            }

            // 图片可能出现的问题。获取到的是图片不是缩略图。 图片期限问题。。
            Elements elements = doc.getElementsByTag("img");
            if (CollectionUtils.isEmpty(elements)) {
                elements = doc.getElementsByTag("image");
            }
            String src = null;
            if (CollUtil.isNotEmpty(elements)) {
                for (Element element : elements) {
                    src = element.attr("src");
                    if (StringUtils.isNotEmptyString(src) && !src.contains("data:image")) {
                        break;
                    }
                }
            }
            if (StringUtils.isEmpty(title)) {
                return null;
            }
            planCmsReminderLog.setCmsTitle(title);
            planCmsReminderLog.setIcon(src);
            return planCmsReminderLog;
        } else {
            return null;
        }
    }


}

