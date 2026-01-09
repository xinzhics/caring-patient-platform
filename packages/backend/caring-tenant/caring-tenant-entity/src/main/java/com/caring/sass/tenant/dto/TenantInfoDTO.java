package com.caring.sass.tenant.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @author xinzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantInfoDTO", description = "项目信息")
public class TenantInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * APK的二维码地址
     */
    @ApiModelProperty(value = "APK的二维码地址")
    private String apkScUrl;

    @ApiModelProperty(value = "UniAPK的二维码地址")
    private String uniApkScUrl;
    /**
     * 打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）
     */
    @ApiModelProperty(value = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    private Integer packageStatus;

    @ApiModelProperty(value = "打包状态（0：未打包  1：打包中  2：打包失败  3：打包成功）")
    private Integer uniPackageStatus;

    @ApiModelProperty(value = "项目管理后台url")
    private String mngUrl;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户密码")
    private String password;

    private String orgCode;

}
