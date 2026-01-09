package com.caring.sass.cms;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.cms.dto.ChannelPageDTO;
import com.caring.sass.cms.dto.ChannelSaveDTO;
import com.caring.sass.cms.dto.ChannelUpdateDTO;
import com.caring.sass.cms.entity.Channel;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@FeignClient(name = "${caring.feign.cms-server:caring-cms-server}", path = "/channel", qualifier = "ChannelApi"
//        ,fallback = ChannelApiFallback.class)
)
public interface ChannelApi extends SuperApi<Long, Channel, ChannelPageDTO, ChannelSaveDTO, ChannelUpdateDTO> {

    @PostMapping("initChannel")
    R<Boolean> initChannel();

    @PostMapping("copyChannelAndChannelContent")
    R<Boolean> copyChannelAndChannelContent(@RequestParam("fromTenantCode") String fromTenantCode,
                                            @RequestParam("toTenantCode") String toTenantCode);

    @ApiOperation("查询栏目组")
    @PostMapping("channelList")
    R<List<Channel>> list(@RequestBody Channel data);

}
