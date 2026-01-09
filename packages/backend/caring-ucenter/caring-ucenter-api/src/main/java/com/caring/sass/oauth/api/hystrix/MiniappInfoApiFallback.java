package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.MiniappInfoApi;
import com.caring.sass.user.entity.MiniappInfo;
import org.springframework.stereotype.Component;


@Component
public class MiniappInfoApiFallback implements MiniappInfoApi {


    @Override
    public R<MiniappInfo> findByOpenId(String openId, String appId) {
        return R.timeout();
    }

    @Override
    public R<MiniappInfo> createOrUpdateUser(MiniappInfo miniappInfo) {
        return R.timeout();
    }

    @Override
    public R<Boolean> setRemindSubscriptionMassageTrueByPhone(String phoneNumber) {
        return R.timeout();
    }

    @Override
    public R<MiniappInfo> selectByIdNoTenant(Long id) {
        return R.timeout();
    }
}
