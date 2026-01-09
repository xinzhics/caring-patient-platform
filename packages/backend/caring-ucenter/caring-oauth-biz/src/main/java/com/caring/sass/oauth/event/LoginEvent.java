package com.caring.sass.oauth.event;

import com.caring.sass.oauth.event.model.LoginStatusDTO;
import org.springframework.context.ApplicationEvent;

/**
 * 登录事件
 *
 * @author caring
 * @date 2020年03月18日17:22:55
 */
public class LoginEvent extends ApplicationEvent {
    public LoginEvent(LoginStatusDTO source) {
        super(source);
    }
}
