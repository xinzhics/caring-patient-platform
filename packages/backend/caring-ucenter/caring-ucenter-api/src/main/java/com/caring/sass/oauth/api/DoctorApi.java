package com.caring.sass.oauth.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.api.QueryApi;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.oauth.api.hystrix.DoctorApiFallback;
import com.caring.sass.user.dto.AppDoctorDTO;
import com.caring.sass.user.dto.DoctorPageDTO;
import com.caring.sass.user.entity.Doctor;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName PatientApi
 * @Description
 * @Author yangShuai
 * @Date 2020/10/26 17:02
 * @Version 1.0
 */
@Primary
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = DoctorApiFallback.class
        , path = "/doctor", qualifier = "DoctorApiFallback")
public interface DoctorApi extends QueryApi<Doctor, Long, DoctorPageDTO> {

    @PutMapping("update")
    R<Boolean> update(@RequestBody Doctor doctor);

    @PutMapping("updateWithTenant/{tenantCode}")
    R<Boolean> updateWithTenant(@RequestBody Doctor doctor, @PathVariable(value = "tenantCode") String tenantCode);

    /**
     * 通过手机号查找医生信息
     *
     * @param mobile 电话号码
     * @return 医生信息
     */
    @GetMapping("/findByMobile")
    R<Doctor> findByMobile(@RequestParam(value = "mobile") String mobile);

    /**
     * 通过微信openId查找医生信息
     *
     * @param openId 微信openId
     * @return 医生信息
     */
    @GetMapping("/findByOpenId")
    R<Doctor> findByOpenId(@RequestParam(value = "openId") String openId,
                           @RequestParam(value = "tenantCode") String tenantCode);


    @ApiOperation(value = "带数据范围分页列表查询")
    @PostMapping(value = "/pageWithScope")
    R<IPage<Doctor>> pageWithScope(@RequestBody @Validated PageParams<DoctorPageDTO> params);


    @ApiOperation(value = "查询医助下医生的名字和id")
    @PostMapping("findDoctorNameAndId")
    R<List<Doctor>> findDoctorNameAndId(@RequestBody AppDoctorDTO appDoctorDTO);


    @ApiOperation(value = "项目信息更新，刷新医生的名片")
    @PutMapping("/updateDoctorBusinessCardQrCodeForTenantInfo")
    R<Boolean> updateDoctorBusinessCardQrCodeForTenantInfo(@RequestParam("tenantCode") String tenantCode);

    @ApiOperation(value = "统计医生，不包括默认医生")
    @GetMapping("/countDoctor")
    R<Integer> countDoctor();

    @ApiOperation("查询患者的医生的基本信息")
    @GetMapping("/getDoctorBaseInfoById/{id}")
    R<Doctor> getDoctorBaseInfoById(@PathVariable("id") Long id);

    @ApiOperation("查询患者的医生的基本信息")
    @GetMapping("/getDoctorBaseInfoByPatientId/{patientId}")
    R<Doctor> getDoctorBaseInfoByPatientId(@PathVariable("patientId") Long patientId);

    @ApiOperation("通过Ids查询不限租户")
    @PutMapping("findByIdsNoTenant")
    R<List<Doctor>> findByIdsNoTenant(@RequestBody List<Long> doctorIds);


    @ApiOperation("群发消息并返回发送人数")
    @PostMapping("/doctorSendGroupMsg")
    R<Long> doctorSendGroupMsg(@RequestBody JSONObject chatGroupSend);

    @ApiOperation("获取患者二维码")
    @GetMapping("getPatientQrCode")
    R<String> getPatientQrCode(@RequestParam("mobile") String mobile,
                               @RequestParam("tenantCode") String tenantCode);
}
