package com.caring.sass.wx.dto.template;

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
 * 模板消息 属性表通过 templateId 和 TemplateMessage 表关联。
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
@ApiModel(value = "TemplateMsgFieldsSaveDTO", description = "模板消息 属性表通过 templateId 和 TemplateMessage 表关联。")
public class TemplateMsgFieldsSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对应微信公众平台中的模板Id
     */
    @ApiModelProperty(value = "对应微信公众平台中的模板Id")
    @Length(max = 200, message = "对应微信公众平台中的模板Id长度不能超过200")
    private Long templateId;
    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    @Length(max = 100, message = "标签长度不能超过100")
    private String label;
    /**
     * 属性
     */
    @ApiModelProperty(value = "属性")
    @Length(max = 200, message = "属性长度不能超过200")
    private String attr;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private Integer type;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 65535, message = "属性值长度不能超过65,535")
    private String value;
    /**
     * 颜色值
     */
    @ApiModelProperty(value = "颜色值")
    @Length(max = 20, message = "颜色值长度不能超过20")
    private String color;
    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 32, message = "业务Id长度不能超过32")
    private String businessId;

}
