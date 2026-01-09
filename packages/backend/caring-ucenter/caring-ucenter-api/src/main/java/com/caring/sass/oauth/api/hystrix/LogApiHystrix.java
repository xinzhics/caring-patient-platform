package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.log.entity.OptLogDTO;
import com.caring.sass.oauth.api.LogApi;
import org.springframework.stereotype.Component;

/**
 * 日志操作 熔断
 *
 * @author caring
 * @date 2019/07/02
 */
@Component
public class LogApiHystrix implements LogApi {
    @Override
    public R<OptLogDTO> save(OptLogDTO log) {
        return R.timeout();
    }
}
