package com.caring.sass.wx.entity.menu;

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
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_sys_menu")
@ApiModel(value = "SysMenu", description = "系统定义的微信菜单")
@AllArgsConstructor
public class SysMenu extends Entity<Long> {

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
    @Length(max = 50, message = "菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型长度不能超过50")
    @TableField(value = "type", condition = LIKE)
    @Excel(name = "菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型")
    private String type;

    /**
     * 菜单类型（0：会员菜单  1：医生菜单）
     */
    @ApiModelProperty(value = "菜单类型（0：会员菜单  1：医生菜单）")
    @Length(max = 50, message = "菜单类型（0：会员菜单  1：医生菜单）长度不能超过50")
    @TableField(value = "button_type", condition = LIKE)
    @Excel(name = "菜单类型（0：会员菜单  1：医生菜单）")
    private String buttonType;

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
    @ApiModelProperty(value = "小程序的appid（仅认证公众号可配置）")
    @Length(max = 500, message = "小程序的appid（仅认证公众号可配置）长度不能超过500")
    @TableField(value = "app_id", condition = LIKE)
    @Excel(name = "小程序的appid（仅认证公众号可配置）")
    private String appId;

    /**
     * 小程序的页面路径
     */
    @ApiModelProperty(value = "小程序的页面路径")
    @Length(max = 500, message = "小程序的页面路径长度不能超过500")
    @TableField(value = "page_path", condition = LIKE)
    @Excel(name = "小程序的页面路径")
    private String pagePath;


    @Builder
    public SysMenu(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String name, String type, String buttonType, String url, Integer delFlag, 
                    String appId, String pagePath) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.type = type;
        this.buttonType = buttonType;
        this.url = url;
        this.delFlag = delFlag;
        this.appId = appId;
        this.pagePath = pagePath;
    }

}
