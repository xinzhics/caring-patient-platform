package com.caring.sass.tenant.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AppConfigUpdateDTO", description = "项目APP配置")
public class AppConfigUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 医生二维码背景图
     */
    @ApiModelProperty(value = "医生二维码背景图")
    @Length(max = 500, message = "医生二维码背景图长度不能超过500")
    private String qrCodeBackground;
    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    @Length(max = 50, message = "应用名称长度不能超过50")
    private String appApplicationName;
    /**
     * 应用包名,规则：二级域名.pds.automaticCreateApp
     */
    @ApiModelProperty(value = "应用包名,规则：二级域名.pds.automaticCreateApp")
    @Length(max = 50, message = "应用包名,规则：二级域名.pds.automaticCreateApp长度不能超过50")
    private String appApplicationId;
    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private Integer appVersionCode;
    /**
     * 显示版本号
     */
    @ApiModelProperty(value = "显示版本号")
    @Length(max = 255, message = "显示版本号长度不能超过255")
    private String appVersionName;

    @ApiModelProperty(value = "UNI医助安卓版本号")
    private String uniAppVersionName;
    /**
     * 启动页图片
     */
    @ApiModelProperty(value = "启动页图片")
    @Length(max = 500, message = "启动页图片长度不能超过500")
    private String appLaunchImage;
    /**
     * 登录背景页
     */
    @ApiModelProperty(value = "登录背景页")
    @Length(max = 500, message = "登录背景页长度不能超过500")
    private String appLoginBackground;
    /**
     * 服务协议
     */
    @ApiModelProperty(value = "服务协议")
    @Length(max = 65535, message = "服务协议长度不能超过65,535")
    private String appAgreement;
    /**
     * 常见问题
     */
    @ApiModelProperty(value = "常见问题")
    @Length(max = 65535, message = "常见问题长度不能超过65,535")
    private String appFrequentlyAskedQuestion;
    /**
     * 问题反馈
     */
    @ApiModelProperty(value = "问题反馈")
    @Length(max = 65535, message = "问题反馈长度不能超过65,535")
    private String questionFeedback;
    /**
     * 关于我们
     */
    @ApiModelProperty(value = "关于我们")
    @Length(max = 65535, message = "关于我们长度不能超过65,535")
    private String appAboutUs;
    /**
     * 华为应用ID
     */
    @ApiModelProperty(value = "华为应用ID")
    @Length(max = 255, message = "华为应用ID长度不能超过255")
    private String appHuaweiAppid;
    /**
     * 微信应用key
     */
    @ApiModelProperty(value = "微信应用key")
    @Length(max = 255, message = "微信应用key长度不能超过255")
    private String appWeixinKey;
    /**
     * 环信IMkey
     */
    @ApiModelProperty(value = "环信IMkey")
    @Length(max = 255, message = "环信IMkey长度不能超过255")
    private String appImgKey;
    /**
     * 环信IM密码
     */
    @ApiModelProperty(value = "环信IM密码")
    @Length(max = 255, message = "环信IM密码长度不能超过255")
    private String appImPassword;
    /**
     * 60*60应用图标
     */
    @ApiModelProperty(value = "60*60应用图标")
    @Length(max = 255, message = "60*60应用图标长度不能超过255")
    private String appIcon;
    /**
     * APK的url
     */
    @ApiModelProperty(value = "APK的url")
    @Length(max = 500, message = "APK的url长度不能超过500")
    private String apkUrl;
    /**
     * 打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）
     */
    @ApiModelProperty(value = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    private Integer packageStatus;
    /**
     * 医生登录二维码
     */
    @ApiModelProperty(value = "医生登录二维码")
    @Length(max = 500, message = "医生登录二维码长度不能超过500")
    private String doctorLoginCode;
    /**
     * 包名url
     */
    @ApiModelProperty(value = "包名url")
    @Length(max = 500, message = "包名url长度不能超过500")
    private String uploadUrl;
    /**
     * 极光推送的Channel
     */
    @ApiModelProperty(value = "极光推送的Channel")
    @Length(max = 50, message = "极光推送的Channel长度不能超过50")
    private String jpushChannel;
    /**
     * 极光推送的AppKey
     */
    @ApiModelProperty(value = "极光推送的AppKey")
    @Length(max = 50, message = "极光推送的AppKey长度不能超过50")
    private String jpushAppkey;
    /**
     * 极光 MasterSecret
     */
    @ApiModelProperty(value = "极光 MasterSecret")
    @Length(max = 200, message = "极光 MasterSecret长度不能超过200")
    private String jpushMasterSecret;
    /**
     * 药店版APP二维码下载url
     */
    @ApiModelProperty(value = "药店版APP二维码下载url")
    @Length(max = 255, message = "药店版APP二维码下载url长度不能超过255")
    private String drugstoreQrcodeUrl;
    /**
     * 药店版apk二维码下载url
     */
    @ApiModelProperty(value = "药店版apk二维码下载url")
    @Length(max = 255, message = "药店版apk二维码下载url长度不能超过255")
    private String drugstoreApkUrl;
    /**
     * 药店apk打包状态
     */
    @ApiModelProperty(value = "药店apk打包状态")
    private Boolean drugstoreApkPackingStatus;
    /**
     * 小米AppId
     */
    @ApiModelProperty(value = "小米AppId")
    @Length(max = 200, message = "小米AppId长度不能超过200")
    private String miAppId;
    /**
     * 小米AppKey
     */
    @ApiModelProperty(value = "小米AppKey")
    @Length(max = 200, message = "小米AppKey长度不能超过200")
    private String miAppKey;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    /**
     * 预约管理开关：0开启，1关闭
     */
    @ApiModelProperty(value = "预约管理开关：0开启，1关闭")
    private Integer appointmentSwitch;

    @ApiModelProperty(value = "预约的医生范围：myself， organ")
    private String appointmentDoctorScope;

    /**
     * 会诊管理开关：0开启，1关闭
     */
    @ApiModelProperty(value = "会诊管理开关：0开启，1关闭")
    private Integer consultationSwitch;

    @ApiModelProperty(value = "患者管理平台：0开启，1关闭")
    private Integer patientManageSwitch;

    /**
     * app使用者称呼
     */
    @ApiModelProperty(value = "app使用者")
    @Length(max = 64, message = "app使用者称呼长度不能超过64")
    private String appUserCall;

    /**
     * 微信使用者称呼
     */
    @ApiModelProperty(value = "二维码角色")
    @Length(max = 64, message = "微信使用者称呼长度不能超过64")
    private String wxUserCall;

    @ApiModelProperty(value = "更新记录")
    private String renewRecord;


    @ApiModelProperty(value = "uni安卓更新记录")
    private String uniRenewRecord;

    /**
     * uni-app的离线打包key
     */
    @ApiModelProperty(value = "uniapp的离线打包key")
    private String dcloudAppkey;

    /**
     * uni-app 中的APPID
     */
    @ApiModelProperty(value = "uniapp中的APPID")
    private String dcloudAppid;

}
