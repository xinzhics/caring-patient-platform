package com.caring.sass.oauth.api;

import com.caring.sass.base.R;
import com.caring.sass.oauth.api.hystrix.DoctorCustomGroupApiFallback;
import com.caring.sass.user.entity.DoctorCustomGroup;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}", fallback = DoctorCustomGroupApiFallback.class
        , path = "/doctorCustomGroup", qualifier = "DoctorCustomGroupApiFallback")
public interface DoctorCustomGroupApi {

    @ApiOperation("搜索医生的小组")
    @GetMapping("queryDoctorCustomGroup")
    R<List<Long>> queryDoctorCustomGroup(@RequestParam(name = "doctorId") Long doctorId,
                                                @RequestParam(name = "groupName") String groupName);

    @ApiOperation("搜索医生的小组")
    @PostMapping("queryDoctorCustomGroup")
    R<List<DoctorCustomGroup>> queryDoctorCustomGroup(@RequestBody List<Long> groupIds);


}
