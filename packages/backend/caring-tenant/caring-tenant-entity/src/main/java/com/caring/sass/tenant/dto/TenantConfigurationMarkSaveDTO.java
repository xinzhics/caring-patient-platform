package com.caring.sass.tenant.dto;

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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantConfigurationMarkSaveDTO", description = "项目配置标记表")
public class TenantConfigurationMarkSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联内容库状态 0 默认设置, 1 已设置
     */
    @ApiModelProperty(value = "关联内容库状态 0 默认设置, 1 已设置")
    private Integer libraryTenantStatus;
    /**
     * 注册引导设置状态 0 默认设置, 1 已设置
     */
    @ApiModelProperty(value = "注册引导设置状态 0 默认设置, 1 已设置")
    private Integer tenantRegisterGuideStatus;
    /**
     * 注册成功提示状态 0 默认设置, 1 已设置
     */
    @ApiModelProperty(value = "注册成功提示状态 0 默认设置, 1 已设置")
    private Integer tenantRegisterSuccessStatus;
    /**
     * 基本信息表单状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "基本信息表单状态 0 未设置, 1 已设置")
    private Integer baseInfoFormStatus;
    /**
     * 关键字回复 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "关键字回复 0 未设置, 1 已设置")
    private Integer keywordReplyStatus;
    /**
     * 收到消息回复 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "收到消息回复 0 未设置, 1 已设置")
    private Integer receivedMessageReply;
    /**
     * 菜单设置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "菜单设置 0 未设置, 1 已设置")
    private Integer menuSettingStatus;
    /**
     * 疾病信息设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "疾病信息设置状态 0 未设置, 1 已设置")
    private Integer diseaseInfoStatus;
    /**
     * 标签管理的状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "标签管理的状态 0 未设置, 1 已设置")
    private Integer labelManagementStatus;
    /**
     * 随访管理设置的状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "随访管理设置的状态 0 未设置, 1 已设置")
    private Integer followManageStatus;
    /**
     * 角色设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "角色设置状态 0 未设置, 1 已设置")
    private Integer systemRoleStatus;
    /**
     * 患者个人中心设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "患者个人中心设置状态 0 未设置, 1 已设置")
    private Integer patientCenterStatus;
    /**
     * 医生个人中心设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "医生个人中心设置状态 0 未设置, 1 已设置")
    private Integer doctorCenterStatus;
    /**
     * 医助个人中心设置状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "医助个人中心设置状态 0 未设置, 1 已设置")
    private Integer nursingCenterStatus;
    /**
     * 项目后台统计状态 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "项目后台统计状态 0 未设置, 1 已设置")
    private Integer projectBackendStatisticsStatus;
    /**
     * 患者推荐设置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "患者推荐设置 0 未设置, 1 已设置")
    private Integer recommendConfigStatus;
    /**
     * 运营支持 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "运营支持 0 未设置, 1 已设置")
    private Integer operationalSupportStatus;
    /**
     * APP推送配置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "APP推送配置 0 未设置, 1 已设置")
    private Integer pushConfigurationStatus;
    /**
     * 安卓变更日志 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "安卓变更日志 0 未设置, 1 已设置")
    private Integer androidChangeLogStatus;
    /**
     * UNI安卓变更日志 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "UNI安卓变更日志 0 未设置, 1 已设置")
    private Integer uniChangeLogStatus;
    /**
     * UNI配置 0 未设置, 1 已设置
     */
    @ApiModelProperty(value = "UNI配置 0 未设置, 1 已设置")
    private Integer uniConfigurationStatus;

}
