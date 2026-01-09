package com.caring.sass.oauth.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.DoctorGroupApi;
import com.caring.sass.user.entity.DoctorGroup;
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
public class DoctorGroupApiFallback implements DoctorGroupApi {


    @Override
    public R<List<DoctorGroup>> getDoctorGroupOtherDoctor(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<DoctorGroup> getDoctorGroupTime(Long doctorId, String patientImAccount) {
        return R.timeout();
    }

    @Override
    public R<DoctorGroup> getGroupDoctorOne(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<List<String>> getGroupDoctorListReadMyMessage(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<List<Long>> findGroupDoctorIdByDoctorId(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<List<Long>> findGroupDoctorIdByGroupId(Long groupId) {
        return R.timeout();
    }

}
