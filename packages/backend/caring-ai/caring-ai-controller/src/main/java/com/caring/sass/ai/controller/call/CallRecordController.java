package com.caring.sass.ai.controller.call;


import com.caring.sass.ai.call.service.CallRecordService;
import com.caring.sass.ai.dto.call.CallRecordPageDTO;
import com.caring.sass.ai.dto.call.CallRecordSaveDTO;
import com.caring.sass.ai.dto.call.CallRecordUpdateDTO;
import com.caring.sass.ai.entity.call.CallRecord;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 分身通话记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/callRecord")
@Api(value = "CallRecord", tags = "分身通话记录")
public class CallRecordController extends SuperController<CallRecordService, Long, CallRecord, CallRecordPageDTO, CallRecordSaveDTO, CallRecordUpdateDTO> {



    @GetMapping("/submitLastTime")
    @ApiOperation("提交一次通话时间")
    public R<CallRecord> submitLastTime(Long recordId) {
        CallRecord callRecord = baseService.submitLastTime(recordId);
        return R.success(callRecord);
    }


    /**
     * 手动结束通话
     */
    @GetMapping("/endCall")
    @ApiOperation("手动结束通话")
    public R<CallRecord> endCall(Long recordId) {
        CallRecord callRecord = baseService.endCall(recordId);
        return R.success(callRecord);
    }

}
