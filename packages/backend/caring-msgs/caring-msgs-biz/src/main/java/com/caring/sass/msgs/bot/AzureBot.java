package com.caring.sass.msgs.bot;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.jackson.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 微软云认知服务
 *
 * @author leizhi
 */
@Order(4)
@Slf4j
@Service
public class AzureBot implements AiBot {

    private static final String END_POINT = System.getenv().getOrDefault("AZURE_OPENAI_ENDPOINT", "https://kailing.openai.azure.com");


    private static final String PATH = "/openai/deployments/gtp-35-turbo/chat/completions?api-version=2023-03-15-preview";

    private static final String API_KEY = System.getenv().getOrDefault("AZURE_OPENAI_API_KEY", "");
    private static final String ASSISTANT = "assistant";
    private static final String ROLE = "role";


    @Data
    private static class ReqParam {
        /**
         * 消息列表
         */
        private List<ReqMessage> messages;

        private float temperature = 0.7F;

        private float top_p = 0.95F;

        private int frequency_penalty = 0;

        private int presence_penalty = 0;
        private int max_tokens = 800;

        private Integer stop = null;

        private boolean stream = false;

    }

    @Data
    @AllArgsConstructor
    private static class ReqMessage {
        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }

    @Override
    public String chat(String greeting, String message, String user) {
        ReqMessage systemMsg = new ReqMessage("system", greeting);
        ReqMessage msg = new ReqMessage("user", message);
        List<ReqMessage> messages = new ArrayList<>();
        messages.add(systemMsg);
        messages.add(msg);

        ReqParam reqParam = new ReqParam();
        reqParam.setMessages(messages);
        String body = JsonUtil.toJson(reqParam);

        String url = END_POINT + PATH;
        HttpResponse response = HttpUtil.createPost(url)
                .header("api-key", API_KEY)
                .header("content-type", "application/json")
                .body(body)
                .execute();
        if (HttpStatus.HTTP_OK == response.getStatus()) {
            String r = response.body();
            JSONObject j = JSONObject.parseObject(r);
            JSONArray choices = j.getJSONArray("choices");
            if (choices.size() == 0) {
                return "";
            }
            JSONObject botMessage = choices.getJSONObject(0).getJSONObject("message");
            if (ASSISTANT.equals(botMessage.get(ROLE))) {
                return botMessage.getString("content");
            }
        }
        log.error("AzureBot request error:{}", response.body());
        return "";
    }
}
