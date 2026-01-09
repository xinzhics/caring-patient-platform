package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.RoleApi;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色查询API
 *
 * @author caring
 * @date 2019/08/02
 */
@Component
public class RoleApiFallback implements RoleApi {
    @Override
    public R<List<Long>> findUserIdByCode(String[] codes) {
        return R.timeout();
    }
}
