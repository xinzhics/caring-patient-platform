
package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.AuthTokenAPI;
import org.springframework.stereotype.Component;

@Component
public class EasemobAuthToken implements AuthTokenAPI {
    public EasemobAuthToken() {
    }

    @Override
    public Object getAuthToken() {
        return IMConfigWrapper.getAccessToken();
    }
}
