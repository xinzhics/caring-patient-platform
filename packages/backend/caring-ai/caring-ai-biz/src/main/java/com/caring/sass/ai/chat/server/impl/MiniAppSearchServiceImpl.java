package com.caring.sass.ai.chat.server.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.assistant.SearchAssistant;
import com.caring.sass.ai.chat.dao.MiniAppUserChatHistoryGroupMapper;
import com.caring.sass.ai.chat.dao.MiniAppUserChatMapper;
import com.caring.sass.ai.chat.server.MiniAppSearchService;
import com.caring.sass.ai.config.BingSearchConfig;
import com.caring.sass.ai.entity.chat.MiniAppUserChat;
import com.caring.sass.ai.entity.chat.MiniAppUserChatHistoryGroup;
import com.caring.sass.ai.entity.dto.MiniAppUserChatSaveDTO;
import com.caring.sass.ai.entity.search.SearchResult;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @className: MiniAppSearchServiceImpl
 * @author: 杨帅
 * @date: 2024/4/9
 */
@Slf4j
@Service
public class MiniAppSearchServiceImpl  extends SuperServiceImpl<MiniAppUserChatMapper, MiniAppUserChat> implements MiniAppSearchService {

    private final CacheRepository cacheRepository;

    private final SearchAssistant searchAssistant;

    private final BingSearchConfig bingSearchConfig;

    private final MiniAppUserChatHistoryGroupMapper miniAppUserChatHistoryGroupMapper;

    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_REFERENCE = "references";
    private static final String MSG_TYPE_REFER = "refer";
    private static final String MSG_TYPE_ERROR = "error";
    private static final String MSG_TYPE_END = "end";

    public MiniAppSearchServiceImpl(CacheRepository cacheRepository, SearchAssistant searchAssistant, MiniAppUserChatHistoryGroupMapper miniAppUserChatHistoryGroupMapper, BingSearchConfig bingSearchConfig) {
        this.cacheRepository = cacheRepository;
        this.searchAssistant = searchAssistant;
        this.bingSearchConfig = bingSearchConfig;
        this.miniAppUserChatHistoryGroupMapper = miniAppUserChatHistoryGroupMapper;
    }


    /**
     * 小程序提交审核
     * @param appId
     */
    @Override
    public void submitAuditing(String appId) {
        cacheRepository.set("auditing" + appId, true);
    }


    /**
     * 小程序取消审核
     * @param appId
     */
    @Override
    public void cancelAuditing(String appId) {
        cacheRepository.del("auditing" + appId);
    }

    /**
     * 小程序是否在审核
     * @param appId
     * @return
     */
    @Override
    public Boolean auditing(String appId) {
        Object o = cacheRepository.get("auditing" + appId);
        if (o != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取会话上一次内容
     *
     * @param sessionId 会话标识
     */
    public MiniAppUserChat queryLastChatBySessionId(String sessionId) {
        return baseMapper.selectOne(Wraps.<MiniAppUserChat>lbQ()
                .eq(MiniAppUserChat::getSessionId, sessionId)
                .last(" limit 0,1 ")
                .orderByDesc(MiniAppUserChat::getCreateTime));
    }

    /**
     * 提交一次会话
     * @param miniAppUserChatSaveDTO
     * @return
     */
    public MiniAppUserChat submitChat(MiniAppUserChatSaveDTO miniAppUserChatSaveDTO) {
        Long historyId = miniAppUserChatSaveDTO.getHistoryId();
        if (Objects.isNull(historyId)) {
            // 新建一个历史对话
            MiniAppUserChatHistoryGroup historyGroup = MiniAppUserChatHistoryGroup.builder()
                    .contentTitle(miniAppUserChatSaveDTO.getContent())
                    .currentUserType(miniAppUserChatSaveDTO.getCurrentUserType())
                    .senderId(miniAppUserChatSaveDTO.getSenderId())
                    .build();
            miniAppUserChatHistoryGroupMapper.insert(historyGroup);
            miniAppUserChatSaveDTO.setHistoryId(historyGroup.getId());
        }
        MiniAppUserChat appUserChat = new MiniAppUserChat();
        BeanUtils.copyProperties(miniAppUserChatSaveDTO, appUserChat);
        baseMapper.insert(appUserChat);
        return appUserChat;
    }

    @Override
    public SseEmitter createSse(String uid) {
        // 默认2分钟超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
            try {
                String cacheValue = cacheRepository.get(MSG_TYPE_ANSWER + uid);
                if (StrUtil.isBlank(cacheValue)) {
                    return;
                }
                List<String> array = StrUtil.split(cacheValue, '#', 2);
                BaseContextHandler.setTenant(array.get(0));
                // todo 一些聊天记录等业务逻辑实现
                MiniAppUserChat chat = queryLastChatBySessionId(uid);
                if (chat == null) {
                    return;
                }
                String reference = cacheRepository.get(MSG_TYPE_REFERENCE + uid);   // 参考资料
                String refer = cacheRepository.get(MSG_TYPE_REFER + uid);   // 相关搜索

                MiniAppUserChat miniAppUserChat = MiniAppUserChat.builder()
                        .sessionId(uid)
                        .senderRoleType(MiniAppUserChat.AiRole)
                        .content(array.get(1))
                        .reference(reference)
                        .refer(refer)
                        .type(MediaType.text.toString())
                        .replyStatus(MiniAppUserChat.sendSuccess)
                        .replyMsgId(chat.getId())
                        .historyId(chat.getHistoryId())
                        .senderId(chat.getSenderId())
                        .sendStatus(MiniAppUserChat.sendSuccess)
                        .build();
                baseMapper.insert(miniAppUserChat);

                cacheRepository.del(MSG_TYPE_ANSWER + uid, MSG_TYPE_REFERENCE + uid, MSG_TYPE_REFER + uid);
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

    /**
     * 发送消息给搜索服务。
     * @param miniAppUserChatSaveDTO
     * @return
     */
    @Override
    public MiniAppUserChat sseChat(MiniAppUserChatSaveDTO miniAppUserChatSaveDTO) {

        MiniAppUserChat userChat = submitChat(miniAppUserChatSaveDTO);

        String uid = miniAppUserChatSaveDTO.getSessionId();
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }

        String query = miniAppUserChatSaveDTO.getContent();
        Map<String, String> contexts = getContexts(query);
        String queryContext = contexts.get("queryContext");
        // 相关搜索
        String refer = contexts.get("refer");
        // 参考资料
        String references = contexts.get("references");

        // 发送参考资料
        JsonArray jsonArray = new GsonBuilder().setPrettyPrinting().create().fromJson(references, JsonArray.class);
        cacheRepository.set(MSG_TYPE_REFERENCE + uid, jsonArray.toString());
        send(uid, sseEmitter, jsonArray.toString(), MSG_TYPE_REFERENCE);

        // 发送相关搜索
        jsonArray = new GsonBuilder().setPrettyPrinting().create().fromJson(refer, JsonArray.class);
        cacheRepository.set(MSG_TYPE_REFER + uid, jsonArray.toString());
        send(uid, sseEmitter, jsonArray.toString(), MSG_TYPE_REFER);

        StringBuilder answer = new StringBuilder();
        answer.append(BaseContextHandler.getTenant()).append("#");
        searchAssistant.search(queryContext, query)
                // 发送聚合的答案
                .onNext(ret -> {
                    send(uid, sseEmitter, ret, MSG_TYPE_ANSWER);
                    answer.append(ret);
                })
                .onComplete(ret -> {
                    cacheRepository.set(MSG_TYPE_ANSWER + uid, answer.toString());
                    send(uid, sseEmitter, "[DONE]", MSG_TYPE_END);
                    sseEmitter.complete();
                })
                .onError(ret -> {
                    send(uid, sseEmitter, "AI服务繁忙，请稍后再试", MSG_TYPE_ERROR);
                    send(uid, sseEmitter, "[DONE]", MSG_TYPE_END);
                    sseEmitter.complete();
                }).start();

        return userChat;
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
            queryRet.addProperty("[[^:" + i + "]]", webPage.getSnippet());
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

    public void send(String uid, SseEmitter sseEmitter, String msg, String msgType) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", msg);
            jsonObject.put("type", msgType);
            log.info("发送消息 {}", jsonObject.toJSONString());
            String encode = URLEncoder.encode(jsonObject.toJSONString(), "UTF-8");
            String replace = encode.replace("+", "%20");
            sseEmitter.send(SseEmitter.event()
                    .id(uid)
                    .data(replace)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("发送消息失败uid:[{}],msg:[{}]", uid, msg, e);
        }
    }


}
