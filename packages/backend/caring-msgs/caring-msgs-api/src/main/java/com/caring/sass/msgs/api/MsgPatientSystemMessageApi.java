package com.caring.sass.msgs.api;


import com.caring.sass.base.R;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @className: MsgPatientSystemMessageApi
 * @author: 杨帅
 * @date: 2023/8/21
 */

@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}",qualifier = "MsgPatientSystemMessageApi",path = "/msgPatientSystemMessage")
public interface MsgPatientSystemMessageApi {


    @ApiOperation("保存患者消息记录")
    @PostMapping("saveSystemMessage")
    R<Boolean> saveSystemMessage(@RequestBody @Validated MsgPatientSystemMessageSaveDTO systemMessageSaveDTO);

    @ApiOperation("删除某业务的系统消息")
    @DeleteMapping("deleteByBusinessId")
    R<Boolean> deleteByBusinessId(@RequestParam("functionType") String functionType, @RequestParam("businessId") Long businessId);

}
