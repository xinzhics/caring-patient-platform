package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "u_user_patient", autoResultMap = true)
@ApiModel(value = "Patient", description = "患者表")
@AllArgsConstructor
public class Patient extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    /*** 关注*/
    public static final int PATIENT_SUBSCRIBE = 0;

    /** 关注 且 入组完成*/
    public static final int PATIENT_SUBSCRIBE_NORMAL = 1;

    /** 取关 */
    public static final int PATIENT_NO_SUBSCRIBE = 2;


    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @TableField(value = "service_advisor_id", condition = EQUAL)
    @Excel(name = "医助ID")
    private Long serviceAdvisorId;

    /**
     * 医助名称
     */
    @ApiModelProperty(value = "医助名称")
    @Length(max = 50, message = "医助名称长度不能超过50")
    @TableField(value = "service_advisor_name", condition = LIKE)
    @Excel(name = "医助名称")
    private String serviceAdvisorName;

    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "微信昵称")
    @Length(max = 180, message = "微信昵称长度不能超过180")
    @TableField(value = "nick_name", condition = LIKE)
    @Excel(name = "微信昵称")
    private String nickName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 50, message = "手机号码长度不能超过50")
    @TableField(value = "mobile", condition = EQUAL, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "手机号码")
    private String mobile;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别，0男，1女")
    @TableField("sex")
    private Integer sex;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    @Length(max = 180, message = "真实姓名长度不能超过180")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "真实姓名")
    private String name;

    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    @TableField(value = "birthday", condition = LIKE)
    @Excel(name = "出生年月")
    private String birthday;

    /**
     * 小组ID
     */
    @Deprecated
    @ApiModelProperty(value = "小组ID")
    @TableField(value = "group_id", condition = EQUAL)
    @Excel(name = "小组ID")
    private Long groupId;

    /**
     * 小组名
     */
    @Deprecated
    @ApiModelProperty(value = "小组名")
    @Length(max = 100, message = "小组名长度不能超过100")
    @TableField(value = "group_name", condition = LIKE)
    @Excel(name = "小组名")
    private String groupName;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    @Length(max = 50, message = "医生名称长度不能超过50")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "医生名称")
    private String doctorName;

    /**
     * 会员状态（0:关注  1：正常  2：取关 )
     * 会员状态（0:未注册  1：已注册  2：取关 )
     */
    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关)")
    @TableField(value = "status_", condition = EQUAL)
    @Excel(name = "会员状态（0:关注  1：正常  2：取关)")
    private Integer status;

    /**
     * 诊断类型ID
     */
    @ApiModelProperty(value = "诊断类型ID")
    @TableField(value = "diagnosis_id", condition = EQUAL)
    @Excel(name = "诊断类型ID")
    private String diagnosisId;

    /**
     * 诊断类型名称
     */
    @ApiModelProperty(value = "诊断类型名称")
    @Length(max = 255, message = "诊断类型名称长度不能超过255")
    @TableField(value = "diagnosis_name", condition = LIKE)
    @Excel(name = "诊断类型名称")
    private String diagnosisName;


    @ApiModelProperty(value = "随访阶段ID")
    @TableField(value = "follow_stage_id", condition = EQUAL)
    private String followStageId;

    @ApiModelProperty(value = "随访阶段名称")
    @TableField(value = "follow_stage_name", condition = LIKE)
    private String followStageName;

    /**
     * 所属公众号ID
     */
    @ApiModelProperty(value = "所属公众号ID")
    @Length(max = 32, message = "所属公众号ID长度不能超过32")
    @TableField(value = "wx_app_id", condition = LIKE)
    @Excel(name = "所属公众号ID")
    private String wxAppId;

    /**
     * 微信openid
     */
    @ApiModelProperty(value = "微信openid")
    @Length(max = 32, message = "微信openid长度不能超过32")
    @TableField(value = "open_id", condition = EQUAL)
    @Excel(name = "微信openid")
    private String openId;

    @ApiModelProperty(value = "导入其他公众号的openId")
    @TableField(value = "import_wx_open_id", condition = EQUAL)
    private String importWxOpenId;

    /**
     * 微信union_id
     */
    @ApiModelProperty(value = "微信union_id")
    @Length(max = 32, message = "微信union_id长度不能超过32")
    @TableField(value = "union_id", condition = EQUAL)
    @Excel(name = "微信unionId")
    private String unionId;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Length(max = 250, message = "头像长度不能超过250")
    @TableField(value = "avatar", condition = LIKE)
    @Excel(name = "头像")
    private String avatar;

    /**
     * 环信账号
     */
    @ApiModelProperty(value = "环信账号")
    @Length(max = 255, message = "环信账号长度不能超过255")
    @TableField(value = "im_account", condition = LIKE)
    @Excel(name = "环信账号")
    private String imAccount;

    @ApiModelProperty(value = "")
    @Length(max = 180, message = "长度不能超过180")
    @TableField(value = "remark", condition = LIKE)
    @Excel(name = "")
    private String remark;

    /**
     * 是否完成入组信息（0：否  1：是）
     */
    @ApiModelProperty(value = "是否完成入组信息（0：否  1：是）")
    @TableField("is_complete_enter_group")
    @Excel(name = "是否完成入组信息（0：否  1：是）")
    private Integer isCompleteEnterGroup;

    /**
     * 随访计划完成次数
     */
    @Deprecated
    @ApiModelProperty(value = "随访计划完成次数")
    @TableField("examine_count")
    @Excel(name = "随访计划完成次数")
    private Integer examineCount;

    /**
     * 病分期
     */
    @ApiModelProperty(value = "病分期")
    @TableField("ckd")
    @Excel(name = "病分期")
    private Integer ckd;

    /**
     * 随访计划开始时间
     */
    @ApiModelProperty(value = "随访计划开始时间")
    @TableField("nursing_time")
    @Excel(name = "随访计划开始时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime nursingTime;

    /**
     * 医院名称
     */
    @ApiModelProperty(value = "医院名称")
    @Length(max = 100, message = "医院名称长度不能超过100")
    @TableField(value = "hospital_name", condition = LIKE)
    @Excel(name = "医院名称")
    private String hospitalName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    @Length(max = 50, message = "身份证号长度不能超过50")
    @TableField(value = "certificate_no", condition = LIKE)
    @Excel(name = "身份证号")
    private String certificateNo;

    /**
     * 会员标识码
     */
    @ApiModelProperty(value = "会员标识码")
    @Length(max = 32, message = "会员标识码长度不能超过32")
    @TableField(value = "code", condition = LIKE)
    @Excel(name = "会员标识码")
    private String code;

    /**
     * 是否同意 入组协议 （0； 同意 1 ）
     */
    @ApiModelProperty(value = "是否同意 入组协议 （0； 同意 1 ）")
    @TableField("agree_agreement")
    @Excel(name = "是否同意 入组协议 （0； 同意 1 ）")
    private Integer agreeAgreement;

    @ApiModelProperty(value = "疾病信息的填写状态。 0 未填写， 1 已填写")
    @TableField("disease_information_status")
    private Integer diseaseInformationStatus;

    /**
     * 用户累计打卡天数
     */
    @Deprecated
    @ApiModelProperty(value = "用户累计打卡天数")
    @TableField("grand_total_check")
    @Excel(name = "用户累计打卡天数")
    private Integer grandTotalCheck;

    /**
     * 用户连续打卡天数
     */
    @Deprecated
    @ApiModelProperty(value = "用户连续打卡天数")
    @TableField("successive_check")
    @Excel(name = "用户连续打卡天数")
    private Integer successiveCheck;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    @TableField("height")
    @Excel(name = "身高")
    private Integer height;

    /**
     * 体重
     */
    @ApiModelProperty(value = "体重")
    @TableField("weight")
    @Excel(name = "体重")
    private Integer weight;

    /**
     * 所属机构ID
     */
    @ApiModelProperty(value = "所属机构ID")
    @TableField(value = "org_id")
    @Excel(name = "所属机构ID")
    private Long organId;

    /**
     * 所属机构代码
     */
    @ApiModelProperty(value = "所属机构代码")
    @Length(max = 200, message = "所属机构代码长度不能超过200")
    @TableField(value = "organ_code", condition = LIKE)
    @Excel(name = "所属机构代码")
    private String organCode;

    /**
     * 所属机构代码
     */
    @ApiModelProperty(value = "所属机构代码")
    @Length(max = 200, message = "所属机构代码长度不能超过200")
    @TableField(value = "class_code", condition = LIKE)
    @Excel(name = "所属机构代码")
    private String classCode;

    /**
     * 所属机构名称
     */
    @ApiModelProperty(value = "所属机构名称")
    @Length(max = 50, message = "所属机构名称长度不能超过50")
    @TableField(value = "organ_name", condition = LIKE)
    @Excel(name = "所属机构名称")
    private String organName;

    /**
     * 最后登录IP
     */
    @ApiModelProperty(value = "最后登录IP")
    @Length(max = 20, message = "最后登录IP长度不能超过20")
    @TableField(value = "last_login_ip", condition = LIKE)
    @Excel(name = "最后登录IP")
    private String lastLoginIp;

    /**
     * 最后登录时间，时间戳类型
     */
    @ApiModelProperty(value = "最后登录时间，时间戳类型")
    @TableField("last_login_time")
    @Excel(name = "最后登录时间，时间戳类型")
    private Long lastLoginTime;

    /**
     * 登录次数
     */
    @ApiModelProperty(value = "登录次数")
    @TableField("total_login_times")
    @Excel(name = "登录次数")
    private Integer totalLoginTimes;

    /**
     * 用户额外信息
     * TODO: 好像没用过 (杨帅)。
     */
    @Deprecated
    @ApiModelProperty(value = "用户额外信息")
    @TableField("extra_info")
    @Excel(name = "用户额外信息")
    private String extraInfo;

    /**
     * 入组时间
     */
    @ApiModelProperty(value = "入组时间")
    @TableField("complete_enter_group_time")
    @Excel(name = "入组时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime completeEnterGroupTime;

    @ApiModelProperty(value = "是否展开聊天小组, 0 关闭，1展示")
    @TableField(value = "im_group_status", condition = EQUAL)
    private Integer imGroupStatus;

    @ApiModelProperty(value = "医助是否展开聊天小组, 0 关闭，1展示")
    @TableField(value = "nursing_staff_im_group_status", condition = EQUAL)
    private Integer nursingStaffImGroupStatus;


    @ApiModelProperty(value = "医助是否退出聊天小组, 0 未退出，1 退出")
    @TableField(value = "nursing_exit_chat", condition = EQUAL)
    private Integer nursingExitChat;


    @ApiModelProperty(value = "医生是否退出聊天小组, 0 未退出，1 退出")
    @TableField(exist = false)
    private Integer doctorExitChat;

    @ApiModelProperty(value = "医生退出聊天的ID的集合转JSON")
    @TableField(value = "doctor_exit_chat_list_json", condition = EQUAL)
    private String doctorExitChatListJson;

    @ApiModelProperty(value = "医生备注")
    @TableField("doctor_remark")
    @Excel(name = "医生备注", width = 20)
    private String doctorRemark;

    @ApiModelProperty(value = "医生是否展开聊天小组, 0 关闭，1展示")
    @TableField(value = "doctor_im_group_status", condition = EQUAL)
    private Integer doctorImGroupStatus;

    // 用户每次发送消息，认为用户在线。 用户有消息超过5分钟未读，认为用户离线。
    @Deprecated
    @ApiModelProperty(value = "im在线状态")
    @TableField(value = "online")
    private Integer online;

    @ApiModelProperty(value = "取关时间")
    @TableField(value = "un_subscribe_time")
    private LocalDateTime unSubscribeTime;

    @ApiModelProperty(value = "名字第一个字的首字母")
    @TableField(value = "name_first_letter")
    private String nameFirstLetter;

    @ApiModelProperty(value = "是否有聊天")
    @TableField(value = "has_send_chat")
    private Boolean hasSendChat;

    // UPDATE u_user_patient as p set p.default_doctor_patient = (SELECT d.build_in from u_user_doctor as d where d.id = p.doctor_id);
    @ApiModelProperty(value = "默认医生的患者")
    @TableField(value = "default_doctor_patient")
    private Boolean defaultDoctorPatient;

    @ApiModelProperty(value = "患者密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;
}
