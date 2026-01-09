package com.caring.sass.msgs.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.JsonUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.config.JPushConfig;
import com.caring.sass.msgs.dto.IMUserDto;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.msgs.utils.MediaTypeUtils;
import com.caring.sass.msgs.utils.file.FileUtils;
import com.caring.sass.msgs.utils.im.IMUtils;
import com.caring.sass.tenant.api.AppConfigApi;
import com.caring.sass.tenant.entity.AppConfig;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.Msg;
import io.swagger.client.model.UserName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

@Slf4j
@Service
public class IMService {

    @Autowired
    IMUtils imUtils;

    @Autowired
    JPushService jpushService;

    @Autowired
    AppConfigApi appConfigApi;

    public boolean isUserOnline(String userimaccount) {
        return this.imUtils.isUserOnline(userimaccount);
    }

    public boolean sendAssistanceNotice(String assistanceImAccount, String msgContent, String msgType) {
        Msg msg = new Msg();
        UserName userName = new UserName();
        userName.add(assistanceImAccount);

        msg.targetType("users").target(userName);
        JSONObject m = new JSONObject();
        m.put("type", "txt");
        m.put("msg", msgContent);
        msg.msg(m);
        JSONObject ext = new JSONObject();
        ext.put("type", msgType);
        ext.put("content", msgContent);
        msg.ext(ext);
        msg.from(assistanceImAccount);
        return imUtils.sendMsg(msg);
    }

    public boolean sendImRemindReceipt(List<String> imAccount, Long chatId, String senderImAccount,  String receiverImAccount) {
        Msg msg = new Msg();
        UserName userName = new UserName();
        for (String s : imAccount) {
            if (StrUtil.isNotEmpty(s)) {
                userName.add(s);
            }
        }
        msg.targetType("users").target(userName);
        JSONObject m = new JSONObject();
        m.put("type", "txt");
        String contentByType = MediaTypeUtils.getImContentByType(MediaType.text, chatId.toString());
        m.put("msg", contentByType);
        msg.msg(m);
        JSONObject ext = new JSONObject();

        ext.put("id", chatId.toString());
        ext.put("senderImAccount", senderImAccount);
        ext.put("receiverImAccount", receiverImAccount);
        ext.put("type", MediaType.remind);
        msg.ext(ext);
        msg.from(senderImAccount);
        return imUtils.sendMsg(msg);
    }

    /**
     * 撤回消息后， 通知组内成员
     * @param imAccount
     * @param chatId
     * @param senderImAccount
     * @param senderId
     * @param senderName
     * @param senderRoleType
     * @param senderAvatar
     * @return
     */
    public boolean sendWithdrawChat(List<String> imAccount, Long chatId,
                                    String senderImAccount, String senderId, String senderName, String senderRoleType,
                                    String senderAvatar, String receiverImAccount) {
        Msg msg = new Msg();
        UserName userName = new UserName();
        for (String s : imAccount) {
            if (StrUtil.isNotEmpty(s)) {
                userName.add(s);
            }
        }
        msg.targetType("users").target(userName);
        JSONObject m = new JSONObject();
        m.put("type", "txt");
        String contentByType = MediaTypeUtils.getImContentByType(MediaType.text, chatId.toString());
        m.put("msg", contentByType);
        msg.msg(m);
        JSONObject ext = new JSONObject();

        ext.put("id", chatId.toString());
        ext.put("senderImAccount", senderImAccount);
        ext.put("receiverImAccount", receiverImAccount);
        ext.put("senderId", senderId);
        ext.put("senderName", senderName);
        ext.put("senderRoleType", senderRoleType);
        ext.put("senderAvatar", senderAvatar);
        ext.put("type", MediaType.withdraw);
        msg.ext(ext);
        msg.from(senderImAccount);
        return imUtils.sendMsg(msg);
    }

    /**
     * 由于 app 前段的问题，暂时将 userName 拆开。一次只给一个人推送
     * @param chat
     * @return
     */
    public boolean sendImMessage(Chat chat) {
        Msg msg = new Msg();
        UserName userName = new UserName();
        List<ChatSendRead> sendReads = chat.getSendReads();
        for (ChatSendRead sendRead : sendReads) {
            if (StringUtils.isEmpty(sendRead.getImAccount())) {
                continue;
            }
            userName.add(sendRead.getImAccount());
        }
        if (userName.isEmpty()) {
            return true;
        }
        msg.targetType("users").target(userName);
        JSONObject m = JsonUtils.bean2JSONObject(chat);
        m.put("type", "txt");
        String contentByType = MediaTypeUtils.getImContentByType(chat.getType(), chat.getContent());
        m.put("msg", contentByType);
        msg.msg(m);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(chat));
        jsonObject.put("id", chat.getId().toString());
        msg.ext(jsonObject);
        msg.from(chat.getSenderImAccount());
        log.info("环信消息推送： {}", jsonObject);
        return imUtils.sendMsg(msg);
    }


    public void sendJpushMessage(String tenantCode, String nursingImAccount, Msg message) {
        BaseContextHandler.setTenant(tenantCode);
        String jPushAppKey = null;
        String jPushMasterSecret = null;
        R<AppConfig> appConfigR = appConfigApi.getByTenant();
        if (appConfigR.getIsSuccess().equals(true) && null != appConfigR.getData()) {
            AppConfig configData = appConfigR.getData();
            jPushAppKey = configData.getJpushAppkey();
            jPushMasterSecret = configData.getJpushMasterSecret();
        }
        if (StringUtils.isEmpty(jPushAppKey) || StringUtils.isEmpty(jPushMasterSecret)) {
            return;
        }
        if (StrUtil.isNotEmpty(nursingImAccount)) {
            JSONObject jsonObject = new JSONObject();
            JPushConfig config = new JPushConfig(jPushAppKey, jPushMasterSecret);
            jpushService.pushAlertToAlias(config, message.getTargetType() == null ? "CHAT" : message.getTargetType(), "您收到一条消息", jsonObject, nursingImAccount);

        }

    }

    /**
     *
     * @param tenantCode
     * @param contentByType
     * @param chatSendReads
     */
    public void sendJpushMessage(String tenantCode, String contentByType, List<ChatSendRead> chatSendReads) {
        String jPushAppKey = null;
        String jPushMasterSecret = null;
        BaseContextHandler.setTenant(tenantCode);
        String nursingImAccount = null;
        for (ChatSendRead sendRead : chatSendReads) {
            if (UserType.UCENTER_NURSING_STAFF.equals(sendRead.getRoleType())) {
                nursingImAccount = sendRead.getImAccount();
            }
        }
        if (StrUtil.isEmpty(nursingImAccount)) {
            return;
        }
        R<AppConfig> appConfigR = appConfigApi.getByTenant();
        if (appConfigR.getIsSuccess() && null != appConfigR.getData()) {
            AppConfig configData = appConfigR.getData();
            jPushAppKey = configData.getJpushAppkey();
            jPushMasterSecret = configData.getJpushMasterSecret();
        }

        if (StringUtils.isEmpty(jPushAppKey) || StringUtils.isEmpty(jPushMasterSecret)) {
            return;
        }

        if (!StringUtils.isEmpty(nursingImAccount)) {
            BaseContextHandler.setTenant(tenantCode);
            JPushConfig config = new JPushConfig(jPushAppKey, jPushMasterSecret);
            this.jpushService.pushAlertToAlias(config,  "users" , contentByType, new JSONObject(), nursingImAccount);
        }
    }


    public boolean register(IMUserDto user) {
        boolean register = false;
        try {
            register = imUtils.register(user.getUsername(), user.getPassword());
            if (!register) {
                log.error("[注册用户失败]:username:" + user.getUsername() + "  password:" + user.getPassword());
            }

            return register;
        } catch (Exception e) {
            String message = e.getMessage();
            if ("400".equals(message)) {
                return true;
            } else {
                log.error("注册出错了:错误码[" + message + "]", e);
                return false;
            }
        }
    }

    public boolean delete(String userName) {
        boolean flag = false;

        try {
            flag = this.imUtils.deleteImUser(userName);
            if (!flag) {
                log.error("[删除用户失败]:username:" + userName);
            }

            return flag;
        } catch (Exception var5) {
            String message = var5.getMessage();
            if ("400".equals(message)) {
                return true;
            } else {
                log.error("删除出错了:错误码[" + message + "]", var5);
                throw new RuntimeException("删除出错了:错误码[" + message + "]");
            }
        }
    }

    public Object upload(String path) {
        String dir = System.getProperty("user.dir") + "/" + UUID.randomUUID().toString().replace("-", "");
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

        Object var8;
        try {
            String imgName = UUID.randomUUID().toString().replace("-", "") + ".jpeg";
            String url = FileUtils.downLoadFromUrl(path, imgName, dir);
            File imgFile = new File(url);
            var8 = this.imUtils.upload(imgFile);
        } catch (Exception var17) {
            throw new RuntimeException("上传文件出现异常");
        } finally {
            if (file != null) {
                try {
                    org.apache.commons.io.FileUtils.deleteDirectory(file);
                } catch (IOException var16) {
                    log.error("删除文件夹出错");
                }
            }

        }

        return var8;
    }

}
