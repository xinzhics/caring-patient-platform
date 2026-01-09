package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.ParameterApi;
import org.springframework.stereotype.Component;


/**
 * 熔断类
 *
 * @author caring
 * @date 2020年02月09日11:24:23
 */
@Component
public class ParameterApiFallback implements ParameterApi {
    @Override
    public R<String> getValue(String key, String defVal) {
        return R.timeout();
    }
}
