package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ConsultationConstant;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.GroupImApi;
import com.caring.sass.msgs.dto.ImGroupDto;
import com.caring.sass.msgs.entity.ConsultationChat;
import com.caring.sass.msgs.entity.ConsultationMessageStatus;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.tenant.api.AppConfigApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.ConsultationGroupService;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.impl.ImService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Validated
@RestController
@RequestMapping("/consultationGroup")
@Api(value = "consultationGroup", tags = "会诊")
public class ConsultationGroupController extends SuperController<ConsultationGroupService, Long, ConsultationGroup, ConsultationPageGroup, ConsultationSaveGroup, ConsultationUpdateGroup> {

    @Autowired
    GroupImApi groupImApi;

    @Autowired
    ImService imService;

    @Autowired
    AppConfigApi appConfigApi;

    @Autowired
    NursingStaffService nursingStaffService;

    @Autowired
    PatientService patientService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    OauthApi oauthApi;

    @Autowired
    DoctorService doctorService;

    public static final String IM_GUEST_PREFIX = "guest";

    /**
     * app 发起一个病例讨论
     * @param consultationSaveGroup
     * @return
     */
    @Override
    public R<ConsultationGroup> save(ConsultationSaveGroup consultationSaveGroup) {

        Long saveGroupNurseId = consultationSaveGroup.getNurseId();
        List<Long> patientIds = consultationSaveGroup.getPatientIds();
        if (patientIds == null) {
            patientIds = new ArrayList<>();
            String stringPatientIds = consultationSaveGroup.getStringPatientIds();
            if (StringUtils.isEmpty(stringPatientIds)) {
                return R.fail("参数异常");
            }
            String[] split = stringPatientIds.split(",");
            for (String id : split) {
                patientIds.add(Long.parseLong(id));
            }
            consultationSaveGroup.setPatientIds(patientIds);
        }
        ConsultationGroup group = BeanUtil.toBean(consultationSaveGroup, this.getEntityClass());
        group.setCreateUserType(UserType.UCENTER_NURSING_STAFF);
        baseService.save(group);
        NursingStaff nursingStaff = nursingStaffService.getById(saveGroupNurseId);
        List<String> imAccount = new ArrayList<>(patientIds.size());
        for (Long patientId : patientIds) {
            Patient patient = patientService.getById(patientId);
            if (patient != null && patient.getImAccount() != null) {
                imAccount.add(patient.getImAccount());
            }
        }
        Tenant tenant;
        R<String> imGroup = groupImApi.createImGroup(ImGroupDto.builder().owner(nursingStaff.getImAccount())
                .groupname(consultationSaveGroup.getGroupName())
                .desc(consultationSaveGroup.getGroupDesc())
                .imAccount(imAccount).build());
        if (imGroup.getIsSuccess() != null && imGroup.getIsSuccess()) {
            group.setImGroupId(imGroup.getData());
            R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
            if (tenantR.getIsSuccess() == null || !tenantR.getIsSuccess()) {
                baseService.removeById(group.getId());
                log.info("请求租户服务失败");
                return R.fail("系统繁忙");
            }
            tenant = tenantR.getData();
            String consultationNotice = ApplicationDomainUtil.wxGuestBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                    "consultationDoctor", String.format(H5Router.CONSULTATION_DOCTOR_NOTICE, group.getId()));
            group.setConsultationEntrance(consultationNotice);
            baseService.updateById(group);
        } else {
            baseService.removeById(group.getId());
            log.info("创建环信im群组失败");
            return R.fail("创建会诊组失败");
        }
        // 保存基本的群组成员。患者和医助
        baseService.appSaveMember(group, nursingStaff, patientIds, tenant);
        return R.success(group);

    }

    /**
     * 医生发起一个病例讨论
     *
     */
    @PostMapping("doctor/create")
    @ApiOperation("医生发起一个病例讨论")
    public R<ConsultationGroup> doctorCreate(@RequestBody ConsultationSaveGroup consultationSaveGroup) {

        Long doctorId = consultationSaveGroup.getDoctorId();
        List<Long> patientIds = consultationSaveGroup.getPatientIds();
        if (patientIds == null) {
            patientIds = new ArrayList<>();
            String stringPatientIds = consultationSaveGroup.getStringPatientIds();
            if (StringUtils.isEmpty(stringPatientIds)) {
                return R.fail("参数异常");
            }
            String[] split = stringPatientIds.split(",");
            for (String id : split) {
                patientIds.add(Long.parseLong(id));
            }
            consultationSaveGroup.setPatientIds(patientIds);
        }
        Tenant tenant;
        ConsultationGroup group = BeanUtil.toBean(consultationSaveGroup, this.getEntityClass());
        Doctor doctor = doctorService.getById(doctorId);
        if (Objects.isNull(doctor)) {
            return R.fail("医生不存在");
        }
        group.setCreateUserType(UserType.UCENTER_DOCTOR);
        baseService.save(group);
        List<String> imAccount = new ArrayList<>(patientIds.size());
        for (Long patientId : patientIds) {
            Patient patient = patientService.getById(patientId);
            if (patient != null && patient.getImAccount() != null) {
                imAccount.add(patient.getImAccount());
            }
        }
        R<String> imGroup = groupImApi.createImGroup(ImGroupDto.builder()
                .owner(doctor.getImAccount())
                .groupname(consultationSaveGroup.getGroupName())
                .desc(consultationSaveGroup.getGroupDesc())
                .imAccount(imAccount).build());
        if (imGroup.getIsSuccess() != null && imGroup.getIsSuccess()) {
            group.setImGroupId(imGroup.getData());
            R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
            if (tenantR.getIsSuccess() == null || !tenantR.getIsSuccess()) {
                baseService.removeById(group.getId());
                log.info("请求租户服务失败");
                return R.fail("系统繁忙");
            }
            tenant = tenantR.getData();
            String consultationNotice = ApplicationDomainUtil.wxGuestBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),
                    "consultationDoctor", String.format(H5Router.CONSULTATION_DOCTOR_NOTICE, group.getId()));
            group.setConsultationEntrance(consultationNotice);
            baseService.updateById(group);
        } else {
            baseService.removeById(group.getId());
            log.info("创建环信im群组失败");
            return R.fail("创建会诊组失败");
        }
        // 保存基本的群组成员。患者和医助
        baseService.doctorSaveMember(group, doctor, patientIds, tenant);
        return R.success(group);

    }


    @PostMapping("addMemberToGroup")
    @ApiOperation("给讨论组邀请医生，添加医助")
    public R addMemberToGroup(@RequestBody @Validated ConsultationGroupMemberAddSomeDTO memberAddSomeDTO) {

        String nursingIMAccount = memberAddSomeDTO.getNursingIMAccount();
        List<String> doctorOpenId = memberAddSomeDTO.getDoctorOpenId();
        if (StringUtils.isEmpty(nursingIMAccount) && CollUtil.isEmpty(doctorOpenId)) {
            return R.fail("医助ID和医生openId不能同时为空");
        }
        baseService.addMemberToGroup(memberAddSomeDTO);
        return R.success();
    }



    @ApiOperation("病历讨论各状态下列表")
    @PostMapping("consultationGroupPage")
    public R<IPage<ConsultationGroup>> consultationGroupPage(@RequestBody @Validated PageParams<ConsultationGroupPage> pageParams) {
        ConsultationGroupPage model = pageParams.getModel();
        IPage<ConsultationGroup> page = pageParams.buildPage();
        baseService.consultationGroupPage(page, model);
        List<ConsultationGroup> pageRDataRecords = page.getRecords();
        Long userId = model.getUserId();
        String userRole = model.getUserRole();
        if (CollectionUtils.isNotEmpty(pageRDataRecords)) {
            List<Long> collect = pageRDataRecords.stream().map(ConsultationGroup::getId).collect(Collectors.toList());
            R<Map<Long, Integer>> noReadMessage = groupImApi.countNoReadMessage(userId, userRole, collect);
            if (noReadMessage.getIsSuccess() != null && noReadMessage.getIsSuccess()) {
                Map<Long, Integer> data = noReadMessage.getData();
                if (data != null) {
                    for (ConsultationGroup consultationGroup : pageRDataRecords) {
                        consultationGroup.setNoReadMessage(data.get(consultationGroup.getId()));
                    }
                }

            }
        }
        return R.success(page);
    }


    @PostMapping("acceptOrReject/invite")
    @ApiOperation("接受或者拒绝邀请")
    public R memberAcceptOrReject(@RequestBody @Validated ConsultationGroupAcceptOrReject groupAcceptOrReject) {

        Long groupId = groupAcceptOrReject.getGroupId();
        String imAccount = groupAcceptOrReject.getImAccount();
        String action = groupAcceptOrReject.getAction();

        baseService.memberAcceptOrReject(groupId, imAccount, action, groupAcceptOrReject.getRejectMessage());
        return R.success();
    }

    @ApiOperation("删除拒绝的邀请")
    @PostMapping("deleteRejectMember")
    public R deleteRejectMember(@RequestParam String doctorImAccount,
                                @RequestParam Long groupId) {

        baseService.deleteRejectMember(groupId, doctorImAccount);
        return R.success();
    }

    @ApiOperation("查询会诊组详情，并排序成员")
    @GetMapping("getConsultationGroupDetail/{consultationGroupId}")
    public R<ConsultationGroup> getConsultationGroupDetail(@PathVariable("consultationGroupId") Long consultationGroupId,
                                                           @RequestParam(required = false, name = "openId") String openId,
                                                           @RequestParam(required = false, name = "openId") String mobile,
                                                           @RequestParam(required = false, name = "imAccount") String imAccount) {
        ConsultationGroup consultationGroup = baseService.getConsultationGroupDetail(consultationGroupId, openId, imAccount);
        return R.success(consultationGroup);

    }


    @ApiOperation("使用手机号扫码进入群组")
    @PostMapping("/anno/joinGroupFromQrCode")
    public R<JSONObject> annoJoinGroupFromQrCode(
            @Validated @RequestBody ConsultationGroupMemberDto consultationGroupMemberDto) {

        R<ConsultationGroupMember> groupMemberR = joinGroupFromQrCode(consultationGroupMemberDto);
        if (groupMemberR.getIsSuccess()) {
            ConsultationGroupMember memberRData = groupMemberR.getData();
            // 进行登录授权
            R<String> oauthTokenR = oauthApi.consultationGroupMemberLogin(memberRData);
            String oauthToken = oauthTokenR.getData();
            JSONObject jsonObject = JSONObject.parseObject(oauthToken);
            jsonObject.put("ConsultationGroupMember", memberRData);
            return R.success(jsonObject);
        } else {
            return R.fail(groupMemberR.getMsg());
        }

    }




    // TODO: 此功能 前端 需要调整用户的识别方式 需要从openId 改为 手机号
    @ApiOperation("扫码后加入群组")
    @PostMapping("joinGroupFromQrCode")
    public R<ConsultationGroupMember> joinGroupFromQrCode(
            @Validated @RequestBody ConsultationGroupMemberDto consultationGroupMemberDto) {

        String memberOpenId = consultationGroupMemberDto.getMemberOpenId();
        String mobile = consultationGroupMemberDto.getMobile();
        if (StringUtils.isEmpty(memberOpenId) && StringUtils.isEmpty(mobile)) {
            return R.fail("openId和手机号不能都为空");
        }
        ConsultationGroup group = baseService.getById(consultationGroupMemberDto.getConsultationGroupId());
        if (Objects.isNull(group)) {
            throw new BizException("群组不存在");
        }
        String groupImApiError = "添加成员到环信群组失败";
        ConsultationGroupMember consultationGroupMember = null;
        if (StringUtils.isNotEmptyString(memberOpenId)) {
            consultationGroupMember =
                    baseService.getConsultationMemberByOpenId(consultationGroupMemberDto.getConsultationGroupId(), memberOpenId);
        } else {
            consultationGroupMember =
                    baseService.getConsultationMemberByMobile(consultationGroupMemberDto.getConsultationGroupId(), consultationGroupMemberDto.getMobile());
        }

        // 一直没有加入过群组。执行首次加入群组业务
        if (consultationGroupMember == null) {
            Doctor doctor = doctorService.getBaseInfoByOpenId(memberOpenId);
            String guestImAccount;
            if (Objects.nonNull(doctor) && StrUtil.isNotEmpty(doctor.getImAccount())) {
                guestImAccount = doctor.getImAccount();
            } else {
                guestImAccount = imService.registerAccount(IM_GUEST_PREFIX, memberOpenId);
            }
            consultationGroupMember = new ConsultationGroupMember();
            BeanUtils.copyProperties(consultationGroupMemberDto, consultationGroupMember);
            consultationGroupMember.setMemberImAccount(guestImAccount);
            consultationGroupMember.setMemberStatus(ConsultationGroupMember.JOINED);
            baseService.saveMember(consultationGroupMember);
            try {
                groupImApi.addUserToGroup(group.getImGroupId(), guestImAccount, false);
            } catch (Exception e) {
                log.info(groupImApiError);
            }
        }
        BeanUtils.copyProperties(consultationGroupMemberDto, consultationGroupMember);

        // 已经加入过。现在重新再次加入
        if (consultationGroupMember.getMemberStatus() == ConsultationGroupMember.REMOVED
            ||  consultationGroupMember.getMemberStatus() == ConsultationGroupMember.INVITE
            ||  consultationGroupMember.getMemberStatus() == ConsultationGroupMember.REFUSE
            ||  consultationGroupMember.getMemberStatus() == ConsultationGroupMember.CANNOT_REMOVED
                || consultationGroupMember.getMemberStatus() == ConsultationGroupMember.SCAN_CODE_DID_NOT_JOIN) {
            try {
                groupImApi.addUserToGroup(group.getImGroupId(), consultationGroupMember.getMemberImAccount(), false);
            } catch (Exception e) {
                log.info(groupImApiError);
            }
            consultationGroupMember.setMemberStatus(ConsultationGroupMember.JOINED);
            baseService.updateMemberById(consultationGroupMember);
        }

        // 异步发送一条 扫码进入群组的 消息通知
        asyncSendGuestChangeGroupImMessage(consultationGroupMember, ConsultationConstant.GUEST_JOIN_GROUP_EVENT, BaseContextHandler.getTenant());

        return R.success(consultationGroupMember);
    }


    protected void asyncSendGuestChangeGroupImMessage(ConsultationGroupMember consultationGroupMember, String guestChangeType, String tenantCode) {
        sendGuestChangeGroupImMessage(consultationGroupMember, guestChangeType, tenantCode);
    }

    protected void sendGuestChangeGroupImMessage(ConsultationGroupMember consultationGroupMember, String guestChangeType, String tenantCode){
        BaseContextHandler.setTenant(tenantCode);
        ConsultationMessageDto consultationMessageDto = ConsultationMessageDto.builder()
                .content(guestChangeType)
                .type(MediaType.text)
                .build();
        sendGroupMessage(consultationGroupMember.getMemberImAccount(),
                consultationGroupMember.getConsultationGroupId(),
                consultationMessageDto);
    }



    @ApiOperation("移除群组中成员")
    @GetMapping("removeMemberFormImGroup/{memberId}")
    public R<ConsultationGroupMember> removeMemberFormImGroup(@PathVariable("memberId") Long memberId) {

        ConsultationGroupMember groupMember = baseService.getMemberById(memberId, false);
        if (groupMember == null) {
            return R.fail("成员不存在");
        }
        if (groupMember.getMemberStatus() == ConsultationGroupMember.REMOVED) {
            return R.success(groupMember);
        }
        ConsultationGroup group = baseService.getById(groupMember.getConsultationGroupId());
        if (group == null) {
            return R.fail("群组不存在");
        }

        // 发送人员推送群组消息
        sendGuestChangeGroupImMessage(groupMember, ConsultationConstant.GUEST_DROP_OUT_GROUP_EVENT, BaseContextHandler.getTenant());

        // 这是成员状态为 移除状态
        groupMember.setMemberStatus(ConsultationGroupMember.REMOVED);
        baseService.updateMemberById(groupMember);
        try {

            // 从环信群组中移除此成员
            groupImApi.removeUserFromGroup(group.getImGroupId(), groupMember.getMemberImAccount());
        } catch (Exception e) {
            log.info("环信移除群组成员失败");
        }

        return R.success(groupMember);
    }


    @ApiOperation("使用微信用户信息查询或生产一个成员")
    @PostMapping("findConsultationGroupMember/{groupId}")
    public R<ConsultationGroupMember> findConsultationGroupMember(@PathVariable("groupId") Long groupId,
                                                                  @RequestBody WxOAuth2UserInfo wxOAuth2UserInfo) {

        ConsultationGroupMember groupMember = baseService.getConsultationMemberByOpenId(groupId, wxOAuth2UserInfo.getOpenid());
        if (groupMember == null) {
            groupMember = new ConsultationGroupMember();

            // version 2.2BUG ， 2.3版本依然使用 //使用openId 查询是否是系统内的医生, 是则使用医生的名字，头像，IM账号
            Doctor doctor = doctorService.getBaseInfoByOpenId(wxOAuth2UserInfo.getOpenid());
            if (Objects.nonNull(doctor)) {
                groupMember.setMemberName(doctor.getName());
                groupMember.setMemberAvatar(doctor.getAvatar());
                groupMember.setMemberImAccount(doctor.getImAccount());
                groupMember.setMemberRole(ConsultationGroupMember.ROLE_DOCTOR);
                groupMember.setMemberRoleRemarks("医生");
                groupMember.setReceiveWeiXinTemplate(ConsultationGroupMember.RECEIVE_WEIXIN_TEMPLATE_YES);
            } else {
                groupMember.setMemberName(wxOAuth2UserInfo.getNickname());
                groupMember.setMemberAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                groupMember.setReceiveWeiXinTemplate(ConsultationGroupMember.RECEIVE_WEIXIN_TEMPLATE_NO);
            }
            groupMember.setMemberStatus(ConsultationGroupMember.SCAN_CODE_DID_NOT_JOIN);
            groupMember.setConsultationGroupId(groupId);
            if (StringUtils.isEmpty(groupMember.getMemberImAccount())) {
                String guestImAccount = imService.registerAccount(IM_GUEST_PREFIX, wxOAuth2UserInfo.getOpenid());
                groupMember.setMemberImAccount(guestImAccount);
            }
            groupMember.setMemberOpenId(wxOAuth2UserInfo.getOpenid());
            baseService.saveMember(groupMember);
        } else {
            groupMember.setMemberOpenId(wxOAuth2UserInfo.getOpenid());
            if (StringUtils.isEmpty(groupMember.getMemberName())) {
                groupMember.setMemberName(wxOAuth2UserInfo.getNickname());
            }
            if (StringUtils.isEmpty(groupMember.getMemberAvatar())) {
                groupMember.setMemberAvatar(wxOAuth2UserInfo.getHeadImgUrl());
            }
            baseService.updateMemberById(groupMember);
        }
        return R.success(groupMember);

    }

    @ApiOperation("微信授权成功后，使用授权信息中的用户ID拉取聊天成员的信息")
    @GetMapping("findConsultationGroupMember/{memberId}")
    public R<ConsultationGroupMember> findConsultationGroupMember(@PathVariable("memberId") Long memberId) {

        ConsultationGroupMember groupMember = baseService.getMemberById(memberId, true);


        return R.success(groupMember);

    }

    @ApiOperation("删除会诊小组")
    @GetMapping("deleteConsultation/{groupId}")
    public R<Boolean> deleteConsultation(@PathVariable("groupId") Long groupId) {

        ConsultationGroup group = baseService.deleteAllByGroupId(groupId);
        if (Objects.nonNull(group)) {
            try {
                groupImApi.deleteImGroup(group.getImGroupId());
                groupImApi.deleteImGroupMessage(group.getId());
            } catch (Exception e) {
                log.info("删除环信群组或删除群组聊天记录失败");
            }
        }
        return R.success();

    }

    @ApiOperation("结束会诊小组")
    @GetMapping("theEndConsultation/{groupId}")
    public R<Boolean> theEndConsultation(@PathVariable("groupId") Long groupId) {

        ConsultationGroup group = baseService.theEndConsultation(groupId);

        try {
            groupImApi.deleteImGroup(group.getImGroupId());
        } catch (Exception e) {
            log.info("删除环信群组失败");
        }
        return R.success();

    }

    @ApiOperation("来宾主动退出会诊小组")
    @GetMapping("guestLeaveConsultation/{memberId}")
    public R<Boolean> guestLeaveConsultation(@PathVariable("memberId") Long memberId) {

        ConsultationGroupMember groupMember = baseService.getMemberById(memberId, true);
        if (ConsultationGroupMember.REMOVED == groupMember.getMemberStatus()) {
            return R.success(true);
        }
        ConsultationGroup group = groupMember.getGroup();

        sendGuestChangeGroupImMessage(groupMember, ConsultationConstant.GUEST_DROP_OUT_GROUP_EVENT, BaseContextHandler.getTenant());

        groupMember.setMemberStatus(ConsultationGroupMember.REMOVED);
        baseService.updateMemberById(groupMember);
        try {
            groupImApi.removeUserFromGroup(group.getImGroupId(), groupMember.getMemberImAccount());
        } catch (Exception e) {
            log.info("来宾主动退出环信群组失败");
        }
        return R.success();

    }



    @ApiOperation("发送消息到群组中去")
    @PostMapping("sendGroupMessage/{senderImAccount}/{groupId}")
    public R<ConsultationChat> sendGroupMessage(@PathVariable("senderImAccount") String senderImAccount,
                                                @PathVariable("groupId") Long groupId,
                                                @RequestBody ConsultationMessageDto consultationMessageDto) {

        ConsultationGroup group = baseService.getById(groupId);
        if (Objects.isNull(group)) {
            return R.fail("该会诊已经被删除");
        }
        if (ConsultationGroup.FINISH.equals(group.getConsultationStatus())) {
            return R.fail("该会诊已经结束");
        }
        ConsultationGroupMember groupMember = baseService.getConsultationMemberByImAccount(groupId, senderImAccount);
        ConsultationChat consultationChat = ConsultationChat.builder()
                .content(consultationMessageDto.getContent())
                .type(consultationMessageDto.getType())

                .imGroupId(group.getImGroupId())
                .groupId(group.getId())

                .senderId(groupMember.getId())
                .senderAvatar(groupMember.getMemberAvatar())
                .senderRoleType(groupMember.getMemberRole())
                .senderRoleRemark(groupMember.getMemberRoleRemarks())
                .senderName(groupMember.getMemberName())
                .senderImAccount(groupMember.getMemberImAccount())
                .build();

        // 查询群组中 除我之外的 医助和系统医生
        List<ConsultationGroupMember> groupMembers = baseService.getConsultationMemberNoImAccount(groupId, senderImAccount);
        boolean sendJPush = false;
        if (CollUtil.isNotEmpty(groupMembers)) {
            List<ConsultationMessageStatus> messageStatuses = new ArrayList<>();
            for (ConsultationGroupMember member : groupMembers) {
                if (member.getMemberRole().equals(UserType.UCENTER_NURSING_STAFF)) {
                    sendJPush = true;
                    consultationChat.setReceiverId(member.getMemberUserId());
                    consultationChat.setReceiverIm(member.getMemberImAccount());
                }
                messageStatuses.add(ConsultationMessageStatus.builder()
                        .receiverRoleType(member.getMemberRole())
                        .groupId(groupId)
                        .status(2)
                        .receiverId(member.getMemberUserId())
                        .build());
            }
            consultationChat.setMessageStatuses(messageStatuses);
        }
        consultationChat.setSendJPush(sendJPush);

        if (Objects.nonNull(group.getNurseId())) {
            Long nurseId = group.getNurseId();
            NursingStaff nursingStaff = nursingStaffService.getById(nurseId);
            if (Objects.nonNull(nursingStaff)) {
                consultationChat.setReceiverId(nursingStaff.getId());
                consultationChat.setReceiverIm(nursingStaff.getImAccount());
            }
        }
        R<ConsultationChat> consultationChatR = groupImApi.sendConsultationChat(consultationChat);
        if (consultationChatR.getIsSuccess() != null && consultationChatR.getIsSuccess()) {
            try {
                baseService.handlerSendMessage(group, BaseContextHandler.getTenant());
            } catch (Exception e) {
                log.info("给患者发送模板消息失败");
            }
        }
        return R.success(consultationChat);
    }



    @ApiOperation("查询小组中人员信息")
    @GetMapping("groupMember/{groupId}")
    public R<ConsultationGroupMember> groupMember(@PathVariable("groupId")  Long groupId, @RequestParam("imAccount") String imAccount) {

        ConsultationGroupMember groupMember = baseService.groupMember(groupId, imAccount);
        return R.success(groupMember);
    }



    @ApiOperation("无授权请求讨论组基本信息")
    @GetMapping("anno/getGroup/{id}")
    public R<ConsultationGroup> annoGet(@PathVariable("id")  Long id) {
        ConsultationGroup group = baseService.getByIdNoTenant(id);
        return R.success(group);
    }
}
