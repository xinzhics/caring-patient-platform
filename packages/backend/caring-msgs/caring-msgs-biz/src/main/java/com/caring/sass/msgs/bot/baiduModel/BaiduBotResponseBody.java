package com.caring.sass.msgs.bot.baiduModel;

import lombok.Data;

import java.util.List;

/**
 * 百度灵医的返回结果。
 * @className: BaiduBotResponseBody
 * @author: 杨帅
 * @date: 2024/2/29
 */
@Data
public class BaiduBotResponseBody {

    /**
     * 当前回答的错误信息，为空时表示正常返回
     */
    private String error_msg;

    private List<MessageBean> result;


}
