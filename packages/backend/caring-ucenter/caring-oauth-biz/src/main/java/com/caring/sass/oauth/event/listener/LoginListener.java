package com.caring.sass.oauth.event.listener;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.auth.UserTokenService;
import com.caring.sass.authority.service.common.LoginLogService;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.oauth.event.LoginEvent;
import com.caring.sass.oauth.event.model.LoginStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 登录事件监听，用于记录登录日志
 *
 * @author caring
 * @date 2020年03月18日17:39:59
 */
@Component
@Slf4j
public class LoginListener {
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private UserService userService;

    @Async
    @EventListener({LoginEvent.class})
    public void saveSysLog(LoginEvent event) {
        LoginStatusDTO loginStatus = (LoginStatusDTO) event.getSource();

        if (StrUtil.isEmpty(loginStatus.getTenant())) {
            log.warn("忽略记录登录日志:{}", loginStatus);
            return;
        }

        BaseContextHandler.setTenant(loginStatus.getTenant());
        if (LoginStatusDTO.Type.SUCCESS == loginStatus.getType()) {
            // 重置错误次数 和 最后登录时间
            this.userService.resetPassErrorNum(loginStatus.getId());
        } else if (LoginStatusDTO.Type.PWD_ERROR == loginStatus.getType()) {
            // 密码错误
            this.userService.incrPasswordErrorNumById(loginStatus.getId());
        }
        loginLogService.save(loginStatus.getId(), loginStatus.getAccount(), loginStatus.getUa(), loginStatus.getIp(), loginStatus.getLocation(), loginStatus.getDescription());
    }

}
