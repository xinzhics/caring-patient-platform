package com.caring.sass.wx.dto.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @className: MiniAppPageList
 * @author: 杨帅
 * @date: 2024/3/22
 */
@Data
@ApiModel
public class MiniAppPageList {

    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    @Length(max = 200, message = "应用ID长度不能超过200")
    @TableField(value = "app_id", condition = LIKE)
    @Excel(name = "应用ID")
    private String appId;

    /**
     * 公众号名字
     */
    @ApiModelProperty(value = "公众号名字")
    @Length(max = 200, message = "公众号名字长度不能超过200")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "公众号名字")
    private String name;

    @ApiModelProperty(value = "第三方授权的公众号，默认为false表示之前手动配置的公众号")
    @TableField("third_authorization")
    private Boolean thirdAuthorization;


    @ApiModelProperty("所属于的租户")
    @TableField("tenant_code")
    private String tenantCode;

    @ApiModelProperty("所属的项目")
    private String tenantName;

}
