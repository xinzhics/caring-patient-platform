package com.caring.sass.msgs.bot;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.msgs.bot.service.CaringOpenAiService;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gpt机器人
 *
 * @author leizhi
 */
@Slf4j
@Order(3)
@Component
public class GptBot implements AiBot {

    private final CaringOpenAiService openAiService;

    public GptBot(CaringOpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String chat(String greeting, String message, String user) {
        log.info("发送给GPT的消息: {}", message);
        final List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), greeting));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), message));
        ChatCompletionRequest request = ChatCompletionRequest.builder().model("gpt-3.5-turbo").messages(messages).build();
        ChatCompletionResult ret = openAiService.createChatCompletion(request);
        List<ChatCompletionChoice> completionChoices = ret.getChoices();
        if (CollUtil.isEmpty(completionChoices)) {
            return "";
        }
        return completionChoices.get(0).getMessage().getContent();
    }

    @Override
    public String drawPic(String prompt) {
        CreateImageRequest build = CreateImageRequest
                .builder()
                .prompt(prompt)
                .build();
        ImageResult imageResult = openAiService.createImage(build);
        if (Objects.isNull(imageResult) || CollUtil.isEmpty(imageResult.getData())) {
            throw new RuntimeException("AI画图失败");
        }
        return imageResult.getData().get(0).getUrl();
    }
}

