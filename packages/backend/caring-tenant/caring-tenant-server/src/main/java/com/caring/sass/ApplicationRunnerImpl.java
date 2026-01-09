package com.caring.sass;

import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.tenant.service.AppConfigService;
import com.caring.sass.tenant.service.batchBuild.BatchBuildRedisMessageConsumption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    BatchBuildRedisMessageConsumption batchBuildRedisMessageConsumption;

    @Autowired
    AppConfigService appConfigService;

    @Override
    public void run(ApplicationArguments args) {
        SaasGlobalThreadPool.execute(() -> batchBuildRedisMessageConsumption.getMessage());
        SaasGlobalThreadPool.execute(() -> appConfigService.initRedisData());

    }
}
