package com.caring.sass.cms.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.cms.dao.ArticleOtherMapper;
import com.caring.sass.cms.entity.ArticleOther;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CmsRedisCallBackCenter
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 18:16
 * @Version 1.0
 */
@Slf4j
@Component
public class CmsRedisCallBackCenter {

    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("cms-redis-callback-", false);

    private final ArticleOtherMapper articleOtherMapper;

    private final RedisTemplate<String, String> redisTemplate;



    public CmsRedisCallBackCenter(ArticleOtherMapper articleOtherMapper, RedisTemplate<String, String> redisTemplate) {

        this.articleOtherMapper = articleOtherMapper;
        this.redisTemplate = redisTemplate;
        THREAD_FACTORY.newThread(this::running).start();
    }

    private void running() {
        String message = null;
        while (true) {
            try {
                message = redisTemplate.opsForList().rightPop("article_other_upload_callback", 3, TimeUnit.SECONDS);
            } catch (Exception e) {
            }
            if (StringUtils.isNotEmptyString(message)) {
                ArticleOther articleOther = JSONObject.parseObject(message, ArticleOther.class);
                String tenantCode = articleOther.getTenantCode();
                BaseContextHandler.setTenant(tenantCode);
                if ("video".equals(articleOther.getMaterialType())) {
                    if (StringUtils.isNotEmptyString(articleOther.getMediaUrl())) {
                        articleOther.setVideoUploadWx("uploadSuccess");
                    } else {
                        articleOther.setVideoUploadWx("uploadError");
                    }
                } else {
                    articleOther.setVideoUploadWx("uploadSuccess");
                }
                articleOtherMapper.updateById(articleOther);
            }
        }

    }


}
