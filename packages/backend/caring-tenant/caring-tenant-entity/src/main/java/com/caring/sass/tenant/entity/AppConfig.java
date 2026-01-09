package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 项目APP配置
 * </p>
 *
 * @author leizhi
 * @since 2020-09-21
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_app_config")
@ApiModel(value = "AppConfig", description = "项目APP配置")
@AllArgsConstructor
public class AppConfig extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    // 未打包
    public static final Integer NOT_PACKAGING = 0;

    // 打包中
    public static final Integer PACKAGING = 1;

    // 打包失败
    public static final Integer PACKAGING_FAIL = 2;

    // 打包成功
    public static final Integer PACKAGING_SUCCESS = 3;

    /**
     * 医生二维码背景图
     */
    @ApiModelProperty(value = "医生二维码背景图")
    @Length(max = 500, message = "医生二维码背景图长度不能超过500")
    @TableField(value = "qr_code_background", condition = LIKE)
    @Excel(name = "医生二维码背景图")
    private String qrCodeBackground;

    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    @Length(max = 50, message = "应用名称长度不能超过50")
    @TableField(value = "app_application_name", condition = LIKE)
    @Excel(name = "应用名称")
    private String appApplicationName;

    /**
     * 应用包名,规则：二级域名.pds.automaticCreateApp
     */
    @ApiModelProperty(value = "应用包名,规则：二级域名.pds.automaticCreateApp")
    @Length(max = 50, message = "应用包名,规则：二级域名.pds.automaticCreateApp长度不能超过50")
    @TableField(value = "app_application_id", condition = LIKE)
    @Excel(name = "应用包名,规则：二级域名.pds.automaticCreateApp")
    private String appApplicationId;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    @TableField("app_version_code")
    @Excel(name = "版本号")
    private Integer appVersionCode;

    /**
     * 显示版本号
     */
    @ApiModelProperty(value = "显示版本号")
    @Length(max = 255, message = "显示版本号长度不能超过255")
    @TableField(value = "app_version_name", condition = LIKE)
    @Excel(name = "显示版本号")
    private String appVersionName;

    @ApiModelProperty(value = "UNI医助安卓版本号")
    @TableField(value = "uni_app_version_name", condition = EQUAL)
    private String uniAppVersionName;

    /**
     * 启动页图片
     */
    @ApiModelProperty(value = "启动页图片")
    @Length(max = 500, message = "启动页图片长度不能超过500")
    @TableField(value = "app_launch_image", condition = LIKE)
    @Excel(name = "启动页图片")
    private String appLaunchImage;

    /**
     * 登录背景页
     */
    @ApiModelProperty(value = "登录背景页")
    @Length(max = 500, message = "登录背景页长度不能超过500")
    @TableField(value = "app_login_background", condition = LIKE)
    @Excel(name = "登录背景页")
    private String appLoginBackground;

    /**
     * 服务协议
     */
    @ApiModelProperty(value = "服务协议")
    @Length(max = 65535, message = "服务协议长度不能超过65535")
    @TableField("app_agreement")
    @Excel(name = "服务协议")
    private String appAgreement;

    /**
     * 常见问题
     */
    @ApiModelProperty(value = "常见问题")
    @Length(max = 65535, message = "常见问题长度不能超过65535")
    @TableField("app_frequently_asked_question")
    @Excel(name = "常见问题")
    private String appFrequentlyAskedQuestion;

    /**
     * 问题反馈
     */
    @ApiModelProperty(value = "问题反馈")
    @Length(max = 65535, message = "问题反馈长度不能超过65535")
    @TableField("question_feedback")
    @Excel(name = "问题反馈")
    private String questionFeedback;

    /**
     * 关于我们
     */
    @ApiModelProperty(value = "关于我们")
    @Length(max = 65535, message = "关于我们长度不能超过65535")
    @TableField("app_about_us")
    @Excel(name = "关于我们")
    private String appAboutUs;

    /**
     * 华为应用ID
     */
    @ApiModelProperty(value = "华为应用ID")
    @Length(max = 255, message = "华为应用ID长度不能超过255")
    @TableField(value = "app_huawei_appid", condition = LIKE)
    @Excel(name = "华为应用ID")
    private String appHuaweiAppid;

    /**
     * 微信应用key
     */
    @ApiModelProperty(value = "微信应用key")
    @Length(max = 255, message = "微信应用key长度不能超过255")
    @TableField(value = "app_weixin_key", condition = LIKE)
    @Excel(name = "微信应用key")
    private String appWeixinKey;

    /**
     * 环信IMkey
     */
    @ApiModelProperty(value = "环信IMkey")
    @Length(max = 255, message = "环信IMkey长度不能超过255")
    @TableField(value = "app_img_key", condition = LIKE)
    @Excel(name = "环信IMkey")
    private String appImgKey;

    /**
     * 环信IM密码
     */
    @ApiModelProperty(value = "环信IM密码")
    @Length(max = 255, message = "环信IM密码长度不能超过255")
    @TableField(value = "app_im_password", condition = LIKE)
    @Excel(name = "环信IM密码")
    private String appImPassword;

    /**
     * 60*60应用图标
     */
    @ApiModelProperty(value = "60*60应用图标")
    @Length(max = 255, message = "60*60应用图标长度不能超过255")
    @TableField(value = "app_icon", condition = LIKE)
    @Excel(name = "60*60应用图标")
    private String appIcon;

    /**
     * APK的url
     */
    @ApiModelProperty(value = "APK的url")
    @Length(max = 500, message = "APK的url长度不能超过500")
    @TableField(value = "apk_url", condition = LIKE)
    @Excel(name = "APK的url")
    private String apkUrl;

    /**
     * 打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）
     */
    @ApiModelProperty(value = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    @TableField("package_status")
    @Excel(name = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    private Integer packageStatus;

    @ApiModelProperty(value = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    @TableField("uni_package_status")
    @Excel(name = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    private Integer uniPackageStatus;
    /**
     * 医生登录二维码
     */
    @ApiModelProperty(value = "医生登录二维码")
    @Length(max = 500, message = "医生登录二维码长度不能超过500")
    @TableField(value = "doctor_qr_url", condition = LIKE)
    @Excel(name = "医生登录二维码")
    private String doctorQrUrl;

    @ApiModelProperty(value = "医助激活的二维码")
    @TableField(exist = false)
    private String assistantQrUrl;

    @ApiModelProperty(value = "分享用的医生激活码")
    @TableField(exist = false)
    private String doctorShareQrUrl;
    /**
     * 包名url
     */
    @ApiModelProperty(value = "包名url")
    @Length(max = 500, message = "包名url长度不能超过500")
    @TableField(value = "upload_url", condition = LIKE)
    @Excel(name = "包名url")
    private String uploadUrl;

    /**
     * 极光推送的Channel
     */
    @ApiModelProperty(value = "极光推送的Channel")
    @Length(max = 50, message = "极光推送的Channel长度不能超过50")
    @TableField(value = "jpush_channel", condition = LIKE)
    @Excel(name = "极光推送的Channel")
    private String jpushChannel;

    /**
     * 极光推送的AppKey
     */
    @ApiModelProperty(value = "极光推送的AppKey")
    @Length(max = 50, message = "极光推送的AppKey长度不能超过50")
    @TableField(value = "jpush_appkey", condition = LIKE)
    @Excel(name = "极光推送的AppKey")
    private String jpushAppkey;

    /**
     * 极光 MasterSecret
     */
    @ApiModelProperty(value = "极光 MasterSecret")
    @Length(max = 200, message = "极光 MasterSecret长度不能超过200")
    @TableField(value = "jpush_master_secret", condition = LIKE)
    @Excel(name = "极光 MasterSecret")
    private String jpushMasterSecret;

    /**
     * 药店版APP二维码下载url
     */
    @Deprecated
    @ApiModelProperty(value = "药店版APP二维码下载url")
    @Length(max = 255, message = "药店版APP二维码下载url长度不能超过255")
    @TableField(value = "drugstore_qrcode_url", condition = LIKE)
    @Excel(name = "药店版APP二维码下载url")
    private String drugstoreQrcodeUrl;

    /**
     * 药店版apk二维码下载url
     */
    @Deprecated
    @ApiModelProperty(value = "药店版apk二维码下载url")
    @Length(max = 255, message = "药店版apk二维码下载url长度不能超过255")
    @TableField(value = "drugstore_apk_url", condition = LIKE)
    @Excel(name = "药店版apk二维码下载url")
    private String drugstoreApkUrl;

    /**
     * 药店apk打包状态
     */
    @ApiModelProperty(value = "药店apk打包状态")
    @TableField("drugstore_apk_packing_status")
    @Excel(name = "药店apk打包状态", replace = {"是_true", "否_false", "_null"})
    private Boolean drugstoreApkPackingStatus;

    /**
     * 小米AppId
     */
    @ApiModelProperty(value = "小米AppId")
    @Length(max = 200, message = "小米AppId长度不能超过200")
    @TableField(value = "mi_app_id", condition = LIKE)
    @Excel(name = "小米AppId")
    private String miAppId;

    /**
     * 小米AppKey
     */
    @ApiModelProperty(value = "小米AppKey")
    @Length(max = 200, message = "小米AppKey长度不能超过200")
    @TableField(value = "mi_app_key", condition = LIKE)
    @Excel(name = "小米AppKey")
    private String miAppKey;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    @TableField("tenant_id")
    @Excel(name = "租户id")
    private Long tenantId;

    /**
     * 预约管理开关：0开启，1关闭
     */
    @Deprecated
    @ApiModelProperty(value = "预约管理开关：0开启，1关闭")
    @TableField("appointment_switch")
    @Excel(name = "预约管理开关：0开启，1关闭")
    private Integer appointmentSwitch;


    /**
     * @see com.caring.sass.common.constant.AppointmentDoctorScope
     * {@link com.caring.sass.common.constant.AppointmentDoctorScope}
     */
    @ApiModelProperty(value = "预约的医生范围：myself， organ")
    @TableField("appointment_doctor_scope")
    @Excel(name = "预约的医生范围：myself, organ")
    private String appointmentDoctorScope;


    @Deprecated
    @ApiModelProperty(value = "会诊管理开关：0开启，1关闭")
    @TableField("consultation_switch")
    private Integer consultationSwitch;

    @Deprecated
    @ApiModelProperty(value = "转诊开关：0开启，1关闭")
    @TableField("referral_switch")
    private Integer referralSwitch;

    @Deprecated
    @ApiModelProperty(value = "患者管理平台：0开启，1关闭")
    @TableField("patient_manage_switch")
    private Integer patientManageSwitch;

    @Deprecated
    @ApiModelProperty(value = "app使用者称呼")
    @TableField(value = "app_user_call", condition = EQUAL)
    private String appUserCall;


    @Deprecated
    @ApiModelProperty(value = "微信使用者称呼")
    @TableField(value = "wx_user_call", condition = EQUAL)
    private String wxUserCall;


    @ApiModelProperty(value = "更新记录")
    @TableField(value = "renew_record")
    @Excel(name = "更新记录")
    private String renewRecord;

    @ApiModelProperty(value = "uni安卓更新记录")
    @TableField(value = "uni_renew_record")
    @Excel(name = "uni安卓更新记录")
    private String uniRenewRecord;


    @ApiModelProperty(value = "冗余租户code")
    @TableField(exist = false)
    private String tenantCode;

    /**
     * uni-app的离线打包key
     */
    @ApiModelProperty(value = "uniapp的离线打包key")
    @TableField(value = "dcloud_appkey")
    private String dcloudAppkey;

    /**
     * uni-app 中的APPID
     */
    @ApiModelProperty(value = "uniapp中的APPID")
    @TableField(value = "dcloud_appid")
    private String dcloudAppid;

    @ApiModelProperty(value = "UNIapp下载二维码地址")
    @TableField(value = "uni_apk_url")
    private String uniApkUrl;

    @ApiModelProperty(value = "UNIapp安装包下载地址")
    @TableField(value = "uni_apk_download_url")
    private String uniApkDownloadUrl;

}
