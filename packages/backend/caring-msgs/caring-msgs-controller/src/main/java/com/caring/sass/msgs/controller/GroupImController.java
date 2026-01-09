package com.caring.sass.msgs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ConsultationConstant;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.JsonUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.dto.ConsultationChatDto;
import com.caring.sass.msgs.dto.ImGroupDto;
import com.caring.sass.msgs.entity.ConsultationChat;
import com.caring.sass.msgs.service.ConsultationChatService;
import com.caring.sass.msgs.service.impl.IMService;
import com.caring.sass.msgs.service.impl.JPushService;
import com.caring.sass.msgs.utils.MediaTypeUtils;
import com.caring.sass.msgs.utils.im.api.impl.EasemobChatGroup;
import com.caring.sass.msgs.utils.im.api.impl.EasemobSendMessage;
import com.caring.sass.user.entity.ConsultationGroupMember;
import io.swagger.annotations.ApiOperation;
import io.swagger.client.model.Group;
import io.swagger.client.model.Msg;
import io.swagger.client.model.NewOwner;
import io.swagger.client.model.UserName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GroupImController
 * @Description 对接环信的im群组
 * @Author yangShuai
 * @Date 2021/6/2 16:04
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping({"groupIm"})
public class GroupImController {

    @Autowired
    EasemobSendMessage sendMessage;

    @Autowired
    EasemobChatGroup easemobChatGroup;

    @Autowired
    ConsultationChatService consultationChatService;

    @Autowired
    IMService imService;

    @Autowired
    JPushService jPushService;

    @ApiOperation("创建一个环信的im群组")
    @PostMapping("/created")
    public R<String> createImGroup(@RequestBody ImGroupDto imGroupDto) {
        try {
            Group group = new Group();
            List<String> imAccount = imGroupDto.getImAccount();
            BeanUtils.copyProperties(imGroupDto, group);
            UserName userName = new UserName();
            userName.addAll(imAccount);
            group.setMembers(userName);
            group.setPublic(false);
            group.setMaxusers(50);
            group.setApproval(true);
            Object chatGroup = this.easemobChatGroup.createChatGroup(group);
            JSONObject jsonObject = JSONObject.parseObject(chatGroup.toString());
            log.info("创建群组，返回群组信息" + jsonObject);
            Object data = jsonObject.get("data");
            JSONObject groupId = JSONObject.parseObject(data.toString());
            return R.success(groupId.get("groupid").toString());
        } catch (Exception var3) {
            var3.printStackTrace();
            log.error("创建群组失败", var3.getMessage());
        }
        return R.fail("创建环信群组失败");
    }

    @ApiOperation("删除环信的im群组")
    @GetMapping("/delete/{imGroupId}")
    public R<Boolean> deleteImGroup(@PathVariable("imGroupId") String imGroupId) {
        try {
            Object chatGroup = this.easemobChatGroup.deleteChatGroup(imGroupId);
            log.info("删除群组，返回群组信息" + chatGroup.toString());
            return R.success();
        } catch (Exception var3) {
        }
        return R.fail("创建环信群组失败");
    }

    @ApiOperation("添加成员到环信的im群组")
    @GetMapping("/addUserToGroup/{groupId}/{userImAccount}")
    public R<Boolean> addUserToGroup(@PathVariable("groupId") String groupId,
                                     @PathVariable("userImAccount") String userImAccount,
                                     @RequestParam(value = "newOwner", required = false) Boolean newOwner) {

        Object chatGroup = this.easemobChatGroup.addSingleUserToChatGroup(groupId, userImAccount);
        if (newOwner != null && newOwner) {
            NewOwner owner = new NewOwner();
            owner.setNewowner(userImAccount);
            this.easemobChatGroup.transferChatGroupOwner(groupId, owner);
        }
        log.info("添加成员到" + chatGroup.toString());
        return R.success();

    }

    @ApiOperation("移除成员从环信的im群组")
    @GetMapping("/removeUserFromGroup/{groupId}/{userImAccount}")
    public R<Boolean> removeUserFromGroup(@PathVariable("groupId") String groupId, @PathVariable("userImAccount") String userImAccount) {

        Object chatGroup = this.easemobChatGroup.removeSingleUserFromChatGroup(groupId, userImAccount);
        log.info("添加成员到" + chatGroup.toString());
        return R.success();

    }


    @ApiOperation("发送群组消息")
    @PostMapping("sendConsultationChat")
    public R<ConsultationChat> sendConsultationChat(@RequestBody ConsultationChat consultationChat) {

        Boolean sendJPush = consultationChat.getSendJPush();
        String receiverIm = consultationChat.getReceiverIm();

        consultationChatService.saveConsultationChat(consultationChat);
        // 发送环信消息
        Msg message = sendMessage(consultationChat);
        // 判断是否需要发送极光推送
        String tenant = BaseContextHandler.getTenant();
        try {
            jpushJiguang(sendJPush, tenant, receiverIm, message);
        } catch (Exception e) {
            log.info("极光推送异常, 主业务继续进行");
        }
        return R.success(consultationChat);
    }

    @ApiOperation("app手动已读消息")
    @PostMapping("appReadMessage")
    public R<Boolean> appReadMessage(@RequestParam("groupId") Long groupId, @RequestParam Long receiverId) {

        consultationChatService.appReadMessage(groupId, UserType.UCENTER_NURSING_STAFF, receiverId);

        return R.success(true);
    }

    @ApiOperation("医生手动已读消息")
    @PostMapping("doctorReadMessage")
    public R<Boolean> doctorReadMessage(@RequestParam("groupId") Long groupId, @RequestParam Long doctorId) {

        consultationChatService.appReadMessage(groupId, ConsultationGroupMember.ROLE_DOCTOR, doctorId);

        return R.success(true);
    }

    @ApiOperation("统计未读消息数量")
    @PostMapping("appReadMessage/{receiverId}")
    public R<Map<Long, Integer>> countNoReadMessage(@PathVariable("receiverId") Long receiverId,
                                                    @RequestParam(value = "userRole", required = false) String userRole,
                                                    @RequestBody List<Long> groupIds) {


        Map<Long, Integer> map = consultationChatService.countNoReadMessage(groupIds, userRole, receiverId);
        return R.success(map);
    }



    @ApiOperation("获取群组的消息记录")
    @PostMapping("getGroupImMessage/{groupId}")
    public R<List<ConsultationChat>> getGroupImMessage(@PathVariable Long groupId,
                                                       @RequestParam("createTime") Long createTime) {

        Date date = new Date(createTime);
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        List<ConsultationChat> consultationChats = consultationChatService.getGroupImMessage(groupId, localDateTime);
        return R.success(consultationChats);
    }

    @ApiOperation("删除群组聊天消息")
    @DeleteMapping("deleteImGroupMessage/{groupId}")
    public R<Boolean> deleteImGroupMessage(@PathVariable("groupId") Long groupId) {
        consultationChatService.deleteMessage(groupId);
        return R.success(true);
    }


    /**
     * 推送环信消息
     * @param consultationChat
     * @return
     */
    private Msg sendMessage(ConsultationChat consultationChat) {

        Msg msg = new Msg();
        UserName userName = new UserName();
        userName.add(consultationChat.getImGroupId());
        msg.targetType("chatgroups").target(userName);
        JSONObject m = JsonUtils.bean2JSONObject(consultationChat);
        MediaType chatType = consultationChat.getType();
        m.put("type", "txt");
        String content = consultationChat.getContent();
        if (ConsultationConstant.GUEST_DROP_OUT_GROUP_EVENT.equals(content)) {
            content = consultationChat.getSenderName() + "( " + consultationChat.getSenderRoleRemark() + " )" + "离开会诊小组";
        }
        if (ConsultationConstant.GUEST_JOIN_GROUP_EVENT.equals(content)) {
            content = consultationChat.getSenderName() + "( " + consultationChat.getSenderRoleRemark() + " )" + "加入会诊小组";
        }
        m.put("msg", MediaTypeUtils.getImContentByType(chatType, content));
        msg.msg(m);
        ConsultationChatDto chatDto = new ConsultationChatDto();
        BeanUtils.copyProperties(consultationChat, chatDto);

        // 将groupId 转为String。转给环信
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(chatDto));
        jsonObject.put("groupId", String.valueOf(consultationChat.getGroupId()));
        msg.ext(jsonObject);
        msg.from(consultationChat.getSenderImAccount());
        log.info("环信消息推送： {}", consultationChat);
        Object message = sendMessage.sendMessage(msg);
        if (message == null) {
            return null;
        }
        return msg;
    }

    /**
     * 极光推送
     * @param message
     */
    private void jpushJiguang(Boolean jPush, String tenantCode, String nursingImAccount, Msg message ) {
        if (jPush != null && jPush) {
            SaasGlobalThreadPool.execute(() -> {
                imService.sendJpushMessage(tenantCode, nursingImAccount, message);
            });
        }
    }


}
