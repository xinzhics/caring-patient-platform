package com.caring.sass.oauth.api.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.oauth.api.ConsultationGroupApi;
import com.caring.sass.user.entity.ConsultationGroupMember;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConsultationGroupApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 11:20
 * @Version 1.0
 */
@Component
public class ConsultationGroupApiFallback implements ConsultationGroupApi {

    @Override
    public R<ConsultationGroupMember> findConsultationGroupMember(Long groupId, JSONObject wxOAuth2UserInfo) {
        return R.timeout();
    }
}
