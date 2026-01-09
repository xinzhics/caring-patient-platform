package com.caring.sass.cms.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.cms.ContentLibraryApi;
import com.caring.sass.cms.entity.ContentLibrary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContentLibraryApiFallback implements ContentLibraryApi {


    @Override
    public R<List<ContentLibrary>> listByIds(List<Long> longs) {
        return R.success(new ArrayList<>());
    }

}
