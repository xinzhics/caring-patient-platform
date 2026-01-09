package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "u_user_nursing_staff", autoResultMap = true)
@ApiModel(value = "NursingStaff", description = "用户-医助")
@AllArgsConstructor
public class NursingStaff extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @Length(max = 50, message = "用户名长度不能超过50")
    @TableField(value = "login_name", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户名")
    private String loginName;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Length(max = 100, message = "密码长度不能超过100")
    @TableField(value = "password", condition = LIKE)
    @Excel(name = "密码")
    private String password;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 100, message = "姓名长度不能超过100")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "姓名")
    private String name;

    /**
     * 头像url
     */
    @ApiModelProperty(value = "头像url")
    @Length(max = 500, message = "头像url长度不能超过500")
    @TableField(value = "avatar", condition = LIKE)
    @Excel(name = "头像url")
    private String avatar;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 20, message = "手机号码长度不能超过20")
    @TableField(value = "mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "手机号码")
    private String mobile;

    /**
     * 用户密码强度
     */
    @ApiModelProperty(value = "用户密码强度")
    @Length(max = 10, message = "用户密码强度长度不能超过10")
    @TableField(value = "password_strong_level", condition = LIKE)
    @Excel(name = "用户密码强度")
    private String passwordStrongLevel;

    /**
     * IM的账号
     */
    @ApiModelProperty(value = "IM的账号")
    @Length(max = 200, message = "IM的账号长度不能超过200")
    @TableField(value = "im_account", condition = LIKE)
    @Excel(name = "IM的账号")
    private String imAccount;

    /**
     * 手势密码
     */
    @ApiModelProperty(value = "手势密码")
    @Length(max = 100, message = "手势密码长度不能超过100")
    @TableField(value = "gesture_pwd", condition = LIKE)
    @Excel(name = "手势密码")
    private String gesturePwd;

    /**
     *  权限代码
     */
    @ApiModelProperty(value = " 权限代码 ")
    @Length(max = 1000, message = " 权限代码 长度不能超过1000")
    @TableField(value = "class_code", condition = LIKE)
    @Excel(name = " 权限代码 ")
    private String classCode;


    /**
     * 登录次数
     */
    @ApiModelProperty(value = "登录次数")
    @TableField("total_login_times")
    @Excel(name = "登录次数")
    private Integer totalLoginTimes;

    /**
     * 所属单位ID
     */
    @ApiModelProperty(value = "所属单位ID")
    @TableField("org_id")
    @Excel(name = "所属单位ID")
    private Long organId;

    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "organ_name", condition = LIKE)
    @Excel(name = "")
    private String organName;

    /**
     *  所属机构代码
     */
    @ApiModelProperty(value = " 所属机构代码")
    @Length(max = 50, message = " 所属机构代码长度不能超过50")
    @TableField(value = "organ_code", condition = LIKE)
    @Excel(name = " 所属机构代码")
    private String organCode;

    /**
     * 性别 0:男 1：女
     */
    @ApiModelProperty(value = "性别 0:男 1：女")
    @TableField("sex")
    @Excel(name = "性别 0:男 1：女")
    private Integer sex;

    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    @TableField(value = "birthday", condition = LIKE)
    @Excel(name = "出生年月")
    private String birthday;

    /**
     * 额外信息
     */
    @ApiModelProperty(value = "额外信息")

    @TableField("extra_info")
    @Excel(name = "额外信息")
    private String extraInfo;

    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "last_login_ip", condition = LIKE)
    @Excel(name = "")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("last_login_time")
    @Excel(name = "创建时间")
    private Long lastLoginTime;

    @ApiModelProperty(value = "是否展开聊天小组, 0 关闭，1展示")
    @TableField(value = "im_group_status", condition = EQUAL)
    private Integer imGroupStatus;

    @ApiModelProperty(value = "最新访问日期")
    @TableField(value = "latest_access_time")
    private LocalDate latestAccessTime;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

    @ApiModelProperty(value = "微信关注状态 0 未关注， 1 关注, 2 取消关注")
    @TableField("wx_status")
    private Integer wxStatus;

    @ApiModelProperty(value = "是否接收Im微信模板消息 0 不接收, 1接收")
    @TableField(value = "im_wx_template_status", condition = EQUAL)
    private Integer imWxTemplateStatus;

    @ApiModelProperty(value = "微信openid")
    @Length(max = 32, message = "微信openid长度不能超过32")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "微信openid")
    private String openId;

    @ApiModelProperty(value = "全部会员数量")
    @TableField(exist = false)
    private Long patientCount;

    @ApiModelProperty(value = "粉丝数量")
    @TableField(exist = false)
    private Integer fansCount;

    @ApiModelProperty(value = "医生数量")
    @TableField(exist = false)
    private Integer doctorCount;

    @ApiModelProperty(value = "已注册患者数")
    @TableField(exist = false)
    private Integer rPatientCount;
}
