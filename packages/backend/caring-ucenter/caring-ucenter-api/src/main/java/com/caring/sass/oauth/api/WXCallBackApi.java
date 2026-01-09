package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.WXCallBackApiFallback;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.user.dto.wx.message.recv.ImageMessage;
import com.caring.sass.user.dto.wx.message.recv.TextMessage;
import com.caring.sass.user.dto.wx.message.recv.VideoMessage;
import com.caring.sass.user.dto.wx.message.recv.VoiceMessage;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName WXCallBackApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 16:12
 * @Version 1.0
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = WXCallBackApiFallback.class
        , path = "/wx/message/callBack", qualifier = "WXCallBackApi")
public interface WXCallBackApi {

    /**
     * @Author yangShuai
     * @Description 扫码关注。可能是患者。可能是医生误扫
     * @Date 2020/9/27 16:14
     *
     * @return com.caring.sass.base.R
     */
    @PostMapping("patient/subscribe")
    R<Boolean> subscribe(@RequestBody WxSubscribeDto wxSubscribeDto);

    /**
     * @Author yangShuai
     * @Description 医生关注
     * @Date 2020/9/27 16:14
     *
     * @return com.caring.sass.base.R
     */
    @PostMapping("doctor/subscribe")
    R<Boolean> doctorSubscribe(@RequestBody WxSubscribeDto query);

    /**
     * 群发通知的扫码或者关注事件
     * @param query
     * @return
     */
    @PostMapping("qunfa/notification")
    R<Boolean> qunfaNotification(@RequestBody WxSubscribeDto query);

    /**
     * @Author yangShuai
     * @Description 微信回调的 图片消息
     * @Date 2020/9/27 16:37
     *
     * @return com.caring.sass.base.R
     */
    @PostMapping({"onReceiveImageMessageEvent"})
    R<Boolean> onReceiveImageMessageEvent(@RequestBody ImageMessage imageMessage);


    /**
     * @Author yangShuai
     * @Description 微信回调的 文字消息
     * @Date 2020/9/27 16:37
     *
     * @return com.caring.sass.base.R
     */
    @PostMapping({"onReceiveTextMessageEvent"})
    R<Boolean> onReceiveTextMessageEvent(@RequestBody TextMessage textMessage);

    /**
     * @Author yangShuai
     * @Description 取消关注
     * @Date 2020/9/27 17:02
     * @return com.caring.sass.base.R
     */
    @GetMapping("unsubscribe")
    R<Boolean> unsubscribe(@RequestParam(value = "openId") String openId);

    /**
     * @Author yangShuai
     * @Description 视频消息
     * @Date 2020/9/27 17:02
     *
     * @return com.caring.sass.base.R
     */
    @PostMapping("onReceiveVideoMessageEvent")
    R<Boolean> onReceiveVideoMessageEvent(@RequestBody VideoMessage videoMessage);

    /**
     * @Author yangShuai
     * @Description 音频消息
     * @Date 2020/9/27 17:02
     *
     * @return com.caring.sass.base.R
     */
    @PostMapping("onReceiveVoiceMessageEvent")
    R<Boolean> onReceiveVoiceMessageEvent(@RequestBody VoiceMessage message);


    @ApiOperation("微信用户是否在系统中存在")
    @PostMapping("wxUserExist")
    R<String> wxUserExist(@RequestBody @Validated WxSubscribeDto query);


    @ApiOperation("医助扫激活码登录")
    @PostMapping("assistant/subscribe")
    R<Boolean> assistantSubscribe(@RequestBody WxSubscribeDto subscribeDto);

    @ApiOperation("个人服务号用户关注事件")
    @PostMapping("personalServiceNumber/subscribe")
    void personalServiceNumberSubscribe(@RequestBody WxSubscribeDto subscribeDto);

}
