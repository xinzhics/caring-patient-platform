//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.service.impl;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.AndroidNotification.Builder;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.msgs.config.JPushConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map.Entry;

@Slf4j
@Service
public class JPushService {

    public void pushAlert(JPushConfig config, String category, String title, JSONObject jo) {
        String message = "";
        if (jo != null) {
            jo.put("title", title);
            jo.put("category", category);
            if (jo != null) {
                message = jo.toJSONString();
            }
        }

        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(config.getMasterSecret(), config.getAppkey(), (HttpProxy)null, clientConfig);
        AndroidNotification an = this.createAndroidNotification(title, jo);
        PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.all()).setNotification(Notification.newBuilder().addPlatformNotification(an).build()).setOptions(Options.newBuilder().setApnsProduction(config.isApns()).build()).build();

        try {
            jpushClient.sendPush(payload);
        } catch (APIConnectionException var11) {
            var11.printStackTrace();
        } catch (APIRequestException var12) {
            var12.printStackTrace();
        }

    }

    public void pushAlertToAlias(JPushConfig config, String category, String title, JSONObject jo, String alias) {
        String message = "";
        if (jo != null) {
            jo.put("title", title);
            jo.put("category", category);
            if (jo != null) {
                message = jo.toJSONString();
            }
        }

        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(config.getMasterSecret(), config.getAppkey(), (HttpProxy)null, clientConfig);
        AndroidNotification an = this.createAndroidNotification(title, jo);
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(new String[]{alias}))
                .setNotification(Notification.newBuilder().addPlatformNotification(an).build())
                .setOptions(Options.newBuilder().setApnsProduction(config.isApns()).build())
                .build();
        try {
            jpushClient.sendPush(payload);
        } catch (APIConnectionException var12) {
            var12.printStackTrace();
        } catch (APIRequestException var13) {
            var13.printStackTrace();
        }

    }

    public void pushAlertToTags(JPushConfig config, String category, String title, JSONObject jo, String[] tags) {
        String message = "";
        if (jo != null) {
            jo.put("title", title);
            jo.put("category", category);
            if (jo != null) {
                message = jo.toJSONString();
            }
        }

        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(config.getMasterSecret(), config.getAppkey(), (HttpProxy)null, clientConfig);
        AndroidNotification an = this.createAndroidNotification(title, jo);
        PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all())
                .setAudience(Audience.tag(tags))
                .setNotification(Notification.newBuilder().addPlatformNotification(an).build())
                .setOptions(Options.newBuilder().setApnsProduction(config.isApns()).build())
                .build();

        try {
            jpushClient.sendPush(payload);
        } catch (APIConnectionException var12) {
            var12.printStackTrace();
        } catch (APIRequestException var13) {
            var13.printStackTrace();
        }

    }

    AndroidNotification createAndroidNotification(String title, JSONObject params) {
        Builder builder = AndroidNotification.newBuilder();
        builder.setAlert(title);
        builder.setTitle(title);
        if (null != params && params.size() > 0) {
            Iterator var4 = params.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, Object> entry = (Entry)var4.next();
                if (entry.getValue() != null) {
                    builder.addExtra((String)entry.getKey(), entry.getValue().toString());
                }
            }
        }

        return builder.build();
    }

}
