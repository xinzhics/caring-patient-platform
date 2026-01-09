package com.caring.sass.msgs.bot;

import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.msgs.entity.OpenAiChatRequest;
import com.caring.sass.msgs.entity.OpenAiChatResponse;

/**
 * AI机器人
 *
 * @author leizhi
 */
public interface AiBot {


    /**
     * 聊天
     *
     * @param greeting 人格描述
     * @param message  咨询内容
     * @param user     用户标识
     * @return 机器人回复消息
     */
    String chat(String greeting, String message, String user);

    /**
     * 根据prompt生存图片url
     *
     * @param prompt 提示
     * @return 图片url
     */
    default String drawPic(String prompt) {
        throw new RuntimeException(BizConstant.AI_EXCEPTION);
    }

    /**
     * 最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     */
    default String chatV2(OpenAiChatRequest openAiChatRequest) {
        throw new RuntimeException(BizConstant.AI_EXCEPTION);
    }

    /**
     * 流式输出
     */
    default OpenAiChatResponse sseChat(String uid, OpenAiChatRequest openAiChatRequest) {
        throw new RuntimeException(BizConstant.AI_EXCEPTION);
    }

}
