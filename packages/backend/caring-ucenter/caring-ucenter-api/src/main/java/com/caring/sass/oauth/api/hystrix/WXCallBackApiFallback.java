package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.user.dto.wx.message.recv.ImageMessage;
import com.caring.sass.user.dto.wx.message.recv.TextMessage;
import com.caring.sass.user.dto.wx.message.recv.VideoMessage;
import com.caring.sass.user.dto.wx.message.recv.VoiceMessage;
import org.springframework.stereotype.Component;

/**
 * @ClassName WXCallBackApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 11:20
 * @Version 1.0
 */
@Component
public class WXCallBackApiFallback implements WXCallBackApi {


    @Override
    public R<Boolean> subscribe(WxSubscribeDto wxSubscribeDto) {
        return R.timeout();
    }

    @Override
    public R<Boolean> doctorSubscribe(WxSubscribeDto query) {
        return R.timeout();
    }

    @Override
    public R<Boolean> qunfaNotification(WxSubscribeDto query) {
        return R.timeout();
    }

    @Override
    public R<Boolean> onReceiveImageMessageEvent(ImageMessage imageMessage) {
        return R.timeout();
    }

    @Override
    public R<Boolean> onReceiveTextMessageEvent(TextMessage textMessage) {
        return R.timeout();
    }

    @Override
    public R<Boolean> unsubscribe(String openId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> onReceiveVideoMessageEvent(VideoMessage videoMessage) {
        return R.timeout();
    }

    @Override
    public R<Boolean> onReceiveVoiceMessageEvent(VoiceMessage message) {
        return R.timeout();
    }

    @Override
    public R<String> wxUserExist(WxSubscribeDto query) {
        return R.timeout();
    }

    @Override
    public R<Boolean> assistantSubscribe(WxSubscribeDto subscribeDto) {
        return R.timeout();
    }

    @Override
    public void personalServiceNumberSubscribe(WxSubscribeDto subscribeDto) {
        return;
    }
}
