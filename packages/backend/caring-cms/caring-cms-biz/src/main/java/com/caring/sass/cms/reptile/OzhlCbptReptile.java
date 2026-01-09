package com.caring.sass.cms.reptile;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.cms.dao.ChannelContentMapper;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.enums.OwnerTypeEnum;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 中华临床免疫和变态反应杂志 爬虫
 * @className: ozhlCbptReptile
 * @author: 杨帅
 * @date: 2023/9/13
 */
@Component
@Slf4j
public class OzhlCbptReptile {

    @Autowired
    ChannelContentMapper channelContentMapper;

    private static String webDomain = "https://ozhl.cbpt.cnki.net/WKA2";

    private static String domain = "ozhl.cbpt.cnki.net";

    private String cookie = null;
    // 首次抓取，开始的年份
    private Integer firstReptileStartYear = 2007;
    // 首次抓取，结束的年份
    private Integer firstReptileEndYear = 2023;

    /**
     * 首次启动 获取 过去的文章
     */
    public void startFirstReptile() throws IOException, InterruptedException {
        cookie = getCookie(0);
        if (cookie == null) {
            log.error("get cookie error");
        }
        for (int a = firstReptileStartYear; a <= firstReptileEndYear; a++) {
            String url = webDomain + "/WebPublication/changeYearBrowseTopList.ashx?y=" + a;
            String html = null;
            html = getHtml(url, 0);
            Thread.sleep(3000);
            readHtmlUi(html);
        }
        System.out.println("startFirstReptile end");;

    }

    /**
     * 一天执行一次
     */
    public void getNewCms() throws IOException, InterruptedException {
        cookie = getCookie(0);
        if (cookie == null) {
            log.error("get cookie error");
            return;
        }
        String html;
        html = getHtml(webDomain + "/WebPublication/index.aspx", 0);
        readHtmlHome(html);

    }


    public String getCookie(int number) throws IOException {
        if (number >= 10) {
            return null;
        }
        number++;
        URL url = new URL(webDomain + "/WebPublication/index.aspx?mid=ozhl");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // 设置请求方式
        conn.setRequestMethod("GET");

        // 设置User-Agent，伪装成浏览器
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Sec-Ch-Ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"");
        conn.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
        conn.setRequestProperty("Sec-Ch-Ua-Platform", "\"Windows\"");
        conn.setRequestProperty("Sec-Fetch-Dest", "document");
        conn.setRequestProperty("Sec-Fetch-Mode", "navigate");
        conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
        conn.setRequestProperty("Sec-Fetch-User", "?1");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");

        try {
            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);

            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> stringList = headerFields.get("set-cookie");
            // ASP.NET_SessionId=3i1cfybrbtooq5qdnq0rsb45; path=/; HttpOnly
            // SID=202102; path=/
            StringBuilder stringBuilder = new StringBuilder();
            if (CollUtil.isNotEmpty(stringList)) {
                for (String s : stringList) {
                    if (StrUtil.isNotEmpty(s)) {
                        if (s.contains(";")) {
                            String substring = s.substring(0, s.indexOf(";"));
                            if (StrUtil.isNotEmpty(substring)) {
                                stringBuilder.append(substring).append(";");
                            }
                        }
                    }
                }
            }
            // Cookie ASP.NET_SessionId=5q05bk55xtu1bw45dexmy5ix; SID=202109
            return stringBuilder.toString();
        } catch (Exception e) {
            return getCookie(number);
        }

    }


    /**
     * 获取文章列表的html
     * @param url
     * @return
     * @throws IOException
     */
    public String getHtml(String url, int number) throws IOException {

        URL urlC = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) urlC.openConnection();

        // 设置请求方式
        conn.setRequestMethod("GET");

        // 设置User-Agent，伪装成浏览器
//        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
//        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Cookie", cookie);
//        conn.setRequestProperty("Sec-Ch-Ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"");
//        conn.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
//        conn.setRequestProperty("Sec-Ch-Ua-Platform", "\"Windows\"");
//        conn.setRequestProperty("Sec-Fetch-Dest", "document");
//        conn.setRequestProperty("Sec-Fetch-Mode", "navigate");
//        conn.setRequestProperty("Sec-Fetch-User", "?1");
//        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
//        conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
//        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");

        BufferedReader reader = null;
        number++;
        try {
            log.info("url: {}", url);
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder html = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    html.append(line);
                }
                return html.toString();
            } else {
                cookie = getCookie(0);
                if (number >= 10) {
                    log.error("url error: {}, cookie: {}", url, cookie);
                    return null;
                }
               return getHtml(url, number);
            }
        } catch (Exception e) {
            cookie = getCookie(0);
            if (number >= 10) {
                log.error("url error: {}, cookie: {}", url, cookie);
                return null;
            }
            return getHtml(url, number);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    /**
     * 读取 中华临床免疫和变态反应杂志 首页的内容
     * @param html
     */
    private void readHtmlHome(String html) throws IOException, InterruptedException {
        if (StrUtil.isEmpty(html)) {
            return;
        }
        Document document = Jsoup.parse(html);
        Elements uls = document.getElementsByClass("column_contbox_zxlist");
        Set<String> readLinks = new HashSet<>();
        if (CollUtil.isNotEmpty(uls)) {
            for (Element element : uls) {
                Elements lis = element.getElementsByTag("li");
                if (CollUtil.isNotEmpty(lis)) {
                    for (Element li : lis) {
                        Elements cmsDetailUrl = li.getElementsByTag("a");
                        if (CollUtil.isNotEmpty(cmsDetailUrl)) {
                            Element a = cmsDetailUrl.get(0);
                            String href = a.attr("href");
                            if (href.startsWith("..")) {
                                href = href.substring(2);
                            }
                            if (readLinks.contains(href)) {
                                continue;
                            }
                            readLinks.add(href);
                            getHtmlCmsDetail(href);
                        }
                    }
                }
            }
        }
        System.out.println("查询文章数量： "+ readLinks.size() );
    }

    /**
     * 获取 中华临床免疫和变态反应杂志 摘要点击排行
     * @param html
     */
    private void readHtmlUi(String html) throws IOException, InterruptedException {
        if (StrUtil.isEmpty(html)) {
            return;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("li");
        Set<String> readLinks = new HashSet<>();
        if (CollUtil.isNotEmpty(elements)) {
            for (Element element : elements) {
                Elements h3s = element.getElementsByTag("h3");
                if (CollUtil.isNotEmpty(h3s)) {
                    Element h3 = h3s.get(0);
                    Elements cmsDetailUrl = h3.getElementsByTag("a");
                    if (CollUtil.isNotEmpty(cmsDetailUrl)) {
                        Element a = cmsDetailUrl.get(0);
                        String href = a.attr("href");
                        if (href.startsWith("..")) {
                            href = href.substring(2);
                        }
                        if (readLinks.contains(href)) {
                            continue;
                        }
                        readLinks.add(href);
                        getHtmlCmsDetail(href);
                    }
                }
            }
        }

    }


    /**
     * 读取cms详情页面
     * @param url
     */
    public  void getHtmlCmsDetail(String url) throws IOException, InterruptedException {

        if (StrUtil.isEmpty(url) || StrUtil.isEmpty(cookie)) {
            return;
        }
        Thread.sleep(2000);
        String html = getHtml(webDomain + url, 0);
        readCmsDetailSave(url, html);

    }

    /**
     * 读取获取到的cmd详情页面内容。保存到数据库
     * @param url
     * @param html
     */
    private  void readCmsDetailSave(String url, String html) {
        if (StrUtil.isEmpty(html)) {
            return;
        }
        Document document = Jsoup.parse(html);
        Elements bodys = document.getElementsByTag("body");
        if (CollUtil.isNotEmpty(bodys)) {
            Element body = bodys.get(0);
            String cmsPeriod = null;
            String cmsTitle = null;
            String cmsSummary = null;
            String cmsKeyWord = null;
            String cmsAuthor = null;
            Elements header_title = body.getElementsByClass("header_title");
            if (CollUtil.isNotEmpty(header_title)) {
                Elements periods = header_title.get(0).getElementsByTag("h2");
                if (CollUtil.isNotEmpty(periods)) {
                    cmsPeriod = periods.get(0).text();
                }
            }
            System.out.println(cmsPeriod);

            Elements mains = body.getElementsByClass("main");
            if (CollUtil.isEmpty(mains)) {
                return;
            }
            Element main = mains.get(0);
            Elements titles = main.getElementsByTag("h3");
            if (CollUtil.isEmpty(titles)) {
                return;
            }
            Element title = titles.get(0);
            cmsTitle = title.text();
            System.out.println(title);
            Elements content1s = main.getElementsByClass("content1");
            if (CollUtil.isEmpty(content1s)) {
                return;
            }
            Element element = content1s.get(0);
            Elements ps = element.getElementsByTag("p");
            if (CollUtil.isEmpty(ps)) {
                return;
            }
            Element zhaiyao = ps.get(1);
            cmsSummary = zhaiyao.text();
            System.out.println(cmsSummary);
            Element keyword = ps.get(2);
            if (keyword.childNodeSize() == 3) {
                Node node = keyword.childNode(2);
                cmsKeyWord = node.toString();
            }
            Element zuozhe = ps.get(6);
            if (zuozhe.childNodeSize() == 3) {
                Node node = zuozhe.childNode(2);
                cmsAuthor = node.toString();
            }
            ChannelContent content = new ChannelContent();
            content.setTitle(cmsTitle);
            splitTitleChineseEnglish(content);
            content.setAuthor(cmsAuthor);
            content.setSummary(cmsSummary);
            content.setCmsPeriod(cmsPeriod);
            content.setKeyWord(cmsKeyWord);
            content.setLibraryId(1L);
            content.setLink(webDomain + url);
            saveCms(content);
        }

    }

    private void splitTitleChineseEnglish(ChannelContent content) {
        String title = content.getTitle();
        int charIndex = findLastChineseCharIndex(title);
        if (charIndex > 0) {
            String chineseTitle = title.substring(0, charIndex + 1);
            String englishTitle = title.substring(charIndex + 1);
            content.setTitle(chineseTitle.trim());
            content.setEnglishTitle(englishTitle.trim());
        }
    }


    public static void main(String[] args) {
        String string = new String("样受体9与系统性红斑狼疮患者的临床特点相关性");
        int chineseCharIndex = findLastChineseCharIndex(string);
        String chineseTitle = string.substring(0, chineseCharIndex + 1);
        String englishTitle = string.substring(chineseCharIndex + 1);
        System.out.println(chineseTitle);
        System.out.println(englishTitle);
    }

    public static int findLastChineseCharIndex(String str) {
        // 将字符串转换为字符数组
        char[] chars = str.toCharArray();
        // 从字符串的末尾开始遍历字符数组
        for (int i = chars.length - 1; i >= 0; i--) {
            // 判断当前字符是否为汉字
            if (Character.UnicodeBlock.of(chars[i]) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                // 如果是汉字，则返回当前下标
                return i;
            }
        }
        // 如果字符串中没有汉字，则返回-1
        return -1;
    }


    /**
     * 保存文章
     * @param content
     */
    public void saveCms(ChannelContent content) {

        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        Integer count = channelContentMapper.selectCount(Wraps.<ChannelContent>lbQ()
                .eq(ChannelContent::getChannelId, 1701850991646736384L)
                .eq(ChannelContent::getTitle, content.getTitle())
                .eq(ChannelContent::getCmsPeriod, content.getCmsPeriod()));
        if (count != null && count > 0) {
            return;
        }
        content.setOwnerType(OwnerTypeEnum.SYS);
        content.setChannelId(1701850991646736384L);
        channelContentMapper.insert(content);

        // TODO：根据文章关键字。执行相关的推送业务
    }

}
