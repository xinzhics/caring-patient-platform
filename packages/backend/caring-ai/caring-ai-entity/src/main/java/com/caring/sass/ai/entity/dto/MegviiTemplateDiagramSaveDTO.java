package com.caring.sass.ai.entity.dto;

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
 * 模版图管理
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MegviiTemplateDiagramSaveDTO", description = "模版图管理")
public class MegviiTemplateDiagramSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    private Integer order;
    /**
     * 模版图的base64编码
     */
    @ApiModelProperty(value = "模版图的base64编码")
    private String imageBase64;
    /**
     * 人脸矩形框的位置
     */
    @ApiModelProperty(value = "人脸矩形框的位置")
    @Length(max = 65535, message = "人脸矩形框的位置长度不能超过65,535")
    private String faceRectangle;
    /**
     * 人脸的关键点坐标数组
     */
    @ApiModelProperty(value = "人脸的关键点坐标数组")
    @Length(max = 65535, message = "人脸的关键点坐标数组长度不能超过65,535")
    private String landmark;
    /**
     * 人脸属性特征
     */
    @ApiModelProperty(value = "人脸属性特征")
    @Length(max = 65535, message = "人脸属性特征长度不能超过65,535")
    private String attributes;
    /**
     * 融合图分类
     */
    @ApiModelProperty(value = "融合图分类")
    private Long templateDiagramType;
    /**
     * Male 男, Female 女
     */
    @ApiModelProperty(value = "Male 男, Female 女")
    @Length(max = 255, message = "Male 男, Female 女长度不能超过255")
    private String gender;

}
