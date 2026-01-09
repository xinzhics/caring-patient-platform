package com.caring.sass.oauth.api;

import com.caring.sass.oauth.api.hystrix.DictionaryItemApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 数据字典API
 *
 * @author caring
 * @date 2019/07/26
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", path = "dictionaryItem",
        qualifier = "dictionaryItemApi", fallback = DictionaryItemApiFallback.class)
public interface DictionaryItemApi {

    /**
     * 根据 code 查询字典
     *
     * @param codes
     * @return
     */
    @GetMapping("/findDictionaryItem")
    Map<Serializable, Object> findDictionaryItem(@RequestParam(value = "codes") Set<Serializable> codes);
}
