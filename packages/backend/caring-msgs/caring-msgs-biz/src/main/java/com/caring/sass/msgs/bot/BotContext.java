package com.caring.sass.msgs.bot;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.msgs.entity.OpenAiChatRequest;
import com.caring.sass.msgs.entity.OpenAiChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 机器人容器
 *
 * @author leizhi
 */
@Slf4j
@Component
public class BotContext implements AiBot {

    /**
     * 存在此标识则遍历各机器人，直到有正确答复为止
     */
    private static final String BOT_FLAG = "All";

    /**
     * 生成图片的前缀
     */
    private static final String[] DRAW_PIC_PREFIX = {"画", "生成图片"};

    private final Map<String, AiBot> botsMap;
    private final List<AiBot> bots;

    public BotContext(Map<String, AiBot> botsMap, List<AiBot> bots) {
        this.botsMap = botsMap;
        this.bots = bots;
    }

    /**
     * 聊天
     *
     * @param greeting 人格描述
     * @param message  咨询内容
     * @param user     用户标识
     * @return 机器人回复消息
     */
    @Override
    public String chat(String greeting, String message, String user) {
        String botName = ApplicationProperties.getBotName();
        if (Objects.equals(botName, BOT_FLAG)) {
            for (AiBot bot : bots) {
                try {
                    String text = dispatch(greeting, message, user, bot);
                    if (StrUtil.isNotBlank(text)) {
                        log.info("机器人答复内容: {}", text);
                        return text;
                    }
                } catch (Exception e) {
                    log.error("机器人{}响应错误", bot.getClass());
                }
            }
            log.error("所有机器人均未作出正确响应");
            throw new RuntimeException("机器人未作出正确回复");
        } else {
            AiBot aiBot = botsMap.get(botName);
            if (Objects.isNull(aiBot)) {
                log.error("不存在{}机器人配置", botName);
                throw new RuntimeException("机器人配置错误");
            }
            String chat = dispatch(greeting, message, user, aiBot);
            log.info("机器人答复内容: {}", chat);
            return chat;
        }
    }


    /**
     * 最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     */
    @Override
    public String chatV2(OpenAiChatRequest openAiChatRequest) {
        String botName = ApplicationProperties.getBotName();
        AiBot aiBot = botsMap.get(botName);
        return aiBot.chatV2(openAiChatRequest);
    }

    /**
     * 流式输出
     */
    @Override
    public OpenAiChatResponse sseChat(String uid, OpenAiChatRequest openAiChatRequest) {
        String botName = ApplicationProperties.getBotName();
        AiBot aiBot = botsMap.get(botName);
        return aiBot.sseChat(uid, openAiChatRequest);
    }


    public String dispatch(String greeting, String message, String user, AiBot aiBot) {
        if (StrUtil.startWithAny(message, DRAW_PIC_PREFIX)) {
            return aiBot.drawPic(message);
        }
        return aiBot.chat(greeting, message, user);
    }

}
