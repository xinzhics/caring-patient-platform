package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.DoctorGroupApiFallback;
import com.caring.sass.user.entity.DoctorGroup;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName ConsultationGroupApi
 * @Description
 * @Author yangShuai
 * @Date 2020/10/26 17:02
 * @Version 1.0
 */
@Primary
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = DoctorGroupApiFallback.class
        , path = "/doctorGroup", qualifier = "DoctorGroupApiFallback")
public interface DoctorGroupApi {

    @ApiOperation("查询医生愿意看的医生患者, 和医生入组时间")
    @GetMapping(value = "/getDoctorGroup/otherDoctor")
    R<List<DoctorGroup>> getDoctorGroupOtherDoctor(@RequestParam("doctorId") Long doctorId);

    @ApiOperation("查询医生进入小组的时间")
    @GetMapping(value = "/getDoctorGroupTime")
    R<DoctorGroup> getDoctorGroupTime(@RequestParam("doctorId") Long doctorId, @RequestParam("patientImAccount") String patientImAccount);


    @ApiOperation("获取医生的一个小组(当前版本医生只有一个小组)")
    @GetMapping(value = "/getGroupDoctorOne")
    R<DoctorGroup> getGroupDoctorOne(@RequestParam("doctorId") Long doctorId);


    @ApiOperation("获取医生所在小组愿意接收消息的医生im账号")
    @GetMapping("getGroupDoctorListReadMyMessage")
    R<List<String>> getGroupDoctorListReadMyMessage(@RequestParam("doctorId") Long doctorId);

    @ApiOperation("获取医生的ID所在小组内的所有医生ID")
    @GetMapping(value = "/findGroupDoctorIdByDoctorId")
    R<List<Long>> findGroupDoctorIdByDoctorId(@RequestParam("doctorId") Long doctorId);


    @ApiOperation("获取小组内的所有医生ID")
    @GetMapping(value = "/findGroupDoctorIdByGroupId")
    R<List<Long>> findGroupDoctorIdByGroupId(@RequestParam("groupId") Long groupId);
}
