package com.caring.sass.tenant.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.H5RouterApi;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class H5RouterApiFallback implements H5RouterApi {

    @Override
    public R<Integer> countByPath(String path) {
        return R.timeout();
    }

    @Override
    public R<List<H5Router>> getH5RouterByModuleType(RouterModuleTypeEnum moduleType, String userType) {
        return R.timeout();
    }

    @Override
    public R<H5Router> getH5Router(String dictItemType, String userType) {
        return R.timeout();
    }

    @Override
    public R<List<String>> checkFolderShareUrlExist(String url) {
        return R.timeout();
    }
}
