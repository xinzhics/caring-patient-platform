package com.caring.sass.msgs.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.msgs.api.fallback.ImApiFallback;
import com.caring.sass.msgs.dto.*;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.msgs.entity.ChatSendRead;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}", fallback = ImApiFallback.class
        ,qualifier = "ImApi",path = "im")
public interface ImApi {
    @ApiOperation("【账号】 - 注册账号")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(paramType="body", name="user", value="注册的用户账号信息", required=false, dataTypeClass=IMUserDto.class, dataType="IMUserDto")})
    @PostMapping({"/account/register"})
    R<Void> registerAccount(@RequestBody IMUserDto paramIMUserDto);

    @ApiOperation("【账号】 - 删除账号")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(paramType="path", name="username", value="需要删除的账号名", required=false, dataTypeClass=String.class, dataType="String")})
    @PostMapping({"/account/remove/{username}"})
    R<Void> removeAccount(@PathVariable("username") String paramString);

    @ApiOperation("【聊天】 - 发送聊天信息")
    @PostMapping({"/sendChat"})
    R<Chat> sendChat(@RequestBody Chat paramChat);

    @ApiOperation("获取角色未读的消息")
    @GetMapping({"/chat/first/roleType"})
    R<List<ChatSendRead>> chatFirstByRoleType(@RequestParam("receiverRoleType") String receiverRoleType);

    @ApiOperation("群发消息")
    @PostMapping({"/chat/sendGroup"})
    R<String> sendGroup(@RequestBody ChatGroupSend chatGroupSend);

    @ApiOperation("批量修改消息为已读")
    @PostMapping({"chat/refreshMsgStatus/{patientId}"})
    R refreshMsgStatus(@PathVariable("patientId") Long patientId);

    @ApiOperation("修改医生未读消息为已读")
    @PostMapping({"/chat/refreshDoctorMsg/{doctorId}"})
    R refreshDoctorNoReadMsgStatus(@PathVariable("doctorId") Long doctorId);

    @ApiOperation("获取IM的各种配置信息")
    @GetMapping({"/_config_"})
    R<IMConfigDto> getIMConfig();


    @ApiOperation("获取Im是否在线")
    @GetMapping({"imOnline/{imAccount}"})
    R<Boolean> imOnline(@PathVariable("imAccount") String imAccount);


    @ApiOperation("删除患者IM消息和账号")
    @DeleteMapping({"cleanChatAll/{patientImAccount}"})
    R<Boolean> cleanChatAll(@PathVariable("patientImAccount") String patientImAccount);


    @ApiOperation("批量清除医生和群组的未读记录")
    @PostMapping("clearChatNoReadHistory")
    R<Boolean> clearChatNoReadHistory(@RequestBody ChatClearHistory chatClearHistory);


    @ApiOperation("更新跟患者相关的所有信息（姓名， 头像）")
    @PostMapping("batchUpdatePatientForAllTable/{tenantCode}")
    R<Boolean> batchUpdatePatientForAllTable(
            @PathVariable("tenantCode") String tenantCode, @RequestBody JSONObject patient);

    @ApiOperation("web端使用的聊天记录")
    @PostMapping("getPatientChat")
    R<IPage<Chat>> getChatSend(@RequestBody PageParams<ChatPatientPageDTO> params);

    @ApiOperation("删除医助或医生和患者的消息列表")
    @DeleteMapping("removeChatUserNewMsg/{receiverImAccount}")
    R<Boolean> removeChatUserNewMsg(@PathVariable("receiverImAccount") String receiverImAccount,
                                    @RequestParam("requestId") String requestId,
                                    @RequestParam("requestRoleType") String requestRoleType);


    @ApiOperation("清除非医生患者的未读消息（医生变更小组使用）")
    @PostMapping("clearChatNoReadHistoryForNoMyPatient")
    R<Boolean> clearChatNoReadHistoryForNoMyPatient(@RequestBody ChatClearHistory chatClearHistory);


    @ApiOperation("统计医生所有的未读消息")
    @GetMapping("countDoctorMsgNumber/{doctorId}")
    R<Integer> countDoctorMsgNumber(@PathVariable(name = "doctorId") Long doctorId);

    @ApiOperation("更新IM提醒，设置他已填, 并回执")
    @GetMapping("updateImRemind")
    R<Boolean> updateImRemind(@RequestParam(name = "chatId") Long chatId);

    @ApiOperation("回复超时的患者信息")
    @PostMapping("/replyPatientMessage")
    R<String> replyPatientMessage();

    @ApiOperation("统计某时间之后有多少患者消息未读")
    @GetMapping("countPatientNumber/{userId}/{userType}")
    R<Integer> countPatientNumber(@PathVariable(name = "userId") Long userId,
                                         @PathVariable(name = "userType") String userType,
                                         @RequestParam(name = "tenantCode") String tenantCode,
                                         @RequestParam(name = "createTimeString") String createTimeString);

    @ApiOperation("发送医助系统通知")
    @PostMapping("/sendAssistanceNotice")
    R<String> sendAssistanceNotice(@RequestBody SendAssistanceNoticeDto sendAssistanceNoticeDto);

    @ApiOperation("查询患者给医生发送的最后一条消息")
    @GetMapping("/queryLastMsg")
    R<Chat> queryLastMsg(@RequestParam(name = "senderId") Long senderId,
                         @RequestParam(name = "userType") String userType,
                         @RequestParam(name = "receiverImAccount") String receiverImAccount);

}
