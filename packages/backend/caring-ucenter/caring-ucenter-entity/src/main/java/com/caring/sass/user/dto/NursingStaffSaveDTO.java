package com.caring.sass.user.dto;

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
 * 用户-医助
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "NursingStaffSaveDTO", description = "用户-医助")
public class NursingStaffSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @Length(max = 50, message = "用户名长度不能超过50")
    private String loginName;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Length(max = 100, message = "密码长度不能超过100")
    private String password;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 100, message = "姓名长度不能超过100")
    private String name;
    /**
     * 头像url
     */
    @ApiModelProperty(value = "头像url")
    @Length(max = 500, message = "头像url长度不能超过500")
    private String avatar;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 20, message = "手机号码长度不能超过20")
    private String mobile;
    /**
     * 用户密码强度
     */
    @ApiModelProperty(value = "用户密码强度")
    @Length(max = 10, message = "用户密码强度长度不能超过10")
    private String passwordStrongLevel;
    /**
     * IM的账号
     */
    @ApiModelProperty(value = "IM的账号")
    @Length(max = 200, message = "IM的账号长度不能超过200")
    private String imAccount;
    /**
     * 手势密码
     */
    @ApiModelProperty(value = "手势密码")
    @Length(max = 100, message = "手势密码长度不能超过100")
    private String gesturePwd;
    /**
     *  权限代码 
     */
    @ApiModelProperty(value = " 权限代码 ")
    @Length(max = 1000, message = " 权限代码 长度不能超过1000")
    private String classCode;
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    @Length(max = 20, message = "身份证号长度不能超过20")
    private String certificate;
    /**
     * 所属单位ID
     */
    @ApiModelProperty(value = "所属单位ID")
    private Long organId;
    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    private String organName;
    /**
     *  所属机构代码
     */
    @ApiModelProperty(value = " 所属机构代码")
    @Length(max = 50, message = " 所属机构代码长度不能超过50")
    private String organCode;
    /**
     * 性别 0:男 1：女
     */
    @ApiModelProperty(value = "性别 0:男 1：女")
    private Integer sex;
    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    private String birthday;

}
