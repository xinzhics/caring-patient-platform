package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 知识库用户
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_knowledge_user", autoResultMap = true)
@ApiModel(value = "KnowledgeUser", description = "知识库用户")
@AllArgsConstructor
public class KnowledgeUser extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户头像")
    @TableField(value = "user_avatar", condition = LIKE)
    private String userAvatar;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    @Length(max = 20, message = "用户名称长度不能超过20")
    @TableField(value = "user_name", condition = LIKE)
    @Excel(name = "用户名称")
    private String userName;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 300, message = "用户手机号长度不能超过300")
    @Excel(name = "用户手机号")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    private String userMobile;


    @ApiModelProperty(value = "用户密码")
    @Length(max = 300, message = "用户密码长度不能超过300")
    @TableField(value = "password", condition = EQUAL)
    private String password;

    /**
     * 用户类型 主任 普通医生
     */
    @ApiModelProperty(value = "用户类型 主任 普通医生")
    @TableField(value = "user_type", condition = LIKE)
    @Excel(name = "用户类型 主任 普通医生")
    private KnowDoctorType userType;

    @ApiModelProperty(value = "用户类型 会员等级")
    @TableField(value = "membership_level", condition = EQUAL)
    @Deprecated
    private MembershipLevel membershipLevel;


    @Deprecated
    @ApiModelProperty(value = "会员到期时间")
    @TableField(value = "membership_expiration")
    private LocalDateTime membershipExpiration;

    @Deprecated
    @ApiModelProperty(value = "体验AI的截止时间")
    @TableField(value = "experience_ai_time")
    private LocalDateTime experienceAiTime;
    /**
     * 租户(一个主任一个租户)
     */
    @ApiModelProperty(value = "域名(一个主任一个域名)")
    @Length(max = 50, message = "域名(一个主任一个域名)长度不能超过50")
    @TableField(value = "user_domain", condition = LIKE)
    @Excel(name = "租户(一个主任一个租户)")
    private String userDomain;


    @ApiModelProperty(value = "专科域名 一级分类")
    @TableField(value = "menu_domain", condition = EQUAL)
    private String menuDomain;


    @ApiModelProperty(value = "微信openId")
    @TableField(value = "open_id", condition = EQUAL)
    private String openId;


    @ApiModelProperty(value = "分享的海报")
    @TableField(value = "share_poster", condition = EQUAL)
    private String sharePoster;

    @ApiModelProperty(value = "基础会员的价格，单位是分")
    @TableField(value = "basic_membership_price", condition = EQUAL)
    private Integer basicMembershipPrice;

    @ApiModelProperty(value = "专业版的价格，单位是分")
    @TableField(value = "professional_version_price", condition = EQUAL)
    private Integer professionalVersionPrice;

    @ApiModelProperty(value = "用户选择的UI类型")
    @TableField(value = "user_set_ui_style", condition = EQUAL)
    private Integer userSetUiStyle;


    @ApiModelProperty(value = "博主知识库浏览次数")
    @TableField(value = "number_views", condition = EQUAL)
    private Integer numberViews;

    @ApiModelProperty(value = "用户工作单位")
    @TableField(value = "work_unit", condition = EQUAL)
    @Length(max = 400, message = "用户工作单位长度不能超过400")
    private String workUnit;

    @ApiModelProperty(value = "科室")
    @TableField(value = "department", condition = EQUAL)
    @Length(max = 300, message = "科室长度不能超过300")
    private String department;

    @ApiModelProperty(value = "职称")
    @TableField(value = "doctor_title", condition = EQUAL)
    @Length(max = 300, message = "用户工作单位长度不能超过300")
    private String doctorTitle;

    @ApiModelProperty(value = "擅长")
    @TableField(value = "specialty", condition = EQUAL)
    private String specialty;


    @ApiModelProperty(value = "个人简介")
    @TableField(value = "personal_profile", condition = EQUAL)
    @Length(max = 1000 , message = "个人简介长度不能超过300")
    private String personalProfile;

    @ApiModelProperty(value = "AI Studio的链接")
    @TableField(value = "ai_studio_url", condition = EQUAL)
    private String aiStudioUrl;

    @ApiModelProperty(value = "ai工作室医生ID")
    @TableField(value = "ai_studio_doctor_id", condition = EQUAL)
    private String aiStudioDoctorId;

    @ApiModelProperty(value = "AI交互试听音频连接")
    @TableField(value = "ai_interactive_audio_trial_listening", condition = EQUAL)
    private String aiInteractiveAudioTrialListening;

    @ApiModelProperty(value = "数字工作室的链接")
    @TableField(value = "digital_studio_url", condition = EQUAL)
    private String digitalStudioUrl;

    @ApiModelProperty(value = "其他的链接")
    @TableField(value = "other_url", condition = EQUAL)
    private String otherUrl;

    @ApiModelProperty(value = "打招呼视频")
    @TableField(value = "greeting_video", condition = EQUAL)
    private String greetingVideo;

    @ApiModelProperty(value = "打招呼视频封面")
    @TableField(value = "greeting_video_cover", condition = EQUAL)
    private String greetingVideoCover;

    @ApiModelProperty(value = "排序")
    @TableField(value = "sort_", condition = EQUAL)
    private Integer sort;

    @ApiModelProperty(value = "菜单ID 总平台分类")
    @TableField(value = "knowledge_menu_id", condition = LIKE)
    private String knowledgeMenuId;


    @ApiModelProperty(value = "子平台 分类")
    @Deprecated
    @TableField(value = "child_domain_menu_id", condition = LIKE)
    private String childDomainMenuId;

    @ApiModelProperty(value = "子平台 域名")
    @Deprecated
    @TableField(value = "child_domain", condition = EQUAL)
    private String childDomain;

    /**
     * 真人头像
     */
    @ApiModelProperty(value = "真人头像")
    @TableField(value = "real_human_avatar", condition = EQUAL)
    private String realHumanAvatar;

    @ApiModelProperty(value = "博主订阅粉丝数")
    @TableField(exist = false)
    private Integer fanNumber;

    @ApiModelProperty(value = "查询的等级要求")
    @TableField(exist = false)
    private Integer viewPermissions;

    @ApiModelProperty(value = "下载的等级要求")
    @TableField(exist = false)
    private Integer downloadPermission;

    @ApiModelProperty(value = "订阅状态")
    @TableField(exist = false)
    private Boolean subscribeStatus;

    @ApiModelProperty(value = "订阅时间")
    @TableField(exist = false)
    private LocalDateTime subscribeTime;


    @ApiModelProperty(value = "订阅开关")
    @TableField(value = "subscribe_switch", condition = EQUAL)
    private Boolean subscribeSwitch;

    @ApiModelProperty(value = "订阅最后更新时间")
    @TableField(value = "subscribe_last_update_time", condition = EQUAL)
    private LocalDateTime subscribeLastUpdateTime;

    @ApiModelProperty(value = "订阅会员名称")
    @TableField(value = "subscribe_user_name", condition = EQUAL)
    private String subscribeUserName;

    @ApiModelProperty(value = "开启月费")
    @TableField(value = "open_monthly_payment", condition = EQUAL)
    private Boolean openMonthlyPayment;

    @ApiModelProperty(value = "开启年费")
    @TableField(value = "open_annual_payment", condition = EQUAL)
    private Boolean openAnnualPayment;

    @ApiModelProperty(value = "开放科普患教数据")
    @TableField(value = "open_article_data", condition = EQUAL)
    private Boolean openArticleData;

    @ApiModelProperty(value = "开放文献解读")
    @TableField(value = "open_text_ual", condition = EQUAL)
    private Boolean openTextual;

    @ApiModelProperty(value = "开放学术材料")
    @TableField(value = "open_academic_materials", condition = EQUAL)
    private Boolean openAcademicMaterials;

    @ApiModelProperty(value = "开放个人成果")
    @TableField(value = "open_personal_achievements", condition = EQUAL)
    private Boolean openPersonalAchievements;

    @ApiModelProperty(value = "开放病例数据库")
    @TableField(value = "open_case_database", condition = EQUAL)
    private Boolean openCaseDatabase;

    @ApiModelProperty(value = "开放日常收藏")
    @TableField(value = "open_daily_collection", condition = EQUAL)
    private Boolean openDailyCollection;

    @ApiModelProperty(value = "博主展示在DocuKnow")
    @TableField(value = "show_in_docuknow", condition = EQUAL)
    private Boolean showInDocuKnow;

    @ApiModelProperty(value = "当前用户显示博主的订阅数据")
    @TableField(exist = false)
    private Boolean showSubscribeData;

    @ApiModelProperty(value = "数据来源 ai工作室， 赛柏蓝。自建")
    @TableField(value = "data_source", condition = EQUAL)
    private String dataSource;


    @ApiModelProperty(value = "赛柏蓝平台博主ID")
    @TableField(value = "third_party_user_id", condition = EQUAL)
    private Long thirdPartyUserId;

    @ApiModelProperty(value = "赛柏蓝平台博主主页")
    @TableField(value = "third_party_knowledge_link", condition = EQUAL)
    private String thirdPartyKnowledgeLink;

    @ApiModelProperty(value = "总通话时长")
    @TableField(value = "total_call_duration", condition = EQUAL)
    private Integer totalCallDuration;


    @ApiModelProperty(value = "通话开关")
    @TableField(value = "call_switch", condition = EQUAL)
    private Integer callSwitch;

    @ApiModelProperty(value = "Saas租户编码")
    @TableField(value = "saas_tenant_code", condition = EQUAL)
    private String saasTenantCode;

    @ApiModelProperty(value = "短信验证码登录首次登录时间")
    @TableField(value = "sms_code_login_first_time", condition = EQUAL)
    private LocalDateTime smsCodeLoginFirstTime;


    @ApiModelProperty(value = "短信验证码登录最后登录时间")
    @TableField(value = "sms_code_login_last_time", condition = EQUAL)
    private LocalDateTime smsCodeLoginLastTime;

    @ApiModelProperty(value = "上传资质提醒时间")
    @TableField(value = "upload_qualification_remind_time", condition = EQUAL)
    private LocalDateTime uploadQualificationRemindTime;


    @ApiModelProperty(value = "用户资质")
    @TableField(exist = false)
    private List<KnowledgeUserQualification> userQualification;


    @ApiModelProperty(value = "知识库博主子平台分类")
    @TableField(exist = false)
    List<KnowledgeUserDomain> knowledgeUserDomains;


    public Integer getViewPermissions() {
        if (KnowDoctorType.CHIEF_PHYSICIAN.equals(userType)) {
            return 4;
        }
        return MembershipLevel.getGrade(membershipLevel);
    }

    public Integer getDownloadPermission() {
        if (KnowDoctorType.CHIEF_PHYSICIAN.equals(userType)) {
            return 4;
        }
        return MembershipLevel.getGrade(membershipLevel);
    }

    public String getSubscribeUserName() {
        if (subscribeUserName == null) {
            return "订阅会员";
        }
        return subscribeUserName;
    }

}
