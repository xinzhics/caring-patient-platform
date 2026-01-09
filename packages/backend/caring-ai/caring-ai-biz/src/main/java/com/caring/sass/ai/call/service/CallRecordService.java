package com.caring.sass.ai.call.service;

import com.caring.sass.ai.entity.call.CallRecord;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 分身通话记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
public interface CallRecordService extends SuperService<CallRecord> {


    /**
     * 提交一次最后通话时间
     * @param recordId
     * @return
     */
    CallRecord submitLastTime(Long recordId);


     /**
      * 手动结束通话
      */
    CallRecord endCall(Long recordId);


    /**
     * 超时结束通话
     */
    void endTimeOutCall();


}
