package com.caring.sass.ai.face.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;


/**
 * OpenAI SSE返回监听器
 *
 * @author leizhi
 */
@Slf4j
public class CozeBotSSEEventSourceListener extends EventSourceListener {


    private Long userId;

    private Long diagramId;

    private Long diagramTypeId;

    private MegviiFusionDiagramServiceImpl loader;

    public CozeBotSSEEventSourceListener(MegviiFusionDiagramServiceImpl loader, Long userId, Long diagramId, Long diagramTypeId) {
        this.userId = userId;
        this.diagramId = diagramId;
        this.diagramTypeId = diagramTypeId;
        this.loader = loader;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("coze open sse link...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
//        log.debug("coze return：{}", data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        Object message = jsonObject.get("message");
        if (Objects.nonNull(message)) {
            JSONObject messageJSON = JSONObject.parseObject(message.toString());
            Object typeObj = messageJSON.get("type");
            if (Objects.nonNull(typeObj)) {
                if (typeObj.toString().equals("tool_response")) {
                    Object contentObj = messageJSON.get("content");
                    if (Objects.nonNull(contentObj)) {
                        // 解析 contentObj 的图片链接
                        JSONObject parsedContentObject = JSONObject.parseObject(contentObj.toString());
                        // 遍历所有的键，提取以"data"开头的键的值
                        for (String key : parsedContentObject.keySet()) {
                            if (key.startsWith("data")) {
                                String fileUrl = parsedContentObject.getString(key);
                                if (StrUtil.isEmpty(fileUrl)) {
                                    continue;
                                }
                                System.out.println(fileUrl);
                                SaasGlobalThreadPool.execute(() -> this.loader.saveCozeResult(fileUrl, userId, diagramId, diagramTypeId));
                            }
                        }

                    }
                }
            }

        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("coze closed ...");
        try {
            SaasGlobalThreadPool.execute(() -> this.loader.cozeResultClosed(diagramId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        eventSource.cancel();
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("coze  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("coze  sse连接异常data：{}，异常：{}", response, t);
        }
        this.loader.cozeError(diagramId);

        eventSource.cancel();
    }



}
