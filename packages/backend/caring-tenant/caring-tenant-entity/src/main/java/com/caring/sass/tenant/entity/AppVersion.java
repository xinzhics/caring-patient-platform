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
 * 版本表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-22
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_app_version")
@ApiModel(value = "AppVersion", description = "版本表")
@AllArgsConstructor
public class AppVersion extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id")
    @Length(max = 50, message = "项目id长度不能超过50")
    @TableField(value = "project_id", condition = LIKE)
    @Excel(name = "项目id")
    private String projectId;

    /**
     * 系统平台，0：iOS；1：Andorid
     */
    @ApiModelProperty(value = "系统平台，0：iOS；1：Andorid")
    @TableField("platform")
    @Excel(name = "系统平台，0：iOS；1：Andorid")
    private Integer platform;

    /**
     * 应用标示，android中为应用applicationId
     */
    @ApiModelProperty(value = "应用标示，android中为应用applicationId")
    @Length(max = 50, message = "应用标示，android中为应用applicationId长度不能超过50")
    @TableField(value = "boundle_id", condition = LIKE)
    @Excel(name = "应用标示，android中为应用applicationId")
    private String boundleId;

    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    @Length(max = 50, message = "应用名称长度不能超过50")
    @TableField(value = "app_name", condition = LIKE)
    @Excel(name = "应用名称")
    private String appName;

    /**
     * 显示版本号
     */
    @ApiModelProperty(value = "显示版本号")
    @Length(max = 50, message = "显示版本号长度不能超过50")
    @TableField(value = "version_name", condition = LIKE)
    @Excel(name = "显示版本号")
    private String versionName;

    /**
     * 版本号，android升级覆盖安装必须递增；iOS中为versionName对应的每次build的版本号
     */
    @ApiModelProperty(value = "版本号，android升级覆盖安装必须递增；iOS中为versionName对应的每次build的版本号")
    @TableField("version_code")
    @Excel(name = "版本号，android升级覆盖安装必须递增；iOS中为versionName对应的每次build的版本号")
    private Integer versionCode;

    /**
     * apk url地址
     */
    @ApiModelProperty(value = "apk url地址")
    @Length(max = 500, message = "apk url地址长度不能超过500")
    @TableField(value = "url", condition = LIKE)
    @Excel(name = "apk url地址")
    private String url;

    /**
     * 升级描述
     */
    @ApiModelProperty(value = "升级描述")
    @Length(max = 50, message = "升级描述长度不能超过50")
    @TableField(value = "upgrade_desc", condition = LIKE)
    @Excel(name = "升级描述")
    private String upgradeDesc;

    /**
     * 是否启用，1-启用，0-禁用，表示该次升级是否启用
     */
    @ApiModelProperty(value = "是否启用，1-启用，0-禁用，表示该次升级是否启用")
    @TableField("enable")
    @Excel(name = "是否启用，1-启用，0-禁用，表示该次升级是否启用")
    private Integer enable;

    /**
     * apk二维码地址
     */
    @ApiModelProperty(value = "apk二维码地址")
    @Length(max = 500, message = "apk二维码地址长度不能超过500")
    @TableField(value = "qrcode_image_url", condition = LIKE)
    @Excel(name = "apk二维码地址")
    private String qrcodeImageUrl;
}
