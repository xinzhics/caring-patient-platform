package com.caring.sass.authority.api.hystrix;

import com.caring.sass.authority.api.UserBizApi;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.base.R;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 用户API熔断
 *
 * @author caring
 * @date 2019/07/23
 */
@Component
public class UserBizApiFallback implements UserBizApi {
    @Override
    public R<List<Long>> findAllUserId() {
        return R.timeout();
    }

    @Override
    public R<List<User>> findUserList(Set<Serializable> ids) {
        return R.timeout();
    }
}
