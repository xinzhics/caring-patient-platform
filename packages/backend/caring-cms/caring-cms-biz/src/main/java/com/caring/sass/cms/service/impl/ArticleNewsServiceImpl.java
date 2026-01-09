package com.caring.sass.cms.service.impl;

import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.ArticleNewsMapper;
import com.caring.sass.cms.entity.ArticleNews;
import com.caring.sass.cms.entity.ArticleOther;
import com.caring.sass.cms.service.ArticleNewsService;
import com.caring.sass.cms.service.ArticleOtherService;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.constant.WeiXinConstants;
import com.caring.sass.wx.dto.config.MassMessagePreview;
import com.caring.sass.wx.dto.config.UploadTemporaryMatrialForm;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.WxMpMassNews;
import me.chanjar.weixin.mp.bean.WxMpMassPreviewMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.material.WxMpNewsArticle;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ArticleNewsService
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 16:55
 * @Version 1.0
 */
@Service
@Slf4j
public class ArticleNewsServiceImpl extends SuperServiceImpl<ArticleNewsMapper, ArticleNews> implements ArticleNewsService {

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    ArticleOtherService articleOtherService;

    /**
     * 预览图文
     * @param articleNews
     * @param weiXinName
     */
    @Override
    public R previewArticleNews(ArticleNews articleNews, String weiXinName) {
        replaceContentUrl(articleNews);

        // 创建 图文的预览，并发送
        R preview = createdMassMessagePreview(articleNews, weiXinName);
        return preview;
    }

    /**
     * 处理缩略图
     *
     * 解析 html 将里面的图片链接 替换为 微信的 图文链接。
     * 视频链接，查询视频素材库替换或者，上传到视频素材库后再进行替换。
     * @param articleNews
     * @return
     */
    @Override
    public void replaceContentUrl(ArticleNews articleNews) {

        // 处理 缩略图
        ArticleNews old = null;
        if (articleNews.getId() != null) {
            old = getById(articleNews.getId());
            BeanUtils.copyProperties(old, articleNews);
        }
        // 没有旧记录，上传素材
        if (old == null) {
            materialFileUpload(articleNews);
        } else if (StringUtils.isEmpty(old.getThumbMediaId())) {
            // 旧记录没有上传
            materialFileUpload(articleNews);
        } else if (!old.getThumbMediaUrl().equals(articleNews.getThumbMediaUrl())) {
            // 更换了缩略图
            weiXinApi.deleteMedia(old.getThumbMediaId());
            materialFileUpload(articleNews);
        } else {
            // 上传过了，也没有更换，沿用之前的
            articleNews.setThumbUrl(old.getThumbUrl());
            articleNews.setThumbMediaId(old.getThumbMediaId());
        }

        String content = articleNews.getContent();
        if (StringUtils.isEmpty(content)) {
            return;
        }

        Document doc = Jsoup.parse(content);
        Elements elements = doc.getElementsByTag("img");
        String weiXinUrl;
        String timeMillis;
        String src;
        R<String> imgUpload;
        for (Element element : elements) {
            src = element.attr("src");
            ArticleOther articleOther = articleOtherService.exitArticleOther(src);
            if (Objects.nonNull(articleOther)) {
                element.attr("src", articleOther.getMediaUrl());
            } else {
                timeMillis = System.currentTimeMillis() + "";
                imgUpload = weiXinApi.mediaImgUpload(timeMillis, src);
                if (imgUpload.getIsSuccess() != null && imgUpload.getIsSuccess()) {
                    weiXinUrl = imgUpload.getData();
                    element.attr("src", weiXinUrl);
                } else {
                    throw new BizException("上传微信素材超时");
                }
            }
        }
        if (articleNews.getId() != null) {
            baseMapper.updateById(articleNews);
        }
        String html = doc.body().html();
        articleNews.setContent(html.replace("\n", ""));

    }

    /**
     * 把缩略图上传到微信的永久素材
     * @param articleNews
     */
    private void materialFileUpload(ArticleNews articleNews) {
        UploadTemporaryMatrialForm matrialForm = new UploadTemporaryMatrialForm();
        matrialForm.setUrl(articleNews.getThumbMediaUrl());
        matrialForm.setMediaType(WeiXinConstants.MediaType.image);
        matrialForm.setFileName(System.currentTimeMillis() + ".png");
        R<WxMpMaterialUploadResult> fileUpload = weiXinApi.materialFileUpload(matrialForm);
        if (fileUpload.getIsSuccess() != null && fileUpload.getIsSuccess()) {
            WxMpMaterialUploadResult uploadData = fileUpload.getData();
            String mediaId = uploadData.getMediaId();
            String url = uploadData.getUrl();
            articleNews.setThumbMediaId(mediaId);
            articleNews.setThumbUrl(url);
        } else {
            throw new BizException("上传微信素材超时");
        }
    }


    @Override
    public boolean save(ArticleNews model) {
        materialFileUpload(model);
        return super.save(model);
    }

    @Override
    public boolean updateById(ArticleNews model) {
        ArticleNews articleNews = baseMapper.selectById(model.getId());
        if (model.getThumbMediaUrl() != null &&
                !model.getThumbMediaUrl().equals(articleNews.getThumbMediaUrl())) {
            if (StringUtils.isNotEmptyString(articleNews.getThumbMediaId())) {
                weiXinApi.deleteMedia(articleNews.getThumbMediaId());
            }
            materialFileUpload(model);
        }
        return super.updateById(model);
    }

    private WxMpNewsArticle getWxMpNewsArticle(ArticleNews articleNews) {

        WxMpNewsArticle article = new WxMpNewsArticle();
        BeanUtils.copyProperties(articleNews, article);
        return article;
    }

    /**
     * 创建预览图片的表单 并发送
     * @param articleNews
     * @param weiXinName
     */
    private R createdMassMessagePreview(ArticleNews articleNews, String weiXinName) {
        MassMessagePreview preview = new MassMessagePreview();
        WxMpMassPreviewMessage mpMassPreviewMessage = new WxMpMassPreviewMessage();
        mpMassPreviewMessage.setToWxUserName(weiXinName);
        mpMassPreviewMessage.setMsgType("mpnews");
        WxMpMassNews wxMpMassNews = new WxMpMassNews();
        List<WxMpNewsArticle> articles = new ArrayList<>();
        articles.add(getWxMpNewsArticle(articleNews));
        wxMpMassNews.setArticles(articles);
        preview.setWxMpMassNews(wxMpMassNews);
        preview.setWxMpMassPreviewMessage(mpMassPreviewMessage);
        R<WxMpMassSendResult> messagePreview = weiXinApi.massMessagePreview(preview);
        if (messagePreview.getIsSuccess()) {
            return messagePreview;
        } else {
            return messagePreview;
        }
    }





}
