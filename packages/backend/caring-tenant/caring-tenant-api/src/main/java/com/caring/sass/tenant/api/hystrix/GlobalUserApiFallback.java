package com.caring.sass.tenant.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.GlobalUserApi;
import com.caring.sass.tenant.entity.GlobalUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalUserApiFallback implements GlobalUserApi {
    @Override
    public R<GlobalUser> get(Long id) {
        return R.timeout();
    }

    @Override
    public R<List<GlobalUser>> query(GlobalUser data) {
        return R.timeout();
    }

    @Override
    public R<GlobalUser> login(GlobalUser globalUser) {
        return R.timeout();
    }

    @Override
    public R<GlobalUser> mobileLogin(String mobile, String password) {
        return R.timeout();
    }


    @Override
    public R<GlobalUser> mobile(String mobile) {
        return R.timeout();
    }
}
