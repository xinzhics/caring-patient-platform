package com.caring.sass.wx.dto.menu;

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
import com.caring.sass.base.entity.SuperEntity;
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
 * 系统定义的微信菜单
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SysMenuUpdateDTO", description = "系统定义的微信菜单")
public class SysMenuUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 菜单标题
     */
    @ApiModelProperty(value = "菜单标题")
    @Length(max = 50, message = "菜单标题长度不能超过50")
    private String name;
    /**
     * 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
     */
    @ApiModelProperty(value = "菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型")
    @Length(max = 50, message = "菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型长度不能超过50")
    private String type;
    /**
     * 菜单类型（0：会员菜单  1：医生菜单）
     */
    @ApiModelProperty(value = "菜单类型（0：会员菜单  1：医生菜单）")
    @Length(max = 50, message = "菜单类型（0：会员菜单  1：医生菜单）长度不能超过50")
    private String buttonType;
    /**
     * URL
     */
    @ApiModelProperty(value = "URL")
    @Length(max = 500, message = "URL长度不能超过500")
    private String url;
    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
    /**
     * 小程序的appid（仅认证公众号可配置）
     */
    @ApiModelProperty(value = "小程序的appid（仅认证公众号可配置）")
    @Length(max = 500, message = "小程序的appid（仅认证公众号可配置）长度不能超过500")
    private String appId;
    /**
     * 小程序的页面路径
     */
    @ApiModelProperty(value = "小程序的页面路径")
    @Length(max = 500, message = "小程序的页面路径长度不能超过500")
    private String pagePath;
}
