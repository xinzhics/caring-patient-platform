package com.caring.sass.ai.article.service;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.dto.ArticleSee;
import com.caring.sass.ai.dto.ArticleTaskAllDTO;
import com.caring.sass.ai.entity.article.ArticleTask;
import com.caring.sass.ai.entity.article.ConsumptionType;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * Ai创作任务
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
public interface ArticleTaskService extends SuperService<ArticleTask> {

    /**
     * 转移 AI 创作的数据 到新用户
     * @param tempUserId
     * @param toUserId
     */
    void changeCreateUser(Long tempUserId, Long toUserId);

    boolean onlyDeduct(ConsumptionType consumptionType);

    boolean onlySave(ArticleTask model);

    /**
     * 将 AI 返回的 标题 提纲 正文 保存
     * @param result
     */
    void updateAiContent(JSONObject result);


    /**
     * 请求写提纲
     * @param articleSee
     * @return
     */
    SseEmitter resetOutline(ArticleSee articleSee, ArticleTask model, boolean isRewrite);

    /**
     * 请求写正文
     * @param articleSee
     */
    SseEmitter writeContent(ArticleSee articleSee);

    /**
     * 请求AI 写标题
     * @param articleSee
     */
    SseEmitter writeTitle(ArticleSee articleSee);


    void setTaskTitle(ArticleTask task);

    void saveDraft(ArticleTask task);

    ArticleTaskAllDTO getArticleContentAll(Long taskId);


    boolean updateWordCountMatching(ArticleTask task);


    void onlyUpdate(ArticleTask task);
}
