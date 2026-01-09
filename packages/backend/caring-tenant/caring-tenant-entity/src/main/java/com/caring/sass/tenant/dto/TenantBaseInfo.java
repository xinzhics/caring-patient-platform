package com.caring.sass.tenant.dto;

import com.caring.sass.tenant.entity.LibraryTenant;
import com.caring.sass.tenant.entity.Tenant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @className: TenantBaseInfo
 * @author: 杨帅
 * @date: 2023/7/19
 */
@Data
@ApiModel("项目基本信息模型")
public class TenantBaseInfo {

    @ApiModelProperty("租户的ID")
    Long tenantId;

    @ApiModelProperty("租户的code编码")
    String tenantCode;

    @ApiModelProperty("项目名称")
    @Length(max = 20, message = "项目编码长度不能超过20")
    String name;

    @ApiModelProperty("项目LOGO")
    String logo;

    @ApiModelProperty("后台地址")
    String projectManageLink;

    @ApiModelProperty("项目域名")
    @Length(max = 50, message = "域名长度不能超过50")
    String domainName;


    @ApiModelProperty(value = "入组引导")
    @Length(max = 100, message = "入组引导长度不能超过100")
    private String guide;

    /**
     * 入组描素
     */
    @ApiModelProperty(value = "入组描述")
    @Length(max = 100, message = "入组描述长度不能超过100")
    private String describe;


    @ApiModelProperty(value = "入组相关数据的ID")
    private Long guideId;

    /**
     * 引导图片
     */
    @ApiModelProperty(value = "引导图片不存在时，显示项目logo")
    private String icon;


    @ApiModelProperty(value = "是否添加项目介绍 0:添加  1：不添加")
    private Integer enableIntro;

    /**
     * 项目介绍
     */
    @ApiModelProperty(value = "项目介绍")
    private String intro;

    @ApiModelProperty(value = "协议")
    @Length(max = 65535, message = "协议长度不能超过65535")
    private String agreement;

    @ApiModelProperty(value = "医生协议")
    @Length(max = 65535, message = "协议长度不能超过65535")
    private String doctorAgreement;

    @ApiModelProperty(value = "入组成功消息提示")
    @Length(max = 500, message = "消息内容长度不能超过500")
    private String successMsg;

    @ApiModelProperty(value = "项目内容库")
    private List<LibraryTenant> libraryTenants;


}
