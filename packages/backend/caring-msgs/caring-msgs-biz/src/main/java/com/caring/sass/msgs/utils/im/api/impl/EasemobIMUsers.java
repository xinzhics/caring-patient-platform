package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.IMUserAPI;
import com.caring.sass.msgs.utils.im.comm.EasemobAPI;
import com.caring.sass.msgs.utils.im.comm.ResponseHandler;
import io.swagger.client.ApiException;
import io.swagger.client.api.UsersApi;
import io.swagger.client.model.NewPassword;
import io.swagger.client.model.Nickname;
import io.swagger.client.model.RegisterUsers;
import io.swagger.client.model.UserNames;
import org.springframework.stereotype.Component;

@Component
public class EasemobIMUsers implements IMUserAPI {
    private UsersApi api = new UsersApi();
    private ResponseHandler responseHandler = new ResponseHandler();

    public EasemobIMUsers() {
    }

    @Override
    public Object createNewIMUserSingle(RegisterUsers payload) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), payload, IMConfigWrapper.getAccessToken());
        });
    }

    @Override
    public Object createNewIMUserBatch(final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), (RegisterUsers)payload, IMConfigWrapper.getAccessToken());
            }
        });
    }

    @Override
    public Object getIMUserByUserName(final String userName) {
        Object handle = this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
        return handle;
    }

    @Override
    public Object getIMUsersBatch(final Long limit, final String cursor) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), limit + "", cursor);
            }
        });
    }

    @Override
    public Object deleteIMUserByUserName(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object deleteIMUserBatch(final Long limit, final String cursor) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), limit + "", cursor);
            }
        });
    }

    @Override
    public Object modifyIMUserPasswordWithAdminToken(final String userName, final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernamePasswordPut(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), userName, (NewPassword)payload, IMConfigWrapper.getAccessToken());
            }
        });
    }

    @Override
    public Object modifyIMUserNickNameWithAdminToken(final String userName, final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernamePut(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), userName, (Nickname)payload, IMConfigWrapper.getAccessToken());
            }
        });
    }

    @Override
    public Object addFriendSingle(final String userName, final String friendName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameContactsUsersFriendUsernamePost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName, friendName);
            }
        });
    }

    @Override
    public Object deleteFriendSingle(final String userName, final String friendName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameContactsUsersFriendUsernameDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName, friendName);
            }
        });
    }

    @Override
    public Object getFriends(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameContactsUsersGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object getBlackList(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameBlocksUsersGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object addToBlackList(final String userName, final Object payload) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameBlocksUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName, (UserNames)payload);
            }
        });
    }

    @Override
    public Object removeFromBlackList(final String userName, final String blackListName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameBlocksUsersBlockedUsernameDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName, blackListName);
            }
        });
    }

    @Override
    public Object getIMUserStatus(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameStatusGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object getOfflineMsgCount(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersOwnerUsernameOfflineMsgCountGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object getSpecifiedOfflineMsgStatus(final String userName, final String msgId) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameOfflineMsgStatusMsgIdGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName, msgId);
            }
        });
    }

    @Override
    public Object deactivateIMUser(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameDeactivatePost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object activateIMUser(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameActivatePost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object disconnectIMUser(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameDisconnectGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object getIMUserAllChatGroups(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameJoinedChatgroupsGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }

    @Override
    public Object getIMUserAllChatRooms(final String userName) {
        return this.responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return EasemobIMUsers.this.api.orgNameAppNameUsersUsernameJoinedChatroomsGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), userName);
            }
        });
    }
}
