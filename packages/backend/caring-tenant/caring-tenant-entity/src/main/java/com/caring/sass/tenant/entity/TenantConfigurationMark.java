package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 项目配置标记表
 * </p>
 *
 * @author 杨帅
 * @since 2023-07-18
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_configuration_mark")
@ApiModel(value = "TenantConfigurationMark", description = "项目配置标记表")
@AllArgsConstructor
public class TenantConfigurationMark extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 关联内容库状态 0 默认设置, 1 已设置
     */
    @ApiModelProperty(value = "关联内容库状态 0 默认设置, 1 已设置")
    @TableField("library_tenant_status")
    @Excel(name = "关联内容库状态 0 默认设置, 1 已设置")
    private Integer libraryTenantStatus;

    /**
     * 注册引导设置状态 0 默认设置, 1 已设置
     */
    @ApiModelProperty(value = "注册引导设置状态 0 默认设置, 1 已设置")
    @TableField("tenant_register_guide_status")
    @Excel(name = "注册引导设置状态 0 默认设置, 1 已设置")
    private Integer tenantRegisterGuideStatus;

    /**
     * 注册成功提示状态 0 默认设置, 1 已设置
     */
    @ApiModelProperty(value = "注册成功提示状态 0 默认设置, 1 已设置")
    @TableField("tenant_register_success_status")
    @Excel(name = "注册成功提示状态 0 默认设置, 1 已设置")
    private Integer tenantRegisterSuccessStatus;

    /**
     * 基本信息表单状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "基本信息表单状态 0 未设置, 1 已设置")
    @TableField("base_info_form_status")
    @Excel(name = "基本信息表单状态 0 未设置, 1 已设置")
    private Integer baseInfoFormStatus;

    /**
     * 关键字回复 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "关键字回复 0 未设置, 1 已设置")
    @TableField("keyword_reply_status")
    @Excel(name = "关键字回复 0 未设置, 1 已设置")
    private Integer keywordReplyStatus;

    /**
     * 收到消息回复 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "收到消息回复 0 未设置, 1 已设置")
    @TableField("received_message_reply")
    @Excel(name = "收到消息回复 0 未设置, 1 已设置")
    private Integer receivedMessageReply;

    /**
     * 菜单设置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "菜单设置 0 未设置, 1 已设置")
    @TableField("menu_setting_status")
    @Excel(name = "菜单设置 0 未设置, 1 已设置")
    private Integer menuSettingStatus;

    /**
     * 疾病信息设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "疾病信息设置状态 0 未设置, 1 已设置")
    @TableField("disease_info_status")
    @Excel(name = "疾病信息设置状态 0 未设置, 1 已设置")
    private Integer diseaseInfoStatus;

    /**
     * 标签管理的状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "标签管理的状态 0 未设置, 1 已设置")
    @TableField("label_management_status")
    @Excel(name = "标签管理的状态 0 未设置, 1 已设置")
    private Integer labelManagementStatus;

    /**
     * 随访管理设置的状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "随访管理设置的状态 0 未设置, 1 已设置")
    @TableField("follow_manage_status")
    @Excel(name = "随访管理设置的状态 0 未设置, 1 已设置")
    private Integer followManageStatus;

    /**
     * 角色设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "角色设置状态 0 未设置, 1 已设置")
    @TableField("system_role_status")
    @Excel(name = "角色设置状态 0 未设置, 1 已设置")
    private Integer systemRoleStatus;

    /**
     * 患者个人中心设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "患者个人中心设置状态 0 未设置, 1 已设置")
    @TableField("patient_center_status")
    @Excel(name = "患者个人中心设置状态 0 未设置, 1 已设置")
    private Integer patientCenterStatus;

    /**
     * 医生个人中心设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "医生个人中心设置状态 0 未设置, 1 已设置")
    @TableField("doctor_center_status")
    @Excel(name = "医生个人中心设置状态 0 未设置, 1 已设置")
    private Integer doctorCenterStatus;

    /**
     * 医助个人中心设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "医助个人中心设置状态 0 未设置, 1 已设置")
    @TableField("nursing_center_status")
    @Excel(name = "医助个人中心设置状态 0 未设置, 1 已设置")
    private Integer nursingCenterStatus;

    /**
     * 项目后台统计状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "项目后台统计状态 0 未设置, 1 已设置")
    @TableField("project_backend_statistics_status")
    @Excel(name = "项目后台统计状态 0 未设置, 1 已设置")
    private Integer projectBackendStatisticsStatus;

    /**
     * 患者推荐设置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "患者推荐设置 0 未设置, 1 已设置")
    @TableField("recommend_config_status")
    @Excel(name = "患者推荐设置 0 未设置, 1 已设置")
    private Integer recommendConfigStatus;

    /**
     * 运营支持 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "运营支持 0 未设置, 1 已设置")
    @TableField("operational_support_status")
    @Excel(name = "运营支持 0 未设置, 1 已设置")
    private Integer operationalSupportStatus;

    /**
     * APP推送配置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "APP推送配置 0 未设置, 1 已设置")
    @TableField("push_configuration_status")
    @Excel(name = "APP推送配置 0 未设置, 1 已设置")
    private Integer pushConfigurationStatus;

    /**
     * 安卓变更日志 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "安卓变更日志 0 未设置, 1 已设置")
    @TableField("android_change_log_status")
    @Excel(name = "安卓变更日志 0 未设置, 1 已设置")
    private Integer androidChangeLogStatus;

    /**
     * UNI安卓变更日志 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "UNI安卓变更日志 0 未设置, 1 已设置")
    @TableField("uni_change_log_status")
    @Excel(name = "UNI安卓变更日志 0 未设置, 1 已设置")
    private Integer uniChangeLogStatus;

    /**
     * UNI配置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "UNI配置 0 未设置, 1 已设置")
    @TableField("uni_configuration_status")
    @Excel(name = "UNI配置 0 未设置, 1 已设置")
    private Integer uniConfigurationStatus;

    /**
     * UNI配置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "Banner 0 未设置 1已设置")
    @TableField("banner_status")
    private Integer bannerStatus;


    public static void init(TenantConfigurationMark mark, int status) {
        mark.libraryTenantStatus = status;
        mark.tenantRegisterGuideStatus = status;
        mark.tenantRegisterSuccessStatus = status;
        mark.baseInfoFormStatus = status;
        mark.keywordReplyStatus = status;
        mark.receivedMessageReply = status;
        mark.menuSettingStatus = status;
        mark.diseaseInfoStatus = status;
        mark.labelManagementStatus = status;
        mark.followManageStatus = status;
        mark.systemRoleStatus = status;
        mark.patientCenterStatus = status;
        mark.doctorCenterStatus = status;
        mark.nursingCenterStatus = status;
        mark.projectBackendStatisticsStatus = status;
        mark.recommendConfigStatus = status;
        mark.operationalSupportStatus = status;
        mark.pushConfigurationStatus = status;
        mark.androidChangeLogStatus = status;
        mark.uniChangeLogStatus = status;
        mark.uniConfigurationStatus = status;
        mark.bannerStatus = status;
    }

    public static void init(TenantConfigurationMark mark) {
        init(mark, 0);
    }
}
