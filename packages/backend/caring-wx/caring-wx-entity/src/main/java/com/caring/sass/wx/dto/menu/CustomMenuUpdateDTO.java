package com.caring.sass.wx.dto.menu;

import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.wx.dto.enums.MenuActionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 项目自定义微信菜单
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
@ApiModel(value = "CustomMenuUpdateDTO", description = "项目自定义微信菜单")
public class CustomMenuUpdateDTO implements Serializable {

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
    @NotNull(message = "菜单的响应动作类型不能为空")
    private MenuActionType type;
    /**
     * 菜单KEY值，用于消息接口推送，不超过128字节
     */
    @ApiModelProperty(value = "菜单KEY值，用于消息接口推送，不超过128字节")
    @Length(max = 50, message = "菜单KEY值，用于消息接口推送，不超过128字节长度不能超过50")
    private String key;
    /**
     * URL
     */
    @ApiModelProperty(value = "URL")
    @Length(max = 1000, message = "URL长度不能超过1000")
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
    private String appid;
    /**
     * 小程序的页面路径
     */
    @ApiModelProperty(value = "小程序的页面路径")
    @Length(max = 500, message = "小程序的页面路径长度不能超过500")
    private String pagepath;

}
