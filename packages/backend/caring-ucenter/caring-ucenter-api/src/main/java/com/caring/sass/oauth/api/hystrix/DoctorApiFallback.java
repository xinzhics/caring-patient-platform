package com.caring.sass.oauth.api.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.user.dto.AppDoctorDTO;
import com.caring.sass.user.dto.DoctorPageDTO;
import com.caring.sass.user.entity.Doctor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName WXCallBackApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 11:20
 * @Version 1.0
 */
@Component
public class DoctorApiFallback implements DoctorApi {
    @Override
    public R<Boolean> update(Doctor doctor) {
        return R.timeout();
    }

    @Override
    public R<Boolean> updateWithTenant(Doctor doctor, String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<Doctor> findByMobile(String mobile) {
        return R.timeout();
    }

    @Override
    public R<Doctor> findByOpenId(String openId, String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<IPage<Doctor>> pageWithScope(PageParams<DoctorPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Doctor>> findDoctorNameAndId(AppDoctorDTO appDoctorDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> updateDoctorBusinessCardQrCodeForTenantInfo(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<Integer> countDoctor() {
        return R.timeout();
    }

    @Override
    public R<Doctor> getDoctorBaseInfoById(Long id) {
        return R.timeout();
    }

    @Override
    public R<Doctor> getDoctorBaseInfoByPatientId(Long patientId) {
        return R.timeout();
    }

    @Override
    public R<List<Doctor>> findByIdsNoTenant(List<Long> doctorIds) {
        return R.timeout();
    }

    @Override
    public R<Long> doctorSendGroupMsg(JSONObject chatGroupSend) {
        return R.timeout();
    }

    @Override
    public R<String> getPatientQrCode(String mobile, String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<Doctor> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Doctor>> page(PageParams<DoctorPageDTO> pageParams) {
        return R.timeout();
    }

    @Override
    public R<List<Doctor>> query(Doctor doctor) {
        return R.timeout();
    }

    @Override
    public R<List<Doctor>> listByIds(List<Long> list) {
        return R.timeout();
    }
}
