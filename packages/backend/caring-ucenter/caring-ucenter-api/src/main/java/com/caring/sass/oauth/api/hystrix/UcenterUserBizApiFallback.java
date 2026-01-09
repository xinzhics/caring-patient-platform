package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.UcenterUserBizApi;
import com.caring.sass.user.dto.UserBizDto;
import com.caring.sass.user.dto.UserBizInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户API熔断
 *
 * @author caring
 * @date 2019/07/23
 */
@Component
public class UcenterUserBizApiFallback implements UcenterUserBizApi {

    @Override
    public R<Map<String, Map<Long, String>>> queryUserInfo(UserBizDto userBizDto) {
        return R.timeout();
    }

    @Override
    public R<UserBizInfo> queryUserInfo(UserBizInfo userBizInfo) {
        return R.timeout();
    }
}
