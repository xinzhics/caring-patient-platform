package com.caring.sass.authority.event.listener;

import com.caring.sass.authority.event.ParameterUpdateEvent;
import com.caring.sass.authority.event.model.ParameterUpdate;
import com.caring.sass.authority.service.auth.UserTokenService;
import com.caring.sass.common.constant.ParameterKey;
import com.caring.sass.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 参数修改事件监听，用于调整具体的业务
 *
 * @author caring
 * @date 2020年03月18日17:39:59
 */
@Component
@Slf4j
public class ParameterUpdateListener {

    @Autowired
    private UserTokenService userTokenService;

    @Async
    @EventListener({ParameterUpdateEvent.class})
    public void saveSysLog(ParameterUpdateEvent event) {
        ParameterUpdate source = (ParameterUpdate) event.getSource();

        BaseContextHandler.setTenant(source.getTenant());
        if (ParameterKey.LOGIN_POLICY.equals(source.getKey())) {
            userTokenService.reset();
        }
    }
}
