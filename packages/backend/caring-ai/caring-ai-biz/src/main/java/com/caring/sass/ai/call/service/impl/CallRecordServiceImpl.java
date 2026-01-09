package com.caring.sass.ai.call.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.ai.call.dao.CallRecordMapper;
import com.caring.sass.ai.call.service.CallRecordService;
import com.caring.sass.ai.entity.call.CallRecord;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 分身通话记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Service

public class CallRecordServiceImpl extends SuperServiceImpl<CallRecordMapper, CallRecord> implements CallRecordService {

    @Autowired
    KnowledgeUserService knowledgeUserService;


    @Override
    public boolean save(CallRecord model) {


        KnowledgeUser knowledgeUser = knowledgeUserService.getById(model.getDialUserId());
        if (knowledgeUser == null) {
            throw new BizException("拨号用户不存在");
        }
        Long bloggerUserId = model.getBloggerUserId();
        KnowledgeUser user = knowledgeUserService.getOne(Wrappers.<KnowledgeUser>lambdaQuery()
                .last("limit 1")
                .eq(KnowledgeUser::getAiStudioDoctorId, bloggerUserId));
        if (user == null) {
            throw new BizException("博主用户不存在");
        }

        // 对博主的通话时长 -1 分钟
        boolean updateSuccess = knowledgeUserService.updateCallDuration(user.getId(), -1);
        if (!updateSuccess) {
            throw new BizException("通话时长已不足，功能已暂停使用。");
        }

        model.setBloggerUserId(user.getId());
        model.setUserAvatar(knowledgeUser.getUserAvatar());
        model.setUserMobile(knowledgeUser.getUserMobile());
        model.setUserName(knowledgeUser.getUserName());
        model.setCallDuration(1);

        LocalDateTime now = LocalDateTime.now();
        model.setCallStartTime(now);
        model.setCallLastUpdatedTime(now);


        return super.save(model);
    }


    @Override
    public CallRecord submitLastTime(Long recordId) {
        CallRecord callRecord = getById(recordId);
        if (callRecord == null) {
            throw new BizException("通话记录不存在");
        }
        // 已经结束通话了，就不更新了。
        if (callRecord.getCallEndTime() != null) {
            return callRecord;
        }
        callRecord.setCallLastUpdatedTime(LocalDateTime.now());
        callRecord.setCallDuration(callRecord.getCallDuration() + 1);
        updateById(callRecord);

        // 对博主的通话时长，-1分钟
        boolean updateSuccess = knowledgeUserService.updateCallDuration(callRecord.getBloggerUserId(), -1);
        if (!updateSuccess) {
            throw new BizException("通话时长已不足，功能已暂停使用。");
        }

        return callRecord;
    }


    @Override
    public CallRecord endCall(Long recordId) {

        CallRecord callRecord = getById(recordId);
        if (callRecord == null) {
            throw new BizException("通话记录不存在");
        }
        LocalDateTime endTime = LocalDateTime.now();
        setCallDuration(callRecord, endTime);
        updateById(callRecord);

        return null;
    }

    private void setCallDuration(CallRecord callRecord, LocalDateTime endTime) {
        // 计算结束时间 和开始时间 之间的时长。精确到分钟数，不足1分钟按1分钟算。
        LocalDateTime callStartTime = callRecord.getCallStartTime();

        // 计算两个时间之间的纳秒数（Duration）
        Duration duration = Duration.between(callStartTime, endTime);

        // 获取总秒数（可能为负，但通常 startTime <= endTime）
        long seconds = Math.abs(duration.getSeconds());

        // 向上取整到分钟：(seconds + 59) / 60 等价于 ceil(seconds / 60.0)
        long minutes = (seconds + 59) / 60;
        callRecord.setCallEndTime(endTime);
        callRecord.setCallDuration(Integer.valueOf(minutes + ""));

    }


    @Override
    public void endTimeOutCall() {
        LocalDateTime now = LocalDateTime.now();
        // 超时时间是10分钟
        LocalDateTime timeoutTime = now.minusMinutes(10);
        // 查询所有超时的通话记录
        List<CallRecord> callRecords = list(Wrappers.<CallRecord>lambdaQuery()
                .le(CallRecord::getCallLastUpdatedTime, timeoutTime)
                .isNull(CallRecord::getCallEndTime));
        if (callRecords.isEmpty()) {
            return;
        }
        // 对超时的通话记录，进行处理。
        for (CallRecord callRecord : callRecords) {
            LocalDateTime dateTime = callRecord.getCallLastUpdatedTime().plusMinutes(1);
            setCallDuration(callRecord, dateTime);
            updateById(callRecord);
        }
    }

}