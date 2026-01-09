package com.caring.sass.authority.api.hystrix;

import com.caring.sass.authority.api.CoreOrgApi;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.base.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户API熔断
 *
 * @author caring
 * @date 2019/07/23
 */
@Component
public class CoreOrgApiFallback implements CoreOrgApi {

    @Override
    public R<Org> get(Long id) {
        return R.timeout();
    }

    @Override
    public R<Org> queryByTenant() {
        return R.timeout();
    }


    @Override
    public R<Org> getByCode(String code) {
        return R.timeout();
    }
}
