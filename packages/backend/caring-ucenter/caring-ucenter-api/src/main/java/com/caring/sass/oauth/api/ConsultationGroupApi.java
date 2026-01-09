package com.caring.sass.oauth.api;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.ConsultationGroupApiFallback;
import com.caring.sass.user.entity.ConsultationGroupMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName ConsultationGroupApi
 * @Description
 * @Author yangShuai
 * @Date 2020/10/26 17:02
 * @Version 1.0
 */
@Primary
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = ConsultationGroupApiFallback.class
        , path = "/consultationGroup", qualifier = "ConsultationGroupApiFallback")
public interface ConsultationGroupApi  {

    @PostMapping("findConsultationGroupMember/{groupId}")
    R<ConsultationGroupMember> findConsultationGroupMember(@PathVariable("groupId") Long groupId,
                                                           @RequestBody JSONObject wxOAuth2UserInfo);


}
