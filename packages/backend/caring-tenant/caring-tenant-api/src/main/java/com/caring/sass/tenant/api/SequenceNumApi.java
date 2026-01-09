package com.caring.sass.tenant.api;

import com.caring.sass.base.R;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xinzh
 */
@FeignClient(name = "${caring.feign.authority-server:caring-tenant-server}", path = "/sequenceNum", qualifier = "sequenceNumApi")
public interface SequenceNumApi {

    /**
     * 生成不重复递增的序号
     *
     * @param sequenceEnum 序号类型
     * @param length       序号长度
     * @return 序号
     */
    @GetMapping
    R<String> generate(@RequestParam(value = "sequenceEnum") SequenceEnum sequenceEnum,
                       @RequestParam(value = "length", defaultValue = "4") Integer length);
}
