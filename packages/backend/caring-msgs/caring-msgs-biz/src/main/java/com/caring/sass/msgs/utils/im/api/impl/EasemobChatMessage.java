
package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.ChatMessageAPI;
import com.caring.sass.msgs.utils.im.comm.ResponseHandler;
import io.swagger.client.api.ChatHistoryApi;
import org.springframework.stereotype.Component;

@Component
public class EasemobChatMessage implements ChatMessageAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private ChatHistoryApi api = new ChatHistoryApi();

    public EasemobChatMessage() {
    }

    @Override
    public Object exportChatMessages(Long limit, String cursor, String query) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatmessagesGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), query, limit + "", cursor);
        });
    }

    @Override
    public Object exportChatMessages(String timeStr) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatmessagesTimeGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), timeStr);
        });
    }
}
