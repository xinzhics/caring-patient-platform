package com.caring.sass.cms;

import com.caring.sass.base.api.SuperApi;
import com.caring.sass.cms.dto.ChannelContentPageDTO;
import com.caring.sass.cms.dto.ChannelContentSaveDTO;
import com.caring.sass.cms.dto.ChannelContentUpdateDTO;
import com.caring.sass.cms.hystrix.ChannelContentApiFallback;
import com.caring.sass.base.R;
import com.caring.sass.cms.entity.ChannelContent;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName CmsContentApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 15:16
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.cms-server:caring-cms-server}", path = "/channelContent",
        qualifier = "ChannelContentApi", fallback = ChannelContentApiFallback.class)
public interface ChannelContentApi extends SuperApi<Long, ChannelContent, ChannelContentPageDTO, ChannelContentSaveDTO, ChannelContentUpdateDTO> {

    @GetMapping("getChannelContent/{id}")
    R<ChannelContent> queryContentById(@PathVariable("id") Long id);

    @ApiOperation("不带项目信息，根据id查询cms内容")
    @GetMapping("getWithoutTenant/{id}")
    R<ChannelContent> getWithoutTenant(@PathVariable("id") Long id);

    @GetMapping("getBaseContent/{id}")
    R<ChannelContent> getBaseContent(@PathVariable("id") Long id);

    @ApiOperation("不带项目信息，查询cms的标题和缩略图")
    @GetMapping("getTitle/WithoutTenant/{id}")
    R<ChannelContent> getTitle(@PathVariable("id") Long id);


}
