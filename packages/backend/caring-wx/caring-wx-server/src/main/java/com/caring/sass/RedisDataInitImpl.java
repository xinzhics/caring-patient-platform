package com.caring.sass;

import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.wx.service.guide.RegGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName RedisDataInitImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/4/27 11:50
 * @Version 1.0
 */
@Component
public class RedisDataInitImpl implements ApplicationRunner {


    @Autowired
    RegGuideService regGuideService;

    @Override
    public void run(ApplicationArguments args) {

        SaasGlobalThreadPool.execute(() -> regGuideService.initRedisData());

    }
}
