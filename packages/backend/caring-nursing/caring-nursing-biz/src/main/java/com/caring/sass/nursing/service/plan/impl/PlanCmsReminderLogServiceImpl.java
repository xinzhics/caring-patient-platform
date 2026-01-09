package com.caring.sass.nursing.service.plan.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dao.plan.PlanCmsReminderLogMapper;
import com.caring.sass.nursing.entity.plan.PlanCmsReminderLog;
import com.caring.sass.nursing.service.plan.PlanCmsReminderLogService;
import com.caring.sass.nursing.util.HtmlContentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PlanCmsReminderLogServiceImpl
 * @Description 历史推送的文章
 * @Author yangShuai
 * @Date 2021/12/20 11:11
 * @Version 1.0
 */
@Slf4j
@Service
public class PlanCmsReminderLogServiceImpl extends SuperServiceImpl<PlanCmsReminderLogMapper, PlanCmsReminderLog> implements PlanCmsReminderLogService {

    @Autowired
    ChannelContentApi channelContentApi;

    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("plan-cms-reminder-log", false);

    private static final ExecutorService REMINDER_LOG_EXECUTOR = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500), THREAD_FACTORY);


    @Override
    public void submitSyncSave(PlanCmsReminderLog planCmsReminderLog, String tenantCode) {

        REMINDER_LOG_EXECUTOR.execute(() -> syncSave(planCmsReminderLog, tenantCode));

    }

    /**
     * 简单的cms 直接保存
     * @param planCmsReminderLog
     * @param tenantCode
     */
    public void syncSave(PlanCmsReminderLog planCmsReminderLog, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        baseMapper.insert(planCmsReminderLog);

    }

    @Override
    public void submitSyncCmsLinkSave(PlanCmsReminderLog planCmsReminderLog, String tenantCode) {

        REMINDER_LOG_EXECUTOR.execute(() -> syncCmsLinkSave(planCmsReminderLog, tenantCode));

    }

    /**
     * 可能要解析cms的链接，获取网址中文章的内容。
     * @param planCmsReminderLog
     * @param tenantCode
     */
    public void syncCmsLinkSave(PlanCmsReminderLog planCmsReminderLog, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        String cmsLink = planCmsReminderLog.getCmsLink();

        // 检验 文章链接是否 是saas系统的cms文章详情页
        if (cmsLink.contains(ApplicationProperties.getDomainName()) && cmsLink.contains("cms/show?id=")) {
            // 截取文章后面的ID的值。 然后请求cms接口拿到文章的标题和缩略图
            String subStringId = cmsLink.substring(cmsLink.lastIndexOf("id=") + 3);
            if (StringUtils.isNotEmptyString(subStringId)) {
                Long cmsId = null;
                try {
                    // 尝试转换为 Long
                    cmsId = Long.parseLong(subStringId);
                } catch (Exception e) {
                }
                if (cmsId != null) {
                    R<ChannelContent> title = channelContentApi.getTitle(cmsId);
                    if (title.getIsSuccess() != null && title.getIsSuccess()) {
                        ChannelContent titleData = title.getData();
                        planCmsReminderLog.setCmsTitle(titleData.getTitle());
                        planCmsReminderLog.setCmsId(cmsId);
                        planCmsReminderLog.setIcon(titleData.getIcon());
                        baseMapper.insert(planCmsReminderLog);
                        return;
                    }
                }
            }
        }

        // 只能尝试通过文章的链接。去扒拉网站的内容咯。
        String htmlContent = HtmlContentUtil.getHtmlContent(planCmsReminderLog.getCmsLink());
        planCmsReminderLog = HtmlContentUtil.getContent(htmlContent, planCmsReminderLog);
        if (Objects.nonNull(planCmsReminderLog)) {
            baseMapper.insert(planCmsReminderLog);
        }

    }



}
