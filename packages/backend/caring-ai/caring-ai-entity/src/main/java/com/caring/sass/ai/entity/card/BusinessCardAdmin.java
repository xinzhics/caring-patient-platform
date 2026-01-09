package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 科普名片管理员
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-26
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_business_card_admin", autoResultMap = true)
@ApiModel(value = "BusinessCardAdmin", description = "科普名片管理员")
@AllArgsConstructor
public class BusinessCardAdmin extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    @TableField("organ_id")
    @Excel(name = "机构id")
    private Long organId;

    /**
     * 账号的手机号
     */
    @ApiModelProperty(value = "账号的手机号")
    @NotEmpty(message = "账号的手机号不能为空")
    @Length(max = 255, message = "账号的手机号长度不能超过255")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "账号的手机号")
    private String userMobile;

    /**
     * 账号类型 超管 机构管理员
     */
    @ApiModelProperty(value = "账号类型 超管 机构管理员")
    @TableField(value = "user_type", condition = LIKE)
    @Excel(name = "账号类型 超管 机构管理员")
    private BusinessCardUserType userType;

    @ApiModelProperty(value = "用户密码")
    @Length(max = 300, message = "用户密码长度不能超过300")
    @TableField(value = "password", condition = EQUAL)
    private String password;



}
