package com.caring.sass.wx.dto.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @className: MiniAppPageList
 * @author: 杨帅
 * @date: 2024/3/22
 */
@Data
@ApiModel
public class MiniAppSaveDTO {

    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    @NotNull
    @NotEmpty
    @Length(max = 200, message = "应用ID长度不能超过200")
    @Excel(name = "应用ID")
    private String appId;

    /**
     * 对应微信公众号的App Secret
     */
    @NotNull
    @NotEmpty
    @ApiModelProperty(value = "对应微信公众号的App Secret")
    @Length(max = 200, message = "对应微信公众号的App Secret长度不能超过200")
    private String appSecret;

    /**
     * 公众号名字
     */
    @NotNull
    @NotEmpty
    @ApiModelProperty(value = "小程序名称")
    @Excel(name = "公众号名字")
    private String name;

    @NotNull
    @NotEmpty
    @ApiModelProperty("所属于的租户")
    @TableField("tenant_code")
    private String tenantCode;


}
