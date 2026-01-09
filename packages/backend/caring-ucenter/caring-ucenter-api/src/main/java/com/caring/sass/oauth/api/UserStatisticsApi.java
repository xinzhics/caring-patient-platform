package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 用户统计
 *
 * @author caring
 * @date 2019/07/02
 */
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}"
        , path = "/userStatistics", qualifier = "UserStatisticsApi")
public interface UserStatisticsApi {

    /**
     * 用户统计
     *
     * @return 用户统计
     */
    @GetMapping("/userStatistics")
    R<Map<String, Integer>> userStatistics();
}
