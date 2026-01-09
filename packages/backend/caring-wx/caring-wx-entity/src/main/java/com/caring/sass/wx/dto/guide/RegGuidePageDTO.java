package com.caring.sass.wx.dto.guide;

import java.time.LocalDateTime;
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
 * 微信注册引导
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
@ApiModel(value = "RegGuidePageDTO", description = "微信注册引导")
public class RegGuidePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入组引导
     */
    @ApiModelProperty(value = "入组引导")
    @Length(max = 100, message = "入组引导长度不能超过100")
    private String guide;
    /**
     * 入组描素
     */
    @ApiModelProperty(value = "入组描素")
    @Length(max = 100, message = "入组描素长度不能超过100")
    private String describe;
    /**
     * 引导图片
     */
    @ApiModelProperty(value = "引导图片")
    @Length(max = 500, message = "引导图片长度不能超过500")
    private String icon;
    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    @Length(max = 200, message = "链接地址长度不能超过200")
    private String url;
    /**
     * 删除标记(0：未删除)
     */
    @ApiModelProperty(value = "删除标记(0：未删除)")
    private Integer delFlag;
    /**
     * 是否添加项目介绍 0:添加  1：不添加
     */
    @ApiModelProperty(value = "是否添加项目介绍 0:添加  1：不添加")
    private Integer enableIntro;
    /**
     * 项目介绍url
     */
    @ApiModelProperty(value = "项目介绍url")
    private String intro;
    /**
     * 协议
     */
    @ApiModelProperty(value = "协议")
    @Length(max = 65535, message = "协议长度不能超过65,535")
    private String agreement;
    /**
     * 护理目标
     */
    @ApiModelProperty(value = "护理目标")
    @Length(max = 500, message = "护理目标长度不能超过500")
    private String nursingTarget;
    /**
     * 入组成功消息类型 0：图文消息   1：文字消息
     */
    @ApiModelProperty(value = "入组成功消息类型 0：图文消息   1：文字消息")
    private Integer successMsgType;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @Length(max = 500, message = "消息内容长度不能超过500")
    private String successMsg;

}
