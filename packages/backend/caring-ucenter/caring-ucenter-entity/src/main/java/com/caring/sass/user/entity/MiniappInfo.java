package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 小程序用户openId关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-03-22
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_miniapp_info")
@ApiModel(value = "MiniappInfo", description = "小程序用户表")
@AllArgsConstructor
public class MiniappInfo extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID 通过手机号关联到的项目医生id 或知识库医生ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;


    @ApiModelProperty(value = "用戶名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "用戶名称")
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 角色(doctor, NursingStaff,patient)
     */
    @ApiModelProperty(value = "角色(doctor, NursingStaff,patient)")
    @Length(max = 200, message = "角色(doctor, NursingStaff,patient)长度不能超过200")
    @TableField(value = "role_type", condition = LIKE)
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    @Excel(name = "角色(doctor, NursingStaff,patient)")
    private String roleType;


    @ApiModelProperty(value = "用户的手机号")
    @TableField(value = "phone_number", condition = EQUAL)
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    private String phoneNumber;


    @ApiModelProperty(value = "是否已经给用户提示过关注公众号信息")
    @TableField(value = "remind_subscription_massage", condition = EQUAL)
    private Integer remindSubscriptionMassage;

    /**
     * 小程序openId
     */
    @ApiModelProperty(value = "小程序openId")
    @Length(max = 50, message = "小程序openId长度不能超过50")
    @TableField(value = "mini_app_open_id", condition = LIKE)
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    @Excel(name = "小程序openId")
    private String miniAppOpenId;


    @ApiModelProperty("小程序登录后的秘钥")
    @TableField(value = "session_key", condition = LIKE)
    private String sessionKey;


    /**
     * 小程序appId
     */
    @ApiModelProperty(value = "小程序appId")
    @Length(max = 50, message = "小程序appId长度不能超过50")
    @TableField(value = "mini_app_id", condition = LIKE)
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    @Excel(name = "小程序appId")
    private String miniAppId;


    @TableField(exist = false)
    private String tenantCode;


    @TableField(exist = false)
    private String clientType;


}
