package com.caring.sass.cms;

import com.caring.sass.base.R;
import com.caring.sass.cms.entity.ContentLibrary;
import com.caring.sass.cms.hystrix.ContentLibraryApiFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName CmsContentApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 15:16
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.cms-server:caring-cms-server}", path = "/contentLibrary",
        qualifier = "ContentLibraryApi", fallback = ContentLibraryApiFallback.class)
public interface ContentLibraryApi {

    @ApiOperation(value = "根据ids批量查询", notes = "根据ids批量查询")
    @GetMapping({"/listByIds"})
    R<List<ContentLibrary>> listByIds(@RequestParam("ids[]") List<Long> ids);

}
