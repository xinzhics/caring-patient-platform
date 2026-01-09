package com.caring.sass.cms.job;

import com.caring.sass.cms.service.SiteFolderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 任务调动中心配置cron规则 {@see http://39.106.72.188:8767/xxl-job-admin/jobinfo}
 *
 * @author xinzh
 */
@Slf4j
@Component
@AllArgsConstructor
public class TaskJobs {



    @Autowired
    SiteFolderService siteFolderService;

    @XxlJob("siteFolderUpdateDeleteDay")
    public ReturnT<String> siteFolderUpdateDeleteDay(String param) {
        log.info("siteFolderUpdateDeleteDay task start");
        LocalDateTime localDateTime = LocalDateTime.now();
        siteFolderService.siteFolderUpdateDeleteDay();
        log.info("siteFolderUpdateDeleteDay task start");
        return ReturnT.SUCCESS;
    }



}
