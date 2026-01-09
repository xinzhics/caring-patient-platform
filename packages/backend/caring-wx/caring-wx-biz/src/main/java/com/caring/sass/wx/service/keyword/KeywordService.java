package com.caring.sass.wx.service.keyword;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.dto.keyword.AutomaticReplyDto;
import com.caring.sass.wx.dto.keyword.KeyWordDto;
import com.caring.sass.wx.entity.keyword.Keyword;

/**
 * <p>
 * 业务接口
 * 微信服务号自动回复关键词
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface KeywordService extends SuperService<Keyword> {

    /**
     * @Author yangShuai
     * @Description 匹配消息内容
     * @Date 2020/9/17 11:39
     *
     * @return com.caring.sass.wx.entity.keyword.Keyword
     */
    Keyword matchKeyword(String receiveMsg);


    /**
     * @Author yangShuai
     * @Description 自动回复
     * @Date 2020/9/17 14:12
     *
     * @return com.caring.sass.wx.entity.keyword.Keyword
     */
    Keyword getAutomaticReply();

    /**
     * @Author yangShuai
     * @Description 更新自动回复
     * @Date 2020/9/17 14:46
     *
     * @return com.caring.sass.wx.entity.keyword.Keyword
     */
    Keyword updateAutomaticReply(AutomaticReplyDto automaticReply);


}
