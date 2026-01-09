package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfig;
import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.ChatRoomAPI;
import com.caring.sass.msgs.utils.im.comm.EasemobAPI;
import com.caring.sass.msgs.utils.im.comm.ResponseHandler;
import io.swagger.client.ApiException;
import io.swagger.client.StringUtil;
import io.swagger.client.api.ChatRoomsApi;
import io.swagger.client.model.Chatroom;
import io.swagger.client.model.ModifyChatroom;
import io.swagger.client.model.UserNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EasemobChatRoom implements ChatRoomAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private ChatRoomsApi api = new ChatRoomsApi();

    public EasemobChatRoom() {
    }

    @Override
    public Object createChatRoom(final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), (Chatroom)payload);
            }
        });
    }

    @Override
    public Object modifyChatRoom(final String roomId, final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdPut(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId, (ModifyChatroom)payload);
            }
        });
    }

    @Override
    public Object deleteChatRoom(final String roomId) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId);
            }
        });
    }

    @Override
    public Object getAllChatRooms() {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken());
            }
        });
    }

    @Override
    public Object getChatRoomDetail(final String roomId) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId);
            }
        });
    }

    @Override
    public Object addSingleUserToChatRoom(final String roomId, final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdUsersUsernamePost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId, userName);
            }
        });
    }

    @Override
    public Object addBatchUsersToChatRoom(final String roomId, final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId, (UserNames)payload);
            }
        });
    }

    @Override
    public Object removeSingleUserFromChatRoom(final String roomId, final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdUsersUsernameDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId, userName);
            }
        });
    }

    @Override
    public Object removeBatchUsersFromChatRoom(final String roomId, final String[] userNames) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobChatRoom.this.api.orgNameAppNameChatroomsChatroomIdUsersUsernamesDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), roomId, StringUtil.join(userNames, ","));
            }
        });
    }
}
