package com.caring.sass.cms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.constant.MediaTypeEnum;
import com.caring.sass.cms.dao.ArticleOtherMapper;
import com.caring.sass.cms.entity.ArticleOther;
import com.caring.sass.cms.service.ArticleOtherService;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.constant.WeiXinConstants;
import com.caring.sass.wx.dto.config.UploadTemporaryMatrialForm;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ArticleOtherServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 16:55
 * @Version 1.0
 */
@Service
@Slf4j
public class ArticleOtherServiceImpl extends SuperServiceImpl<ArticleOtherMapper, ArticleOther> implements ArticleOtherService {

    @Autowired
    WeiXinApi weiXinApi;


    @Override
    public boolean save(ArticleOther model) {

        super.save(model);
        String tenant = BaseContextHandler.getTenant();
        SaasGlobalThreadPool.execute(() -> saveOrUpdateArticle(tenant ,model));
        return true;
    }

    /**
     * 更新微信素材库。然后将素材id 素材链接 保存到本地
     * @param model
     * @return
     */
    private void saveOrUpdateArticle(String tenant, ArticleOther model) {
        BaseContextHandler.setTenant(tenant);
        String mediaSaasUrl = model.getMediaSaasUrl();
        String fileName = model.getFileName();
        String materialType = model.getMaterialType();
        UploadTemporaryMatrialForm matrialForm = new UploadTemporaryMatrialForm();
        matrialForm.setMediaType(getMediaType(materialType));
        matrialForm.setFileName(fileName);
        matrialForm.setUrl(mediaSaasUrl);
        matrialForm.setVideoTitle(model.getTitle());
        matrialForm.setVideoIntroduction(model.getIntroduction());
        model.setTenantCode(BaseContextHandler.getTenant());
        if (!MediaTypeEnum.image.toString().equals(model.getMaterialType())) {
            matrialForm.setAsynchronous(true);
            matrialForm.setRedisCallBackKey("article_other_upload_callback");
            matrialForm.setRedisCallBackValue(JSON.toJSONString(model));
            model.setVideoUploadWx("uploading");
            super.updateById(model);
        }
        R<WxMpMaterialUploadResult> materialUploadResult = weiXinApi.materialFileUpload(matrialForm);
        if (materialUploadResult.getIsSuccess() != null && materialUploadResult.getIsSuccess()) {
            WxMpMaterialUploadResult data = materialUploadResult.getData();
            if (Objects.nonNull(data)) {
                String mediaId = data.getMediaId();
                String dataUrl = data.getUrl();
                if (StringUtils.isNotEmptyString(mediaId)) {
                    model.setMediaId(mediaId);
                }
                if (StringUtils.isNotEmptyString(dataUrl)) {
                    model.setMediaUrl(dataUrl);
                }
                super.updateById(model);
            }
        } else {
            throw new BizException(materialUploadResult.getMsg());
        }
    }

    @Override
    public boolean updateById(ArticleOther model) {
        ArticleOther articleOther = getById(model.getId());
        super.updateById(model);
        if (!articleOther.getMediaSaasUrl().equals(model.getMediaSaasUrl())) {
            // 素材被修改了。 需要更新微信素材库
            weiXinApi.deleteMedia(articleOther.getMediaId());
            // 只能删除。 然后再次上传
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> saveOrUpdateArticle(tenant ,model));
        }
        return true;
    }

    private WeiXinConstants.MediaType getMediaType(String materialType) {
        if (WeiXinConstants.MediaType.image.toString().equals(materialType)) {
            return WeiXinConstants.MediaType.image;
        } else if (WeiXinConstants.MediaType.video.toString().equals(materialType)) {
            return WeiXinConstants.MediaType.video;
        } else if (WeiXinConstants.MediaType.voice.toString().equals(materialType)) {
            return WeiXinConstants.MediaType.voice;
        } else if (WeiXinConstants.MediaType.thumb.toString().equals(materialType)) {
            return WeiXinConstants.MediaType.thumb;
        }
        throw new BizException("素材类型不存在");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {

        ArticleOther articleOther = getById(id);
        if (StrUtil.isNotBlank(articleOther.getMediaId())) {
            try {
                weiXinApi.deleteMedia(articleOther.getMediaId());
            } catch (Exception e) {
                log.error("delete wx mediaId error id {} {}", id, e.getMessage());
            }
        }
        super.removeById(id);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            this.removeById(serializable);
        }
        return true;
    }

    /**
     * 判断链接地址是否已经在素材库。
     * @param src
     * @return
     */
    @Override
    public ArticleOther exitArticleOther(String src) {
        LbqWrapper<ArticleOther> lbqWrapper = Wraps.<ArticleOther>lbQ().eq(ArticleOther::getMediaSaasUrl, src);
        List<ArticleOther> articleOthers = baseMapper.selectList(lbqWrapper);
        if (CollUtil.isNotEmpty(articleOthers)) {
            return articleOthers.get(0);
        }
        return null;
    }
}
