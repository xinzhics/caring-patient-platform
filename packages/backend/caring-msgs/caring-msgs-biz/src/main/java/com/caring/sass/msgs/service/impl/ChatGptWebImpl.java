package com.caring.sass.msgs.service.impl;

import cn.hutool.http.HttpUtil;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ChatGptWebImpl {

    public String complete(String promt) {
        return HttpUtil.get("http://121.36.11.64:5000/promt?info=" + promt, 2 * 60 * 1000);
    }

    public static void testOpenAI() {

//        {
//            "model": "text-davinci-003",
//                "prompt": "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n\nQ: Hello, who are you?\n",
//                "temperature": 0.9,
//                "max_tokens": 150,
//                "top_p": 1,
//                "frequency_penalty": 0,
//                "presence_penalty": 0.6,
//                "stop": [" Human:", " AI:"]
//        }

        OpenAiService openAiService = new OpenAiService(System.getenv().getOrDefault("OPENAI_API_KEY", ""), Duration.ofSeconds((120)));
        ArrayList<String> stop = new ArrayList<>();
        stop.add(" Q:");
        stop.add(" A:");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .maxTokens(1024)
                .model("text-davinci-003")
                .prompt("he following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\nQ:"+"介绍下自己\n")
                .user("leizhi")
                .stop(stop)
                .build();
        List<CompletionChoice> completionChoices = openAiService.createCompletion(completionRequest).getChoices();
        completionChoices.forEach(x -> System.out.println(x.getText()));
    }

    public static void main(String[] args) {
        testOpenAI();
//        System.out.println(new ChatGptWebImpl().complete("你好"));
    }
}
