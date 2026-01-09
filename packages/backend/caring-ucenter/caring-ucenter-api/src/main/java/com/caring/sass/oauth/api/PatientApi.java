package com.caring.sass.oauth.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.api.QueryApi;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.oauth.api.hystrix.PatientApiFallback;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.Patient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PatientApi
 * @Description
 * @Author yangShuai
 * @Date 2020/10/26 17:02
 * @Version 1.0
 */
@Primary
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = PatientApiFallback.class
        , path = "/patient", qualifier = "PatientApiFallback")
public interface PatientApi extends QueryApi<Patient,Long,PatientPageDTO> {

    @PutMapping("update")
    R<Boolean> update(@RequestBody Patient patient);

    @PostMapping("updateSuccessiveCheck")
    R<Boolean> updateSuccessiveCheck(@RequestBody List<Long> ids);

    @GetMapping("queryByOpenId")
    R<Patient> queryByOpenId(@RequestParam(value = "openId") String openId);

    @ApiOperation("患者填写疾病信息完成")
    @PostMapping("/diseaseInformationStatus")
    R<Boolean> diseaseInformationStatus(@RequestParam("patientId") @NotNull(message = "患者id不为空") Long patientId);

    @ApiOperation("im账号匹配查询患者信息")
    @PostMapping("findPatientNameByImAccounts")
    R<List<Patient>> findPatientByImAccounts(@RequestBody List<String> imAccounts);

    /**
     * 患者完成入组
     */
    @PostMapping("completeEnterGroup")
    R<Boolean> completeEnterGroup(@RequestParam(value = "patientId") Long patientId);

    @PostMapping("getBaseInfoForNursingPlan")
    R<List<NursingPlanPatientBaseInfoDTO>> getBaseInfoForNursingPlan(@RequestBody NursingPlanPatientDTO nursingPlanPatientDTO);

    @PostMapping("updatePatientCompleteEnterGroupTime")
    R<Boolean> updatePatientCompleteEnterGroupTime(@RequestBody NursingPlanUpdatePatientDTO updatePatientDTO);


    @GetMapping("initPatientNameFirstLetter")
    R<Boolean> initPatientNameFirstLetter();

    @ApiOperation(value = "为注册提醒")
    @PostMapping("unregisteredReminder")
    R<String> unregisteredReminder();


    @ApiOperation("用户授权登录时，更新用户的微信信息")
    @PostMapping("anno/updateByOpenId")
    R<Void> updateByOpenId(@RequestBody JSONObject wxOAuth2UserInfo);


    @ApiOperation("获取患者的基本信息")
    @GetMapping("getBaseInfoForStatisticsData/{patientId}")
    R<Patient> getBaseInfoForStatisticsData(@PathVariable("patientId") Long patientId);

    @ApiOperation("通过ID查询患者详细信息")
    @PostMapping("findByIds")
    R<List<Patient>> findByIds(@RequestBody List<Long> ids);

    @ApiOperation(value = "导出任务查询带数据范围分页列表查询")
    @PostMapping(value = "/exportPageWithScope")
    R<IPage<Patient>> exportPageWithScope(@RequestBody @Validated PageParams<PatientPageDTO> params);

    @ApiOperation("通过ID查询医助对患者的备注")
    @PostMapping("findNursingPatientRemark")
    R<Map<Long, String>> findNursingPatientRemark(@RequestBody List<Long> ids);

    @ApiOperation("通过ID查询患者的基本信息")
    @PostMapping("findPatientBaseInfoByIds")
    R<Map<Long, Patient>> findPatientBaseInfoByIds(@RequestBody List<Long> ids);


    @ApiOperation("通过ID查询医生对患者的备注")
    @PostMapping("findDoctorPatientRemark")
    R<Map<Long, String>> findDoctorPatientRemark(@RequestBody List<Long> ids);


    @ApiOperation("患者im账号查询患者信息")
    @GetMapping("findPatientNameByImAccount/{imAccount}")
    R<Patient> findPatientName(@PathVariable("imAccount") String imAccount);


    @ApiOperation(value = "批量更新患者unionId")
    @GetMapping("/batchUpdateUnionId")
    R<Boolean> batchUpdateUnionId();

    @ApiOperation("获取聊天小组详细成员")
    @GetMapping("anno/getPatientGroupDetail")
    R<ImGroupDetail> getPatientGroupDetail(@RequestParam("patientId") Long patientId);

    @ApiOperation("查询医生下患者的im账号")
    @GetMapping("queryDoctorsPatientImAccount/{doctorId}")
    R<List<Object>> queryDoctorsPatientImAccount(@PathVariable("doctorId") Long doctorId);


    @ApiOperation("统计租户下入组且未取关患者数量")
    @GetMapping("countTenantPatientNumber")
    R<Integer> countTenantPatientNumber();


    @ApiOperation("统计医助所属机构和子机构下的患者的数量")
    @PostMapping("countNursingOrgPatientNumber")
    R<Integer> countNursingOrgPatientNumber(@RequestParam("nursingId") Long nursingId, @RequestBody List<Long> tagIdList);

    @ApiOperation("统计统计医生下 注册未取关 患者数量")
    @PostMapping("countDoctorPatientNumber")
    R<Integer> countDoctorPatientNumber(@RequestParam("doctorId") Long doctorId, @RequestBody List<Long> tagIdList);


    @ApiOperation("医生查询自己关闭沟通的患者")
    @PutMapping("doctorExitChatPatientList")
    R<List<Long>> doctorExitChatPatientList(@RequestParam("doctorId") Long doctorId);


    @ApiOperation("患者注册或登录")
    @PostMapping("anno/patient/register")
    R<Patient> register(@RequestBody PatientRegister patientRegister);

}
