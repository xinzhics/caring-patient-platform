
package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.ChatGroupAPI;
import com.caring.sass.msgs.utils.im.comm.ResponseHandler;
import io.swagger.client.StringUtil;
import io.swagger.client.api.GroupsApi;
import io.swagger.client.model.*;
import org.springframework.stereotype.Component;

/**
 * @author xinzh
 */
@Component
public class EasemobChatGroup implements ChatGroupAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private GroupsApi api = new GroupsApi();

    public EasemobChatGroup() {
    }

    @Override
    public Object getChatGroups(Long limit, String cursor) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), limit + "", cursor);
        });
    }

    @Override
    public Object getChatGroupDetails(String[] groupIds) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdsGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), StringUtil.join(groupIds, ","));
        });
    }

    @Override
    public Object createChatGroup(Object payload) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), (Group)payload);
        });
    }

    @Override
    public Object modifyChatGroup(String groupId, Object payload) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdPut(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, (ModifyGroup)payload);
        });
    }

    @Override
    public Object deleteChatGroup(String groupId) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId);
        });
    }

    @Override
    public Object getChatGroupUsers(String groupId) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdUsersGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId);
        });
    }

    @Override
    public Object addSingleUserToChatGroup(String groupId, String userId) {
        UserNames userNames = new UserNames();
        UserName userList = new UserName();
        userList.add(userId);
        userNames.usernames(userList);
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, userNames);
        });
    }

    @Override
    public Object addBatchUsersToChatGroup(String groupId, Object payload) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, (UserNames)payload);
        });
    }

    @Override
    public Object removeSingleUserFromChatGroup(String groupId, String userId) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdUsersUsernameDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, userId);
        });
    }

    @Override
    public Object removeBatchUsersFromChatGroup(String groupId, String[] userIds) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdUsersMembersDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, StringUtil.join(userIds, ","));
        });
    }

    @Override
    public Object transferChatGroupOwner(String groupId, Object payload) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupidPut(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, (NewOwner)payload);
        });
    }

    @Override
    public Object getChatGroupBlockUsers(String groupId) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdBlocksUsersGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId);
        });
    }

    @Override
    public Object addSingleBlockUserToChatGroup(String groupId, String userId) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdBlocksUsersUsernamePost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, userId);
        });
    }

    @Override
    public Object addBatchBlockUsersToChatGroup(String groupId, Object payload) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdBlocksUsersPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, (UserNames)payload);
        });
    }

    @Override
    public Object removeSingleBlockUserFromChatGroup(String groupId, String userId) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdBlocksUsersUsernameDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, userId);
        });
    }

    @Override
    public Object removeBatchBlockUsersFromChatGroup(String groupId, String[] userIds) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatgroupsGroupIdBlocksUsersUsernamesDelete(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), groupId, StringUtil.join(userIds, ","));
        });
    }
}
