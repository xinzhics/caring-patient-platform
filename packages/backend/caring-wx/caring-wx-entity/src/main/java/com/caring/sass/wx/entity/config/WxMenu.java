package com.caring.sass.wx.entity.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
 * 微信公众号菜单
 * </p>
 *
 * @author 杨帅
 * @since 2024-06-25
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_menu")
@ApiModel(value = "WxMenu", description = "微信公众号菜单")
@AllArgsConstructor
public class WxMenu extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单标题
     */
    @ApiModelProperty(value = "菜单标题")
    @Length(max = 50, message = "菜单标题长度不能超过50")
    @TableField(value = "menu_name", condition = LIKE)
    @Excel(name = "菜单标题")
    private String menuName;

    /**
     *  菜单的响应动作类型.view表示网页类型，click表示点击类型，miniprogram表示小程序类型
     */
    @ApiModelProperty(value = " 菜单的响应动作类型.view表示网页类型，click表示点击类型，miniprogram表示小程序类型")
    @Length(max = 50, message = " 菜单的响应动作类型.view表示网页类型，click表示点击类型，miniprogram表示小程序类型长度不能超过50")
    @TableField(value = "menu_type", condition = LIKE)
    @Excel(name = " 菜单的响应动作类型.view表示网页类型，click表示点击类型，miniprogram表示小程序类型")
    private String menuType;

    /**
     * 菜单KEY值，用于消息接口推送，不超过128字节.click等点击类型必须
     */
    @ApiModelProperty(value = "菜单KEY值，用于消息接口推送，不超过128字节.click等点击类型必须")
    @Length(max = 50, message = "菜单KEY值，用于消息接口推送，不超过128字节.click等点击类型必须长度不能超过50")
    @TableField(value = "menu_key", condition = LIKE)
    @Excel(name = "菜单KEY值，用于消息接口推送，不超过128字节.click等点击类型必须")
    private String menuKey;

    /**
     * 网页链接.
     */
    @ApiModelProperty(value = "网页链接.")
    @Length(max = 500, message = "网页链接.长度不能超过500")
    @TableField(value = "menu_url", condition = LIKE)
    @Excel(name = "网页链接.")
    private String menuUrl;

    /**
     * 新增永久素材接口返回的合法media_id.
     */
    @ApiModelProperty(value = "新增永久素材接口返回的合法media_id.")
    @Length(max = 50, message = "新增永久素材接口返回的合法media_id.长度不能超过50")
    @TableField(value = "media_id", condition = LIKE)
    @Excel(name = "新增永久素材接口返回的合法media_id.")
    private String mediaId;

    /**
     * 发布图文接口获得的article_id.
     */
    @ApiModelProperty(value = "发布图文接口获得的article_id.")
    @Length(max = 50, message = "发布图文接口获得的article_id.长度不能超过50")
    @TableField(value = "article_id", condition = LIKE)
    @Excel(name = "发布图文接口获得的article_id.")
    private String articleId;

    /**
     * 小程序的appid.miniprogram类型必须
     */
    @ApiModelProperty(value = "小程序的appid.miniprogram类型必须")
    @Length(max = 50, message = "小程序的appid.miniprogram类型必须长度不能超过50")
    @TableField(value = "appid", condition = LIKE)
    @Excel(name = "小程序的appid.miniprogram类型必须")
    private String appid;

    /**
     * 小程序的页面路径.miniprogram类型必须
     */
    @ApiModelProperty(value = "小程序的页面路径.miniprogram类型必须")
    @Length(max = 50, message = "小程序的页面路径.miniprogram类型必须长度不能超过50")
    @TableField(value = "pagepath", condition = LIKE)
    @Excel(name = "小程序的页面路径.miniprogram类型必须")
    private String pagepath;

    /**
     * 文本内容
     */
    @ApiModelProperty(value = "文本内容")
    @Length(max = 1000, message = "文本内容长度不能超过1000")
    @TableField(value = "text_content", condition = LIKE)
    @Excel(name = "文本内容")
    private String textContent;

    @ApiModelProperty(value = "公众号ID")
    @Length(max = 1000, message = "公众号ID")
    @TableField(value = "wx_app_id", condition = EQUAL)
    private String wxAppId;


}
