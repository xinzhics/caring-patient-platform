package com.caring.sass.nursing.api;

import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.api.hystrix.AttrApiFallback;
import com.caring.sass.nursing.dto.tag.AttrPageDTO;
import com.caring.sass.nursing.dto.tag.AttrSaveDTO;
import com.caring.sass.nursing.dto.tag.AttrUpdateDTO;
import com.caring.sass.nursing.entity.tag.Attr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author leizhi
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", path = "/attr", qualifier = "AttrApi", fallback = AttrApiFallback.class)
public interface AttrApi extends SuperApi<Long, Attr, AttrPageDTO, AttrSaveDTO, AttrUpdateDTO> {
}
