package com.caring.sass.cms;

import com.caring.sass.cms.dto.MassCallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName CmsContentApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 15:16
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.cms-server:caring-cms-server}", path = "/massMailing", qualifier = "MassCallBackApi"
)
public interface MassCallBackApi {

    @PostMapping("massCallBack")
    void massCallBack(@RequestBody MassCallBack massCallBack);

    @GetMapping("massMailing/send")
    void massMailing();

}
