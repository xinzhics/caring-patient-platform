package com.caring.sass.wx.job;

import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.config.TemplateMessageSendTask;
import com.caring.sass.wx.service.thirdParty.WeChatAppInfoAccessToken;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务调动中心配置cron规则 {@see http://39.106.72.188:8767/xxl-job-admin/jobinfo}
 *
 * @author xinzh
 */
@Slf4j
@Component
public class TaskJobs {

    WeChatAppInfoAccessToken weChatAppInfoAccessToken;

    @Autowired
    ConfigService configService;

    @Autowired
    TemplateMessageSendTask templateMessageSendTask;

    public TaskJobs( WeChatAppInfoAccessToken weChatAppInfoAccessToken) {
        this.weChatAppInfoAccessToken = weChatAppInfoAccessToken;
    }

    /**
     * refreshThePublicAccessToken 定时处理 即将过期的 公众号授权。
     * 对使用第三方平台扫码授权的公众号， 定时检查token过期情况， 进行token续期刷新
     */
    @XxlJob("refreshThePublicAccessToken")
    public ReturnT<String> refreshThePublicAccessToken(String param) {
//        log.error("[refreshThePublicAccessToken] 定时任务开始 执行");
        weChatAppInfoAccessToken.getWeChatAppInfo();
        return ReturnT.SUCCESS;
    }


    /**
     * 推送redis中的模版消息
     * @param param
     * @return
     */
    @XxlJob("sendTemplateMessageTask")
    public ReturnT<String> sendTemplateMessageTask(String param) {
//        log.error("[sendTemplateMessageTask] 定时任务开始 执行");
        templateMessageSendTask.run();
        return ReturnT.SUCCESS;
    }


    @XxlJob("initAssistantMenu")
    public ReturnT<String> initAssistantMenu(String param) {
//        log.error("[initAssistantMenu] 定时任务开始 执行");
        configService.initAssistantMenu();
        return ReturnT.SUCCESS;
    }

}
