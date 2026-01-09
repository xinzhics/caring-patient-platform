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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 医生表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "u_user_doctor", autoResultMap = true)
@ApiModel(value = "Doctor", description = "医生表")
@AllArgsConstructor
public class Doctor extends Entity<Long> {

    public static final String defaultAvtar = "https://caing-test.obs.cn-north-4.myhuaweicloud.com/23a176879c0c5675556687e5718cc82.png";

    private static final long serialVersionUID = 1L;

    /**
     * 医生名字
     */
    @ApiModelProperty(value = "医生名字")
    @Length(max = 32, message = "医生名字长度不能超过32")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "医生名字")
    private String name;

    /**
     * 小组ID
     */
    @Deprecated
    @ApiModelProperty(value = "小组ID（暂时作为接收前端数据使用）")
    @TableField(exist = false)
    private Long groupId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 100, message = "备注长度不能超过100")
    @TableField(value = "remarks", condition = LIKE)
    @Excel(name = "备注")
    private String remarks;

    /**
     * 小组名字
     */
    @ApiModelProperty(value = "小组名字")
    @Length(max = 32, message = "小组名字长度不能超过32")
    @TableField(value = "group_name", condition = LIKE)
    @Excel(name = "小组名字")
    private String groupName;

    /**c
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nursing_id", condition = EQUAL)
    @Excel(name = "医助ID")
    private Long nursingId;

    /**
     * 医助
     */
    @ApiModelProperty(value = "医助")
    @Length(max = 100, message = "医助长度不能超过100")
    @TableField(value = "nursing_name", condition = LIKE)
    @Excel(name = "医助")
    private String nursingName;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @TableField(value = "org_id", condition = EQUAL)
    @Excel(name = "机构ID")
    private Long organId;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @Length(max = 32, message = "省长度不能超过32")
    @TableField(value = "province", condition = LIKE)
    @Excel(name = "省")
    private String province;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    @Length(max = 32, message = "市长度不能超过32")
    @TableField(value = "city", condition = LIKE)
    @Excel(name = "市")
    private String city;

    /**
     * 医院
     */
    @ApiModelProperty(value = "医院")
    @TableField("hospital_id")
    @Excel(name = "医院")
    private Long hospitalId;

    /**
     * 科室
     */
    @ApiModelProperty(value = "科室")
    @Length(max = 32, message = "科室长度不能超过32")
    @TableField(value = "department_id", condition = LIKE)
    @Excel(name = "科室")
    private String departmentId;

    /**
     * 职称
     */
    @ApiModelProperty(value = "职称")
    @Length(max = 32, message = "职称长度不能超过32")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "职称")
    private String title;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 12, message = "手机号码长度不能超过12")
    @TableField(value = "mobile", condition = EQUAL, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "密码是否修改")
    @TableField(value = "password_updated", condition = EQUAL)
    private Integer passwordUpdated;


    @ApiModelProperty(value = "密码")
    @TableField(value = "password", condition = EQUAL)
    private String password;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "登录名")
    @TableField(value = "login_name", condition = EQUAL)
    @Excel(name = "登录名")
    private String loginName;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Length(max = 250, message = "头像长度不能超过250")
    @TableField(value = "avatar", condition = LIKE)
    @Excel(name = "头像")
    private String avatar;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @Length(max = 50, message = "昵称长度不能超过50")
    @TableField(value = "nick_name", condition = LIKE)
    @Excel(name = "昵称")
    private String nickName;

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
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "微信openid")
    private String openId;

    /**
     * 环信账号
     */
    @ApiModelProperty(value = "环信账号")
    @Length(max = 255, message = "环信账号长度不能超过255")
    @TableField(value = "im_account", condition = LIKE)
    @Excel(name = "环信账号")
    private String imAccount;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    @TableField(value = "hospital_name", condition = LIKE)
    @Excel(name = "医院名称")
    private String hospitalName;

    /**
     * 患者关注码
     */
    @ApiModelProperty(value = "患者关注码")
    @Length(max = 1000, message = "患者关注码长度不能超过1000")
    @TableField(value = "qr_code", condition = LIKE)
    @Excel(name = "患者关注码")
    private String qrCode;

    @ApiModelProperty(value = "患者关注码")
    @TableField(value = "english_qr_code", condition = LIKE)
    private String englishQrCode;

    /**
     * 医生名片 跟患者关住码一样的
     */
    @ApiModelProperty(value = "医生名片 跟患者关住码一样的")
    @Length(max = 1000, message = "医生名片 跟患者关住码一样的长度不能超过1000")
    @TableField(value = "business_card_qr_code", condition = LIKE)
    @Excel(name = "医生名片 跟患者关住码一样的")
    private String businessCardQrCode;

    @ApiModelProperty(value = "医生名片 英文版")
    @TableField(value = "english_business_card_qr_code", condition = LIKE)
    private String englishBusinessCardQrCode;

    @ApiModelProperty(value = "医生下载时的二维码路径")
    @TableField(value = "down_load_qr_code")
    private String downLoadQrcode;

    /**
     * 科室名称
     */
    @ApiModelProperty(value = "科室名称")
    @Length(max = 100, message = "科室名称长度不能超过100")
    @TableField(value = "deptartment_name", condition = LIKE)
    @Excel(name = "科室名称")
    private String deptartmentName;

    /**
     * 所属机构代码
     */
    @ApiModelProperty(value = "所属机构代码organCode")
    @Length(max = 200, message = "所属机构代码长度不能超过200")
    @TableField(value = "organ_code", condition = LIKE)
    @Excel(name = "所属机构代码")
    private String organCode;


    /**
     * 所属机构代码
     */
    @ApiModelProperty(value = "所属机构代码classCode")
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
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    @TableField(value = "birthday", condition = LIKE)
    @Excel(name = "出生年月")
    private String birthday;

    /**
     * 最后登录IP
     */
    @ApiModelProperty(value = "最后登录IP")
    @Length(max = 20, message = "最后登录IP长度不能超过20")
    @TableField(value = "last_login_ip", condition = LIKE)
    @Excel(name = "最后登录IP")
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @ApiModelProperty(value = "登录次数")
    @TableField("total_login_times")
    @Excel(name = "登录次数")
    private Integer totalLoginTimes;

    /**
     * 性别 0:男 1：女
     */
    @ApiModelProperty(value = "性别 0:男 1：女")
    @TableField("sex")
    @Excel(name = "性别 0:男 1：女")
    private Integer sex;

    /**
     * 最后登录时间，时间戳类型
     */
    @ApiModelProperty(value = "最后登录时间，时间戳类型")
    @TableField("last_login_time")
    @Excel(name = "最后登录时间，时间戳类型")
    private Long lastLoginTime;

    /**
     * 最后登录时间，时间戳类型
     */
    @ApiModelProperty(value = "首次登录时间")
    @TableField("first_login_time")
    @Excel(name = "首次登录时间")
    private LocalDateTime firstLoginTime;


    @ApiModelProperty(value = "微信关注状态 null 或 0 未关注， 1 关注, 2 取消关注, 3 退出登录")
    @TableField("wx_status")
    private Integer wxStatus;

    @ApiModelProperty(value = "是否展开聊天小组, 0 关闭，1展示")
    @TableField(value = "im_group_status", condition = EQUAL)
    private Integer imGroupStatus;

    @ApiModelProperty(value = "是否接收IM消息 0 不接收, 1接收")
    @TableField(value = "im_msg_status", condition = EQUAL)
    private Integer imMsgStatus;

    @ApiModelProperty(value = "是否接收Im微信模板消息 0 不接收, 1接收")
    @TableField(value = "im_wx_template_status", condition = EQUAL)
    private Integer imWxTemplateStatus;
    /**
     * 扩展内容
     */
    @ApiModelProperty(value = "扩展内容{\"Specialties\":\"\",\"Introduction\":\"\"}专业特长,详细介绍作为一个json对象的属性保存在这个字段。")
    @TableField("extra_info")
    @Excel(name = "扩展内容")
    private String extraInfo;

    @ApiModelProperty(value = "总会员人数")
    @TableField(exist = false)
    private Long totalPatientCount;

    @ApiModelProperty(value = "粉丝人数")
    @TableField(exist = false)
    private Long fansCount;

    @ApiModelProperty(value = "推荐人数")
    @TableField(exist = false)
    private Long commendNum;

    @ApiModelProperty(value = "依从度")
    @TableField(exist = false)
    private Long compliance;

    @ApiModelProperty(value = "小组排名")
    @TableField(exist = false)
    private Integer doctorRanking;

    // 用户每次发送消息，认为用户在线。 用户有消息超过5分钟未读，认为用户离线。
    @Deprecated
    @ApiModelProperty(value = "im在线状态")
    @TableField(value = "online")
    private Integer online;

    @ApiModelProperty(value = "医生所在项目名")
    @TableField(exist = false)
    private String tenantName;

    @ApiModelProperty(value = "医生所在项目CODE")
    @TableField(exist = false, value = "im_wx_template_status")
    private String tenantCode;

    @ApiModelProperty(value = "关闭预约(1 为关闭，0 或者 null 为开启 )")
    @TableField(value = "close_appoint", condition = EQUAL)
    private Integer closeAppoint;

    /**
     * {@link com.caring.sass.common.constant.DoctorAppointmentReviewEnum}
     */
    @ApiModelProperty(value = "审核开关(无须审核：no_review， 需要审核 need_review)")
    @TableField(value = "appointment_review", condition = EQUAL)
    private String appointmentReview;

    @ApiModelProperty(value = "内置医生：0否，1是")
    @TableField(value = "build_in", condition = EQUAL)
    private Integer buildIn;

    @ApiModelProperty(value = "个人服务号 的医生头， 1 是， 0 不是, 患者默认关注在 医生头这里。")
    @TableField(value = "doctor_leader", condition = EQUAL)
    private Integer doctorLeader;

    @ApiModelProperty(value = "独立医生 1 为独立医生， 0 为非独立医生")
    @TableField(value = "independence", condition = EQUAL)
    private Integer independence;

    @ApiModelProperty(value = "拥有自己的小组")
    @TableField(value = "has_group", condition = EQUAL)
    private Integer hasGroup;

    @ApiModelProperty(value = "最新访问日期")
    @TableField(value = "latest_access_time")
    private LocalDate latestAccessTime;

    @ApiModelProperty(value = "医生注册时填写的机构")
    @TableField(value = "register_org_name")
    private String registerOrgName;

    @ApiModelProperty(value = "是否AI托管")
    @TableField("ai_hosted")
    private Boolean aiHosted;

    @ApiModelProperty(value = "登录状态")
    @TableField("login_status")
    private Boolean loginStatus;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

    @ApiModelProperty(value = "医生挂号信息")
    @TableField(value = "registration_information")
    private String registrationInformation;
}
