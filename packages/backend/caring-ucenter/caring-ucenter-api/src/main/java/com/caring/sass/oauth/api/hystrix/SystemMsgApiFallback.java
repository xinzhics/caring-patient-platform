package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.SystemMsgApi;
import org.springframework.stereotype.Component;

@Component
public class SystemMsgApiFallback implements SystemMsgApi {
    @Override
    public R<Integer> countMessage(Long doctorId) {
        return R.timeout();
    }
}
