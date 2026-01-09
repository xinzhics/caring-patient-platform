package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.base.R;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.user.entity.NursingStaff;
import org.springframework.stereotype.Component;

/**
 * 用户API熔断
 *
 * @author caring
 * @date 2019/07/23
 */
//@Component
//public class NursingStaffApiFallback implements NursingStaffApi {
//
//    @Override
//    public R<Boolean> createDefaultUser(Org org, String domain, String projectName) {
//        return R.timeout();
//    }
//
//    @Override
//    public R<NursingStaff> getNursingStaff(String mobile) {
//        return R.timeout();
//
//    }
//}
