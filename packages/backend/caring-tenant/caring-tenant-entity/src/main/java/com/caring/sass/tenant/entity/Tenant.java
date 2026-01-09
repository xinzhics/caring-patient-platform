package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.enumeration.TenantConnectTypeEnum;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.enumeration.TenantTypeEnum;
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
 * 项目
 * </p>
 *
 * @author caring
 * @since 2019-10-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant")
@ApiModel(value = "Tenant", description = "项目")
public class Tenant extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final int WX_STATUS_YES = 1;

    public static final int WX_STATUS_NO = 0;

    /**
     * redis中 ai助手信息存储的 hash 桶的key
     */
    public static final String TENANT_AI_REDIS_INFO_KEY = "tenant_ai_redis_info_key";

    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    @Length(max = 20, message = "项目编码长度不能超过20")
    @TableField(value = "code", condition = LIKE)
    @Excel(name = "项目编码", width = 20)
    private String code;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    @Length(max = 255, message = "项目名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "项目名称", width = 20)
    private String name;

    /**
     * 类型
     * #{CREATE:创建;REGISTER:注册}
     */
    @ApiModelProperty(value = "类型")
    @TableField("type")
    @Excel(name = "类型", width = 20, replace = {"注册_REGISTER", "创建_CREATE", "_null"})
    private TenantTypeEnum type;

    @TableField("connect_type")
    @ApiModelProperty(value = "连接类型", example = "LOCAL,REMOTE")
    @Excel(name = "连接类型", width = 20, replace = {"本地_LOCAL", "远程_REMOTE", "_null"})
    private TenantConnectTypeEnum connectType;

    /**
     * 状态
     * #{NORMAL:正常;FORBIDDEN:禁用;WAITING:待审核;REFUSE:拒绝}
     */
    @ApiModelProperty(value = "状态")
    @TableField("status")
    @Excel(name = "状态", width = 20, replace = {"正常_NORMAL", "部署中_WAIT_INIT", "禁用_FORBIDDEN", "待审核_WAITING", "拒绝_REFUSE", "DELETING_已删除", "_null"})
    private TenantStatusEnum status;

    @ApiModelProperty(value = "状态顺序 正常_NORMAL 1 部署中_WAIT_INIT 2 禁用_FORBIDDEN 3")
    @TableField("status_sort")
    private Integer statusSort;

    @ApiModelProperty(value = "只读")
    @TableField("readonly")
    @Excel(name = "只读", replace = {"是_true", "否_false", "_null"})
    private Boolean readonly;

    /**
     * 责任人
     */
    @ApiModelProperty(value = "责任人")
    @Length(max = 50, message = "责任人长度不能超过50")
    @TableField(value = "duty", condition = LIKE)
    @Excel(name = "责任人")
    private String duty;

    /**
     * 有效期
     * 为空表示永久
     */
    @ApiModelProperty(value = "有效期")
    @TableField("expiration_time")
    @Excel(name = "有效期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationTime;

    /**
     * logo地址
     */
    @ApiModelProperty(value = "logo地址")
    @Length(max = 255, message = "logo地址长度不能超过255")
    @TableField(value = "logo", condition = LIKE)
    private String logo;

    /**
     * 项目简介
     */
    @ApiModelProperty(value = "项目简介")
    @Length(max = 255, message = "项目简介长度不能超过255")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "项目简介", width = 20)
    private String describe;


    @ApiModelProperty(value = "域名")
    @Length(max = 50, message = "域名长度不能超过50")
    @TableField(value = "domain_name", condition = EQUAL)
    @Excel(name = "域名")
    private String domainName;


    @ApiModelProperty(value = "旧域名")
    @Length(max = 50, message = "旧域名不能超过50")
    @TableField(value = "old_domain_name", condition = EQUAL)
    @Excel(name = "旧域名")
    private String oldDomainName;


    @ApiModelProperty(value = "微信AppId")
    @Length(max = 50, message = "微信AppId50")
    @TableField(value = "wx_app_id", condition = EQUAL)
    @Excel(name = "微信AppId")
    private String wxAppId;

    @ApiModelProperty(value = "微信名")
    @Length(max = 200, message = "微信名")
    @TableField(value = "wx_name", condition = EQUAL)
    @Excel(name = "微信名")
    private String wxName;

    @ApiModelProperty(value = "微信公众号是否校验")
    @TableField(value = "wx_status", condition = EQUAL)
    @Excel(name = "微信状态")
    private Integer wxStatus;

    @ApiModelProperty(value = "公众号绑定失败原因")
    @TableField(value = "wx_bind_error_message", condition = EQUAL)
    @Excel(name = "公众号绑定失败原因")
    private String wxBindErrorMessage;

    @ApiModelProperty(value = "公众号绑定时间")
    @TableField(value = "wx_bind_time", condition = EQUAL)
    private LocalDateTime wxBindTime;

    @ApiModelProperty(value = "发布时间")
    @TableField(value = "publish_time")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "医生扫码关注图片地址")
    @TableField(value = "doctor_qr_url")
    private String doctorQrUrl;

    @ApiModelProperty(value = "英文医生扫码关注图片地址")
    @TableField(value = "english_doctor_qr_url")
    private String englishDoctorQrUrl;

    @ApiModelProperty(value = "医生扫码关注图片分享地址")
    @TableField(value = "doctor_share_qr_url")
    private String doctorShareQrUrl;

    @ApiModelProperty(value = "英语医生扫码关注图片分享地址")
    @TableField(value = "english_doctor_share_qr_url")
    private String englishDoctorShareQrUrl;

    @ApiModelProperty(value = "医助激活的二维码")
    @TableField(value = "assistant_qr_url")
    private String assistantQrUrl;

    @ApiModelProperty(value = "英语医助激活的二维码")
    @TableField(value = "english_assistant_qr_url")
    private String englishAssistantQrUrl;

    @ApiModelProperty(value = "顺序")
    @TableField(value = "sort", condition = EQUAL)
    @Excel(name = "顺序")
    private Integer sort;


    @ApiModelProperty(value = "群发通知的二维码")
    @TableField(value = "qun_fa_notification_qr_url")
    private String qunFaNotificationQrUrl;


    @ApiModelProperty(value = "AI助手的名称")
    @TableField(value = "ai_assistant_name")
    private String aiAssistantName;

    @ApiModelProperty(value = "AI助手的头像")
    @TableField(value = "ai_assistant_image")
    private String aiAssistantImage;

    @ApiModelProperty(value = "剩余天数（只供排序使用）")
    @TableField(value = "days_remaining")
    private Integer daysRemaining;

    @ApiModelProperty(value = "项目种类：mentalDisease(精神病)")
    @TableField(value = "project_type")
    private String projectType;

    @ApiModelProperty(value = "项目默认语言")
    @TableField(value = "default_language")
    private String defaultLanguage;

    @ApiModelProperty(value = "医生注册的方式 0 自由注册， 1 必须审核")
    @TableField(value = "doctor_register_type")
    private Integer doctorRegisterType;

    @ApiModelProperty(value = "项目业务类型 普通项目， 过敏疾病项目")
    @TableField(value = "diseases_type")
    private TenantDiseasesTypeEnum diseasesType;

    @ApiModelProperty(value = "数据安全开关 默认关闭")
    @TableField(value = "data_security_settings")
    private Boolean dataSecuritySettings;

    @ApiModelProperty(value = "公众号类型")
    @TableField(value = "official_account_type")
    private TenantOfficialAccountType officialAccountType;

    @TableField(exist = false)
    @ApiModelProperty(value = "搜索关键字")
    @Length(max = 200, message = "搜索关键字")
    private String keyWord;

    @ApiModelProperty(value = "App二维码地址")
    @TableField(exist = false)
    private String qrCodeUrl;

    @ApiModelProperty(value = "UniApp下载二维码地址")
    @TableField(exist = false)
    private String uniDownloadCodeUrl;

    @ApiModelProperty(value = "其他信息的备注(创建者，管理者，授权项目，自建项目)")
    @TableField(exist = false)
    private String otherInfoRemark;

    public TenantOfficialAccountType getOfficialAccountType() {
        if (officialAccountType == null) {
            return TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER;
        }
        return officialAccountType;
    }

    public boolean isPersonalServiceNumber() {
        if (officialAccountType != null && officialAccountType == TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCertificationServiceNumber() {
        if (officialAccountType == null || officialAccountType == TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER) {
            return true;
        } else {
            return false;
        }
    }



}
