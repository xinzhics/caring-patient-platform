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
import java.util.List;

/**
 * <p>
 * 实体类
 * 模板消息
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
@ApiModel(value = "TemplateMsgSaveDTO", description = "模板消息")
public class TemplateMsgSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对应微信公众平台中的模板Id
     */
    @ApiModelProperty(value = "对应微信公众平台中的模板Id")
    @Length(max = 200, message = "对应微信公众平台中的模板Id长度不能超过200")
    private String templateId;
    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 32, message = "业务Id长度不能超过32")
    private String businessId;
    /**
     * 模板json
     */
    @ApiModelProperty(value = "模板json")
    @Length(max = 65535, message = "模板json长度不能超过65,535")
    private String extraJson;

    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
    @Length(max = 50, message = "标识长度不能超过50")
    private String indefiner;
    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @Length(max = 255, message = "模板名称长度不能超过255")
    private String title;
    /**
     * 原模板消息Id
     */
    @ApiModelProperty(value = "原模板消息Id")
    @Length(max = 32, message = "原模板消息Id长度不能超过32")
    private String sourceId;

    @ApiModelProperty(value = "模板属性")
    private List<TemplateMsgFieldsSaveDTO> fields;

}
