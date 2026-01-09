package com.caring.sass;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;

import java.util.Properties;

/**
 * 简单工具测试类
 *
 * @author caring
 * @date 2019/08/06
 */
public class NoBootTest {
    @Test
    public void test() {
        Cache<String, Object> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build();

        Object val = cache.get("hello", (key) -> "延迟加载" + key);
        System.out.println(val);

        System.out.println(cache.getIfPresent("hello"));

    }

    @Test
    public void test2() {
        String data = "hello:\n" +
                "  swagger:\n" +
                "    enabled: true\n" +
                "    title: 网关模块\n" +
                "    base-package: com.caring.sass.zuul.controller\n" +
                "\n" +
                "zuul:\n" +
                "  retryable: false\n" +
                "  servlet-path: /\n" +
                "  ignored-services: \"*\"\n" +
                "  sensitive-headers:\n" +
                "  ratelimit:\n" +
                "    key-prefix: gate_rate\n" +
                "    enabled: true\n" +
                "    repository: REDIS\n" +
                "    behind-proxy: true\n" +
                "    default-policy:\n" +
                "      cycle-type: 1\n" +
                "      limit: 10\n" +
                "      refresh-interval: 60\n" +
                "      type:\n" +
                "        - APP\n" +
                "        - URL\n" +
                "  routes:\n" +
                "    authority:\n" +
                "      path: /authority/**\n" +
                "      serviceId: caring-authority-server\n" +
                "    file:\n" +
                "      path: /file/**\n" +
                "      serviceId: caring-file-server\n" +
                "    msgs:\n" +
                "      path: /msgs/**\n" +
                "      serviceId: caring-msgs-server\n" +
                "    order:\n" +
                "      path: /order/**\n" +
                "      serviceId: caring-order-server\n" +
                "    demo:\n" +
                "      path: /demo/**\n" +
                "      serviceId: caring-demo-server\n" +
                "\n" +
                "authentication:\n" +
                "  user:\n" +
                "    header-name: token\n" +
                "    pub-key: client/pub.key";

        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ByteArrayResource(data.getBytes()));
        Properties object = yamlFactory.getObject();
        System.out.println(object);
    }

    @Test
    public void testParse() {
        // 读取Json
//        String data = "{\"id\":\"chatcmpl-8iCUwcJnmJK7NU29SYbHjPCy3DQaa\",\"object\":\"chat.completion.chunk\",\"created\":1705545166,\"model\":\"gpt-3.5-turbo-0613\",\"system_fingerprint\":null,\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"\"},\"logprobs\":null,\"finish_reason\":null}]}";
        String data = "{\"id\":\"chatcmpl-8iCNoBHAsBLgObNnxmo3zXb1AqhEI\",\"object\":\"chat.completion.chunk\",\"created\":1705544724,\"model\":\"gpt-3.5-turbo-16k-0613\",\"system_fingerprint\":null,\"choices\":[{\"index\":0,\"delta\":{\"function_call\":{\"arguments\":\",\\n\"}},\"logprobs\":null,\"finish_reason\":null}]}";
        ChatCompletionResponse completionResponse = JSONUtil.toBean(data, ChatCompletionResponse.class);
        System.out.println(completionResponse);
    }

}