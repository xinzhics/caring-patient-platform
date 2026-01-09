package com.caring.sass.tenant.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName TenantDetailDTO
 * @Description
 * @Author yangShuai
 * @Date 2020/9/23 13:25
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantDetailDTO", description = "项目信息")
public class TenantDetailDTO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String name;

    @ApiModelProperty(value = "域名")
    private String domainName;

    /**
     * 状态
     * #{NORMAL:正常;FORBIDDEN:禁用;WAITING:待审核;REFUSE:拒绝}
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态", width = 20, replace = {"正常_NORMAL", "待初始化_WAIT_INIT", "禁用_FORBIDDEN", "待审核_WAITING", "拒绝_REFUSE", "DELETE_已删除", "_null"})
    private TenantStatusEnum status;

    @ApiModelProperty(value = "用户名")
    private String projectUserName;


    @ApiModelProperty(value = "密码")
    private String projectUserPassword;

    @ApiModelProperty(value = "机构码")
    private String orgCode;

    @ApiModelProperty(value = "安卓app下载的url")
    private String appLoadUrl;




}
