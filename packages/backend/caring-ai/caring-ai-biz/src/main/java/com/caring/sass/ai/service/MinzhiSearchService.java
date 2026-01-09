package com.caring.sass.ai.service;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.assistant.SearchAssistant;
import com.caring.sass.ai.config.BingSearchConfig;
import com.caring.sass.ai.dto.ChatDTO;
import com.caring.sass.ai.entity.search.SearchResult;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 敏智搜索服务
 *
 * @author leizhi
 */
@Slf4j
@Service
public class MinzhiSearchService {

    private final CacheRepository cacheRepository;

    private final SearchAssistant searchAssistant;

    private final BingSearchConfig bingSearchConfig;

    public MinzhiSearchService(CacheRepository cacheRepository, SearchAssistant searchAssistant, BingSearchConfig bingSearchConfig) {
        this.cacheRepository = cacheRepository;
        this.searchAssistant = searchAssistant;
        this.bingSearchConfig = bingSearchConfig;
    }

    public SseEmitter createSse(String uid) {
        // 默认2分钟超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
            try {
                String cacheValue = cacheRepository.get(uid);
                if (StrUtil.isBlank(cacheValue)) {
                    return;
                }
                List<String> array = StrUtil.split(cacheValue, '#', 2);
                BaseContextHandler.setTenant(array.get(0));
                // todo 一些聊天记录等业务逻辑实现
                cacheRepository.del(uid);
            } catch (Exception e) {
                log.error("保存ai回复内容异常", e);
            }
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data("")
                                .reconnectTime(3000));
                        SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }


    public void sseChat(ChatDTO chatDTO) {
        String uid = chatDTO.getUid();
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }

        String query = chatDTO.getContent();
        Map<String, String> contexts = getContexts(query);
        String queryContext = contexts.get("queryContext");
        // 相关搜索
        String refer = contexts.get("refer");
        // 参考资料
        String references = contexts.get("references");


        searchAssistant.search(queryContext, query)
                // 发送聚合的答案
                .onNext(ret -> send(uid, sseEmitter, ret))
                .onComplete(ret -> {
                    // 发送参考资料
                    send(uid, sseEmitter, references);

                    // 发送相关搜索
                    send(uid, sseEmitter, refer);
                    // 追问
                    searchAssistant.moreQuestion(queryContext, query)
                            .onNext(more -> send(uid, sseEmitter, more))
                            .onComplete(x -> {
                                send(uid, sseEmitter, "[DONE]");
                                sseEmitter.complete();
                            })
                            .onError(x -> {
                                send(uid, sseEmitter, "{\"content\":\"AI服务繁忙，请稍后再试\"}");
                                send(uid, sseEmitter, "[DONE]");
                                sseEmitter.complete();
                            }).start();
                })
                .onError(ret -> {
                    send(uid, sseEmitter, "{\"content\":\"AI服务繁忙，请稍后再试\"}");
                    send(uid, sseEmitter, "[DONE]");
                    sseEmitter.complete();
                }).start();



    }


    /**
     * 解析搜索数据
     *
     * @param query 查询语句
     * @return 搜索返回结果
     */
    public Map<String, String> getContexts(String query) {
        Map<String, String> map = new HashMap<>(3);
        SearchResult searchRet = bingSearchConfig.query(query);

        // 提示词拼接
        JsonArray queryRetContext = new JsonArray();
        // 相关搜索
        JsonArray refContext = new JsonArray();
        // 参考资料
        JsonArray references = new JsonArray();

        SearchResult.WebPages webPages = searchRet.getWebPages();
        SearchResult.RelatedSearches relatedSearches = searchRet.getRelatedSearches();
        List<SearchResult.WebPages.WebPage> webPageList = webPages.getValue();
        int size = webPageList.size();
        for (int i = 0; i < size; i++) {
            SearchResult.WebPages.WebPage webPage = webPageList.get(i);

            // 参考资料
            JsonObject reference = new JsonObject();
            reference.addProperty("url", webPage.getUrl());
            reference.addProperty("name", webPage.getName());
            references.add(reference);

            // 提示词拼接
            JsonObject queryRet = new JsonObject();
            queryRet.addProperty("[<sup>" + i + "</sup>]", webPage.getSnippet());
            queryRetContext.add(queryRet);

            // 相关搜索
            boolean hasRelated = Objects.nonNull(relatedSearches) && !relatedSearches.getValue().isEmpty();
            if (hasRelated) {
                for (SearchResult.RelatedSearches.RelatedSearch relatedSearch : relatedSearches.getValue()) {
                    JsonObject refer = new JsonObject();
                    refer.addProperty("text", relatedSearch.getText());
                    refer.addProperty("websearchUrl", relatedSearch.getWebSearchUrl());
                    refContext.add(refer);
                }
            }
        }

        String queryContextJson = new GsonBuilder().setPrettyPrinting().create().toJson(queryRetContext);
        String referJson = new GsonBuilder().setPrettyPrinting().create().toJson(refContext);
        String referenceJson = new GsonBuilder().setPrettyPrinting().create().toJson(references);
        map.put("queryContext", queryContextJson);
        map.put("refer", referJson);
        map.put("references", referenceJson);
        return map;
    }


    public void send(String uid, SseEmitter sseEmitter, String msg) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(uid)
                    .data(msg)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("发送消息失败uid:[{}],msg:[{}]", uid, msg, e);
        }
    }

}
