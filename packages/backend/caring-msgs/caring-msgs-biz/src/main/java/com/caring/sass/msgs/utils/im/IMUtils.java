//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.utils.im;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.utils.JsonUtils;
import com.caring.sass.msgs.utils.im.api.impl.EasemobChatMessage;
import com.caring.sass.msgs.utils.im.api.impl.EasemobFile;
import com.caring.sass.msgs.utils.im.api.impl.EasemobIMUsers;
import com.caring.sass.msgs.utils.im.api.impl.EasemobSendMessage;
import io.swagger.client.model.Msg;
import io.swagger.client.model.RegisterUsers;
import io.swagger.client.model.User;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IMUtils {
    public static final Logger logger = LoggerFactory.getLogger(IMUtils.class);
    @Autowired
    EasemobIMUsers easemobIMUsers;
    @Autowired
    EasemobSendMessage easemobSendMessage;
    @Autowired
    EasemobChatMessage easemobChatMessage;
    @Autowired
    EasemobFile easemobFile;

    public IMUtils() {
    }

    public boolean isUserOnline(String userIMAccount) {
        Object obj = this.easemobIMUsers.getIMUserStatus(userIMAccount);
        if (obj == null) {
            return false;
        } else {
            try {
                JSONObject jo = JSONObject.parseObject(obj.toString());
                JSONObject data = jo.getJSONObject("data");
                if (data == null) {
                    return false;
                } else {
                    String result = data.getString(userIMAccount);
                    return StringUtils.isEmpty(result) ? false : "online".equals(result);
                }
            } catch (Exception var6) {
                return false;
            }
        }
    }

    public boolean register(String username, String password) {
        RegisterUsers users = new RegisterUsers();
        User user = (new User()).username(username).password(password);
        users.add(user);
        Object o = this.easemobIMUsers.createNewIMUserSingle(users);
        logger.info("返回值：{}", o);
        return o != null;
    }

    public boolean sendMsg(Msg msg) {
        Object o = this.easemobSendMessage.sendMessage(msg);
        logger.info("sendMsg 返回值：{}", o);
        return o != null;
    }

    public Object chatlog() {
        Object o = this.easemobChatMessage.exportChatMessages("1");
        logger.info("返回值：{}", o);
        return o;
    }

    public Object upload(File file) {
        Object o = this.easemobFile.uploadFile(file);
        logger.info("返回值：{}", o);
        return o;
    }

    public Object download(String fileuuid, String shareSecret, boolean isThumbnail) {
        Object o = this.easemobFile.downloadFile(fileuuid, shareSecret, isThumbnail);
        logger.info("返回值：{}", o);
        return o;
    }

    public boolean deleteImUser(String userName) {
        Object o = this.easemobIMUsers.deleteIMUserByUserName(userName);
        logger.info("返回值：{}", o);
        return o != null;
    }



    /**
     * @Author yangShuai
     * @Description 查询 im 离线消息数量
     * @Date 2020/12/2 16:23
     *
     * @return int
     */
    public int offlineMsgCount(String imAccount) {
        Object offlineMsgCount = this.easemobIMUsers.getOfflineMsgCount(imAccount);
        if (offlineMsgCount == null) {
            return 0;
        } else {
            try {
                JSONObject jo = JSONObject.parseObject(offlineMsgCount.toString());
                JSONObject data = jo.getJSONObject("data");
                if (data == null) {
                    return 0;
                } else {
                    String result = data.getString(imAccount);
                    return StringUtils.isEmpty(result) ? 0 : Integer.parseInt(result);
                }
            } catch (Exception var6) {
                return 0;
            }
        }
    }

}
