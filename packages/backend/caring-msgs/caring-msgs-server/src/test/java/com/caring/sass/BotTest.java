package com.caring.sass;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.StaticLog;
import com.caring.sass.msgs.bot.BotContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class BotTest {

    @Resource
    private BotContext botContext;

    @Test
    public void init() {
        TimeInterval timer = DateUtil.timer();
        // -------这是执行过程--------------
        String ret = botContext.chat("你叫小帅，聪明的AI助手", "介绍下自己", "xiaozhi");
        log.info(ret);
        // ---------------------------------
        long interval = timer.interval();// 花费毫秒数
        long intervalMinute = timer.intervalMinute();// 花费分钟数
        StaticLog.info("本次程序执行 花费毫秒数: {} ,   花费分钟数:{} . ", interval, intervalMinute);
    }

}
