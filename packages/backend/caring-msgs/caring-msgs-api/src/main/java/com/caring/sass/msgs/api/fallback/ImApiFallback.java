package com.caring.sass.msgs.api.fallback;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.dto.*;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.msgs.entity.ChatSendRead;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 熔断
 *
 * @author caring
 * @date 2019/07/25
 */
@Component
public class ImApiFallback implements ImApi {

    @Override
    public R<Void> registerAccount(IMUserDto paramIMUserDto) {
        return R.timeout();
    }

    @Override
    public R<Void> removeAccount(String paramString) {
        return R.timeout();
    }

    @Override
    public R<Chat> sendChat(Chat paramChat) {
        return R.timeout();
    }

    @Override
    public R<List<ChatSendRead>> chatFirstByRoleType(String receiverRoleType) {
        return R.timeout();
    }

    @Override
    public R<String> sendGroup(ChatGroupSend chatGroupSend) {
        return R.timeout();
    }

    @Override
    public R refreshMsgStatus(Long patientId) {
        return R.timeout();
    }

    @Override
    public R refreshDoctorNoReadMsgStatus(Long doctorId) {
        return R.timeout();
    }


    @Override
    public R<IMConfigDto> getIMConfig() {
        return R.timeout();
    }

    @Override
    public R<Boolean> imOnline(String imAccount)  {
        return R.timeout();
    }

    @Override
    public R<Boolean> cleanChatAll(String patientImAccount) {
        return R.timeout();
    }

    @Override
    public R<Boolean> clearChatNoReadHistory(ChatClearHistory chatClearHistory) {
            return R.timeout();
    }

    @Override
    public R<Boolean> batchUpdatePatientForAllTable(String tenantCode, JSONObject patient) {
        return R.timeout();
    }

    @Override
    public R<IPage<Chat>> getChatSend(PageParams<ChatPatientPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<Boolean> removeChatUserNewMsg(String receiverImAccount, String requestId, String requestRoleType) {
        return R.timeout();
    }

    @Override
    public R<Boolean> clearChatNoReadHistoryForNoMyPatient(ChatClearHistory chatClearHistory) {
        return R.timeout();
    }

    @Override
    public R<Integer> countDoctorMsgNumber(Long doctorId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> updateImRemind(Long chatId) {
        return R.timeout();
    }

    @Override
    public R<String> replyPatientMessage() {
        return R.timeout();
    }

    @Override
    public R<Integer> countPatientNumber(Long userId, String userType,
                                         String tenantCode,
                                         String createTimeString) {
        return R.timeout();
    }

    @Override
    public R<String> sendAssistanceNotice(SendAssistanceNoticeDto sendAssistanceNoticeDto) {
        return R.timeout();
    }

    @Override
    public R<Chat> queryLastMsg(Long senderId, String userType, String receiverImAccount) {
        return R.timeout();
    }

}
