package com.caring.sass.msgs.bot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.bot.service.CaringOpenAiService;
import com.caring.sass.msgs.entity.OpenAiChatRequest;
import com.caring.sass.msgs.entity.OpenAiChatResponse;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.common.Usage;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * openAi机器人
 *
 * @author leizhi
 */
@Slf4j
@Order(2)
@Component
public class OpenAIBot implements AiBot {

    private final CaringOpenAiService openAiService;

    private final OpenAiStreamClient openAiStreamClient;

    private final OpenAiClient openAiClient;

    private final CacheRepository cacheRepository;


    public OpenAIBot(CaringOpenAiService openAiService, OpenAiStreamClient openAiStreamClient,
                     OpenAiClient openAiClient, CacheRepository cacheRepository) {
        this.openAiService = openAiService;
        this.openAiStreamClient = openAiStreamClient;
        this.openAiClient = openAiClient;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public String chat(String greeting, String message, String user) {
        String prompt = greeting + "\n" + "Q:" + message + "\n";
        ArrayList<String> stop = new ArrayList<>();
        stop.add(" Q:");
        stop.add(" A:");

        CompletionRequest completionRequest = CompletionRequest.builder().maxTokens(1200).model("text-davinci-003").prompt(prompt).topP(1.0D).stop(stop).user(user).build();
        log.info("发送给GPT的消息: {}", prompt);
        List<CompletionChoice> completionChoices = openAiService.createCompletion(completionRequest).getChoices();
        if (CollUtil.isEmpty(completionChoices)) {
            return "";
        }
        CompletionChoice completionChoice = completionChoices.get(0);
        log.info("发送给GPT回复的 {}", completionChoice.getText());
        String text = completionChoice.getText();
        if (text.contains("A:")) {
            text = text.replace("A:", "");
        }
        return text;
    }


    /**
     * 最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     */
    @Override
    public String chatV2(OpenAiChatRequest openAiChatRequest) {
        List<Message> messages = new ArrayList<>(2);
        messages.add(Message.builder().role(Message.Role.USER).content(openAiChatRequest.getGreeting()).build());
        messages.add(Message.builder().role(Message.Role.USER).content(openAiChatRequest.getMessage()).build());
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(messages)
                .maxTokens((4096 - TikTokensUtil.tokens(ChatCompletion.Model.GPT_3_5_TURBO.getName(), messages)))
                .user(openAiChatRequest.getUser())
                .build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
        String content = chatCompletionResponse.getChoices().get(0).getMessage().getContent();
        Usage usage = chatCompletionResponse.getUsage();
        log.info("Open AI 官方计算的请求的tokens数{},返回的tokens数{},内容{}", usage.getPromptTokens(),
                usage.getCompletionTokens(), content);
        return content;
    }

    /**
     * 流式输出
     */
    @Override
    public OpenAiChatResponse sseChat(String uid, OpenAiChatRequest openAiChatRequest) {
        String message = openAiChatRequest.getMessage();
        if (StrUtil.isBlank(message)) {
            log.info("{}参数异常，message为null", uid);
            throw new BizException("参数异常，message不能为空~");
        }

        List<Message> messages = new ArrayList<>();
        String greeting = openAiChatRequest.getGreeting();
        if (StrUtil.isNotBlank(greeting)) {
            Message greetingMsg = Message.builder().content(greeting).role(Message.Role.SYSTEM).build();
            messages.add(greetingMsg);
        }
        Message currentMessage = Message.builder().content(message).role(Message.Role.USER).build();
        messages.add(currentMessage);

        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BaseException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }
        OpenAISSEEventSourceListener openAIEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter, cacheRepository, uid, BaseContextHandler.getTenant());
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);

        OpenAiChatResponse response = new OpenAiChatResponse();
        response.setQuestionTokens(completion.tokens());
        return response;
    }

}
