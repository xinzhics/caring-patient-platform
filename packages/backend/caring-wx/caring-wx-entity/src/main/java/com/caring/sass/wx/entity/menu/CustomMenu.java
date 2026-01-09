package com.caring.sass.wx.entity.menu;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.wx.dto.enums.MenuActionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_custom_menu")
@ApiModel(value = "CustomMenu", description = "项目自定义微信菜单")
@AllArgsConstructor
public class CustomMenu extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单标题
     */
    @ApiModelProperty(value = "菜单标题")
    @Length(max = 50, message = "菜单标题长度不能超过50")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "菜单标题")
    private String name;

    /**
     * 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
     */
    @ApiModelProperty(value = "菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型")
    @NotNull(message = "菜单的响应动作类型不能为空")
    @TableField(value = "type", condition = LIKE)
    @Excel(name = "菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型")
    private MenuActionType type;

    /**
     * 菜单KEY值，用于消息接口推送，不超过128字节
     */
    @ApiModelProperty(value = "菜单KEY值，用于消息接口推送，不超过128字节")
    @Length(max = 50, message = "菜单KEY值，用于消息接口推送，不超过128字节长度不能超过50")
    @TableField(value = "key_", condition = LIKE)
    @Excel(name = "菜单KEY值，用于消息接口推送，不超过128字节")
    private String key;

    /**
     * URL
     */
    @ApiModelProperty(value = "URL")
    @Length(max = 500, message = "URL长度不能超过500")
    @TableField(value = "url", condition = LIKE)
    @Excel(name = "URL")
    private String url;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    @TableField("del_flag")
    @Excel(name = "删除标志")
    private Integer delFlag;

    /**
     * 小程序的appid（仅认证公众号可配置）
     */
    @ApiModelProperty(value = "小程序的appid（仅认证公众号可配置）禁止使用驼峰")
    @Length(max = 500, message = "小程序的appid（仅认证公众号可配置）长度不能超过500")
    @TableField(value = "app_id", condition = LIKE)
    @Excel(name = "小程序的appid（仅认证公众号可配置）")
    private String appid;

    /**
     * 小程序的页面路径
     */
    @ApiModelProperty(value = "小程序的页面路径 禁止使用驼峰")
    @Length(max = 500, message = "小程序的页面路径长度不能超过500")
    @TableField(value = "page_path", condition = LIKE)
    @Excel(name = "小程序的页面路径")
    private String pagepath;

    @ApiModelProperty(value = "所在的租户")
    @TableField(exist = false)
    private String tenantCode;

    @Builder
    public CustomMenu(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime,
                      String name, MenuActionType type, String key, String url, Integer delFlag,
                      String appid, String pagepath) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.type = type;
        this.key = key;
        this.url = url;
        this.delFlag = delFlag;
        this.appid = appid;
        this.pagepath = pagepath;
    }

}
