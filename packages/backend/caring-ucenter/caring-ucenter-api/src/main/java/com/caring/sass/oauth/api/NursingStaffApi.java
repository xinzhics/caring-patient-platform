package com.caring.sass.oauth.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.user.dto.NursingStaffPageDTO;
import com.caring.sass.user.dto.NursingStaffSaveDTO;
import com.caring.sass.user.dto.NursingStaffUpdateDTO;
import com.caring.sass.user.entity.NursingStaff;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName NursingStaffApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 9:47
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.ucenter-server:caring-ucenter-server}" /*, fallback = NursingStaffApiFallback.class*/
        , path = "/nursingStaff", qualifier = "NursingStaffApi")
public interface NursingStaffApi extends SuperApi<Long, NursingStaff, NursingStaffPageDTO, NursingStaffSaveDTO, NursingStaffUpdateDTO> {


    @PostMapping("createDefaultUser/{domain}/{projectName}")
    R<Boolean> createDefaultUser(@RequestBody Org org, @PathVariable("domain") String domain, @PathVariable("projectName") String projectName);


    @GetMapping("getNursingStaff/{mobile}")
    R<NursingStaff> getNursingStaff(@PathVariable(value = "mobile", required = false) String mobile);


    @ApiOperation("更新机构下用户的表机构名称({orgId: '', orgName: ''})")
    @PostMapping(value = "/updateUserOrgName")
    R<Boolean> updateUserOrgName(@RequestBody JSONObject jsonObject);

    @ApiOperation("查询项目下医助的Id")
    @PostMapping(value = "/getNursingStaffIds")
    R<IPage<NursingStaff>> getNursingStaffIds(@RequestBody PageParams<NursingStaffPageDTO> pageParams);

    @ApiOperation(value = "带数据范围分页列表查询")
    @PostMapping(value = "/pageWithScope")
    R<IPage<NursingStaff>> pageWithScope(@RequestBody @Validated PageParams<NursingStaffPageDTO> params);

    @ApiOperation("查询医助所在机构及下属机构id")
    @GetMapping(value = "/getNursingOrgIds")
    R<List<Long>> getNursingOrgIds(@RequestParam("nursingId") Long nursingId);

    @ApiOperation("统计医助，不包括默认")
    @GetMapping(value = "/countUser")
    R<Integer> countUser();

    @ApiOperation("系统以医助身份发送im提示消息")
    @PutMapping("sendPatientImRemind")
    R<Boolean> sendPatientImRemind(@RequestParam("tenantCode") String tenantCode,
                                   @RequestParam("receiverImAccount") String receiverImAccount);
}
