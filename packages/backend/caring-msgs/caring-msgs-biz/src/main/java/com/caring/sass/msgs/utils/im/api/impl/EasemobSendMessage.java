package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.SendMessageAPI;
import com.caring.sass.msgs.utils.im.comm.ResponseHandler;
import io.swagger.client.api.MessagesApi;
import io.swagger.client.model.Msg;
import org.springframework.stereotype.Component;

@Component
public class EasemobSendMessage implements SendMessageAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private MessagesApi api = new MessagesApi();

    public EasemobSendMessage() {
    }

    @Override
    public Object sendMessage(Msg msg) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameMessagesPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), msg);
        });
    }
}
