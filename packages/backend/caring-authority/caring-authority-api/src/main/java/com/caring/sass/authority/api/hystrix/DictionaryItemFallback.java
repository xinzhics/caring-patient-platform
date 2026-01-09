package com.caring.sass.authority.api.hystrix;

import com.caring.sass.authority.api.DictionaryItemApi;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.base.R;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DictionaryItemFallback implements DictionaryItemApi {

    @Override
    public R<List<DictionaryItem>> queryTenantDict(String tenantCode) {
        return R.timeout();
    }
}
