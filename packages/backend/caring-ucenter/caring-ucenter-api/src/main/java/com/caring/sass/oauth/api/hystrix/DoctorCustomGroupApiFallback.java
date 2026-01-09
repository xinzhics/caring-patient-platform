package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.DoctorCustomGroupApi;
import com.caring.sass.user.entity.DoctorCustomGroup;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ConsultationGroupApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 11:20
 * @Version 1.0
 */
@Component
public class DoctorCustomGroupApiFallback implements DoctorCustomGroupApi {


    @Override
    public R<List<Long>> queryDoctorCustomGroup(Long doctorId, String groupName) {
        return R.timeout();
    }

    @Override
    public R<List<DoctorCustomGroup>> queryDoctorCustomGroup(List<Long> groupIds) {
        return R.timeout();
    }
}
