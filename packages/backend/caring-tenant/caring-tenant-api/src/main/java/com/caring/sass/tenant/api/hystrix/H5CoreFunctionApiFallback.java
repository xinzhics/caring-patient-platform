package com.caring.sass.tenant.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.H5CoreFunctionApi;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import org.springframework.stereotype.Component;

@Component
public class H5CoreFunctionApiFallback implements H5CoreFunctionApi {


    @Override
    public R<H5CoreFunctions> findOneByCode() {
        return R.timeout();
    }
}
