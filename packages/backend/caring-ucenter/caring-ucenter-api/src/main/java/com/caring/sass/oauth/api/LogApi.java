package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.log.entity.OptLogDTO;
import com.caring.sass.oauth.api.hystrix.LogApiHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 操作日志保存 API
 *
 * @author caring
 * @date 2019/07/02
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = LogApiHystrix.class, qualifier = "logApi")
public interface LogApi {

    /**
     * 保存日志
     *
     * @param log 日志
     * @return
     */
    @RequestMapping(value = "/optLog", method = RequestMethod.POST)
    R<OptLogDTO> save(@RequestBody OptLogDTO log);

}
