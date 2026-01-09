package com.caring.sass.oauth.api.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName WXCallBackApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 11:20
 * @Version 1.0
 */
@Component
public class PatientApiFallback implements PatientApi {

    @Override
    public R<Boolean> update(Patient patient) {
        return R.timeout();
    }

    @Override
    public  R<Boolean> updateSuccessiveCheck(List<Long> ids) {
        return R.timeout();
    }

    @Override
    public R<Patient> queryByOpenId(String openId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> diseaseInformationStatus(Long patientId) {
        return R.timeout();
    }

    @Override
    public R<List<Patient>> findPatientByImAccounts(List<String> imAccounts) {
        return R.timeout();
    }

    @Override
    public R<Boolean> completeEnterGroup(Long patientId) {
        return R.timeout();
    }

    @Override
    public R<Patient> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Patient>> page(PageParams<PatientPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Patient>> query(Patient data) {
        return R.timeout();
    }

    @Override
    public R<List<Patient>> listByIds(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<List<NursingPlanPatientBaseInfoDTO>> getBaseInfoForNursingPlan(NursingPlanPatientDTO nursingPlanPatientDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> updatePatientCompleteEnterGroupTime(NursingPlanUpdatePatientDTO updatePatientDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> initPatientNameFirstLetter() {
        return R.timeout();
    }

    @Override
    public R<String> unregisteredReminder() {
        return R.timeout();
    }

    @Override
    public R<Void> updateByOpenId(JSONObject wxOAuth2UserInfo) {
        return R.timeout();
    }

    @Override
    public R<Patient> getBaseInfoForStatisticsData(Long patientId) {
        return R.timeout();
    }

    @Override
    public R<List<Patient>> findByIds(List<Long> ids) {
        return R.timeout();
    }

    @Override
    public R<IPage<Patient>> exportPageWithScope(PageParams<PatientPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<Map<Long, String>> findNursingPatientRemark(List<Long> ids) {
        return R.timeout();
    }

    @Override
    public R<Map<Long, Patient>> findPatientBaseInfoByIds(List<Long> ids) {
        return R.timeout();
    }

    @Override
    public R<Map<Long, String>> findDoctorPatientRemark(List<Long> ids) {
        return R.timeout();
    }

    @Override
    public R<Patient> findPatientName(String imAccount) {
        return R.timeout();
    }

    @Override
    public R<Boolean> batchUpdateUnionId() {
        return R.timeout();
    }

    @Override
    public R<ImGroupDetail> getPatientGroupDetail(Long patientId) {
        return R.timeout();
    }

    @Override
    public R<List<Object>> queryDoctorsPatientImAccount(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<Integer> countTenantPatientNumber() {
        return R.timeout();
    }

    @Override
    public R<Integer> countNursingOrgPatientNumber(Long nursingId, List<Long> tagIdList) {
        return R.timeout();
    }

    @Override
    public R<Integer> countDoctorPatientNumber(Long doctorId, List<Long> tagIdList) {
        return R.timeout();
    }

    @Override
    public R<List<Long>> doctorExitChatPatientList(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<Patient> register(PatientRegister patientRegister) {
        return R.timeout();
    }
}
