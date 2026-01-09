package com.caring.sass.oauth.api;

import com.caring.sass.oauth.api.hystrix.StationApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 岗位API
 *
 * @author caring
 * @date 2019/08/02
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", path = "/station",
        qualifier = "stationApi", fallback = StationApiFallback.class)
public interface StationApi {

    /**
     * 根据id查询 岗位
     *
     * @param ids
     * @return
     */
    @GetMapping("/findStationByIds")
    Map<Serializable, Object> findStationByIds(@RequestParam(value = "ids") Set<Serializable> ids);

    /**
     * 根据id查询 岗位名称
     *
     * @param ids
     * @return
     */
    @GetMapping("/findStationNameByIds")
    Map<Serializable, Object> findStationNameByIds(@RequestParam(value = "ids") Set<Serializable> ids);
}
