package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.constant.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName MeetingGroupMember
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 9:43
 * @Version 1.0
 */
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_consultation_group_member")
@ApiModel(value = "ConsultationGroupMember", description = "会诊组成员")
@AllArgsConstructor
public class ConsultationGroupMember extends Entity<Long> {

    /**
     *  不可移除
     */
    public static final int CANNOT_REMOVED = -1;

    /**
     * 医生角色在 病历讨论 中的人员角色
     */
    public static final String ROLE_DOCTOR = "roleDoctor";

    /**
     * 拒绝加入
     */
    public static final int REFUSE = -3;

    /**
     * 邀请，待加入
     */
    public static final int INVITE = -2;

    /**
     * 扫码未加入
     */
    public static final int SCAN_CODE_DID_NOT_JOIN = 0;

    /**
     * 已加入
     */
    public static final int JOINED = 1;

    /**
     * 移除或者离开或者对于讨论组结束
     */
    public static final int REMOVED = 2;

    /**
     * 接受微信模板推送
     */
    public static final int RECEIVE_WEIXIN_TEMPLATE_YES = 1;

    /**
     * 不接受微信模板推送
     */
    public static final int RECEIVE_WEIXIN_TEMPLATE_NO = 0;

    @ApiModelProperty(value = "会诊组ID")
    @TableField(value = "consultation_group_id", condition = EQUAL)
    @Excel(name = "会诊组ID")
    private Long consultationGroupId;


    @ApiModelProperty(value = "成员用户ID(医助ID，医生ID)")
    @TableField(value = "member_user_id")
    private Long memberUserId;


    @ApiModelProperty(value = "成员IM账号")
    @TableField(value = "member_im_account")
    @Excel(name = "成员IM账号")
    private String memberImAccount;

    @ApiModelProperty(value = "成员手机号")
    @TableField(value = "mobile")
    private String mobile;


    @ApiModelProperty(value = "成员的openId")
    @Length(max = 255, message = "成员的openId")
    @TableField(value = "member_open_id", condition = EQUAL)
    @Excel(name = "成员OpenId")
    private String memberOpenId;

    @ApiModelProperty(value = "成员头像")
    @Length(max = 500, message = "成员头像长度不能超过20")
    @TableField(value = "member_avatar", condition = LIKE)
    @Excel(name = "成员头像")
    private String memberAvatar;

    @ApiModelProperty(value = "成员姓名")
    @Length(max = 255, message = "成员姓名长度不能超过20")
    @TableField(value = "member_name", condition = LIKE)
    @Excel(name = "成员姓名")
    private String memberName;

    @ApiModelProperty(value = "成员角色")
    @Length(max = 255, message = "成员角色长度不能超过20")
    @TableField(value = "member_role", condition = EQUAL)
    @Excel(name = "成员角色")
    private String memberRole;

    @ApiModelProperty(value = "成员角色备注")
    @Length(max = 255, message = "成员角色备注长度不能超过20")
    @TableField(value = "member_role_remarks", condition = EQUAL)
    @Excel(name = "成员角色备注")
    private String memberRoleRemarks;


    @ApiModelProperty(value = "成员状态(-1 不可移除，0 扫码未加入 1 加入状态， 2 已移除， -2 邀请中, -3 拒绝 )")
    @Length(max = 255, message = "成员状态名字长度不能超过20")
    @TableField(value = "member_status", condition = EQUAL)
    @Excel(name = "成员状态")
    private Integer memberStatus;

    @ApiModelProperty(value = "拒绝原因")
    @TableField(value = "member_refuse_message")
    private String memberRefuseMessage;

    @ApiModelProperty(value = "患者ID(只有角色是患者的时候此值才有)")
    @TableField(value = "patient_id", condition = EQUAL)
    private Long patientId;

    @ApiModelProperty(value = "邀请人ID")
    @TableField(value = "invite_people", condition = EQUAL)
    private Long invitePeople;

    @ApiModelProperty(value = "邀请人角色 医助 或 医生")
    @TableField(value = "invite_people_role", condition = EQUAL)
    private String invitePeopleRole;

    @ApiModelProperty(value = "是否接收微信的模板消息 1 是,  0 否")
    @TableField(value = "receive_weixin_template", condition = EQUAL)
    private Integer receiveWeiXinTemplate;

    @TableField(exist = false)
    private Boolean isMe;

    @TableField(exist = false)
    private ConsultationGroup group;

    @ApiModelProperty(value = "当前登陆人和患者医生id相同才返回")
    @TableField(exist = false)
    private String doctorRemark;

    @ApiModelProperty(value = "当前登陆人和患者医助id相同才返回")
    @TableField(exist = false)
    private String nursingRemark;

    public ConsultationGroupMember() {
    }

    public ConsultationGroupMember(Long consultationGroupId, Patient patient, String memberRoleRemarks) {
        this(consultationGroupId, patient.getImAccount(), patient.getId(), null, patient.getOpenId(), patient.getAvatar(), patient.getName(),
                UserType.UCENTER_PATIENT, StrUtil.isEmpty(memberRoleRemarks) ? "患者" : memberRoleRemarks , CANNOT_REMOVED);
        this.receiveWeiXinTemplate = RECEIVE_WEIXIN_TEMPLATE_YES;
        this.mobile = patient.getMobile();
    }

    public ConsultationGroupMember(Long consultationGroupId, Doctor doctor, String memberRoleRemarks) {
        this(consultationGroupId, doctor, CANNOT_REMOVED, memberRoleRemarks);
    }

    public ConsultationGroupMember(Long consultationGroupId, Doctor doctor, Integer memberStatus, String memberRoleRemarks) {
        this(consultationGroupId, doctor.getImAccount(), null, doctor.getId(), doctor.getOpenId(), doctor.getAvatar(), doctor.getName(),
                ROLE_DOCTOR, StrUtil.isEmpty(memberRoleRemarks) ? "医生" : memberRoleRemarks , memberStatus);
        this.receiveWeiXinTemplate = RECEIVE_WEIXIN_TEMPLATE_YES;
        this.mobile = doctor.getMobile();
    }

    public ConsultationGroupMember(Long consultationGroupId, NursingStaff nursingStaff, String memberRoleRemarks) {
        this(consultationGroupId, nursingStaff, CANNOT_REMOVED, memberRoleRemarks);
        this.receiveWeiXinTemplate = RECEIVE_WEIXIN_TEMPLATE_NO;
    }

    public ConsultationGroupMember(Long consultationGroupId, NursingStaff nursingStaff, Integer memberStatus, String memberRoleRemarks) {
        this(consultationGroupId, nursingStaff.getImAccount(), null , nursingStaff.getId(), "", nursingStaff.getAvatar(), nursingStaff.getName(),
                UserType.UCENTER_NURSING_STAFF, StrUtil.isEmpty(memberRoleRemarks) ? "医助" : memberRoleRemarks , memberStatus);
        this.receiveWeiXinTemplate = RECEIVE_WEIXIN_TEMPLATE_NO;
        this.mobile = nursingStaff.getMobile();
    }

    public ConsultationGroupMember(Long consultationGroupId, String memberImAccount, Long patientId, Long memberUserId, String memberOpenId,
                                   String memberAvatar, String memberName, String memberRole,  String memberRoleRemarks, Integer memberStatus) {
        this.consultationGroupId = consultationGroupId;
        this.memberImAccount = memberImAccount;
        this.patientId = patientId;
        this.memberOpenId = memberOpenId;
        this.memberAvatar = memberAvatar;
        this.memberName = memberName;
        this.memberRole = memberRole;
        this.memberRoleRemarks = memberRoleRemarks;
        this.memberStatus = memberStatus;
        this.memberUserId = memberUserId;
    }




}


