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
import javax.validation.constraints.Pattern;

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
 * 小程序用户openId关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-03-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MiniappInfoSaveDTO", description = "小程序用户openId关联表")
public class MiniappInfoSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 角色(doctor, NursingStaff,patient)
     */
    @ApiModelProperty(value = "角色(doctor, NursingStaff,patient)")
    @Length(max = 200, message = "角色(doctor, NursingStaff,patient)长度不能超过200")
    @Pattern(regexp = "^[^\\s#\\(\\)\\*%,;]+$", message = "roleType参数异常")
    private String roleType;
    /**
     * 小程序openId
     */
    @ApiModelProperty(value = "小程序openId")
    @Length(max = 50, message = "小程序openId长度不能超过50")
    @Pattern(regexp = "^[^\\s#\\(\\)\\*%,;]+$", message = "参数异常")
    private String miniAppOpenId;
    /**
     * 小程序appId
     */
    @ApiModelProperty(value = "小程序appId")
    @Length(max = 50, message = "小程序appId长度不能超过50")
    @Pattern(regexp = "^[^\\s#\\(\\)\\*%,;]+$", message = "参数异常")
    private String miniAppId;

}
