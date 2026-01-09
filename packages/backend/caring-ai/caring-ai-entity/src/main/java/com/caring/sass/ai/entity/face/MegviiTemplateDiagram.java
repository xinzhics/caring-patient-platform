package com.caring.sass.ai.entity.face;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_megvii_template_diagram")
@ApiModel(value = "MegviiTemplateDiagram", description = "模版图管理")
@AllArgsConstructor
public class MegviiTemplateDiagram extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    @TableField("order_")
    @Excel(name = "优先级")
    private Integer order;

    /**
     * 模版图的base64编码
     */
    @ApiModelProperty(value = "模版图的base64编码")
    @TableField("image_base64")
    @Excel(name = "模版图的base64编码")
    private String imageBase64;


    @ApiModelProperty(value = "图片在obs的地址")
    @TableField("image_obs_url")
    @Excel(name = "图片在obs的地址")
    private String imageObsUrl;

    /**
     * 人脸矩形框的位置
     */
    @ApiModelProperty(value = "人脸矩形框的位置")
    @Length(max = 65535, message = "人脸矩形框的位置长度不能超过65535")
    @TableField("face_rectangle")
    @Excel(name = "人脸矩形框的位置")
    private String faceRectangle;

    /**
     * 人脸的关键点坐标数组
     */
    @ApiModelProperty(value = "人脸的关键点坐标数组")
    @Length(max = 65535, message = "人脸的关键点坐标数组长度不能超过65535")
    @TableField("landmark")
    @Excel(name = "人脸的关键点坐标数组")
    private String landmark;

    /**
     * 人脸属性特征
     */
    @ApiModelProperty(value = "人脸属性特征")
    @Length(max = 65535, message = "人脸属性特征长度不能超过65535")
    @TableField("attributes")
    @Excel(name = "人脸属性特征")
    private String attributes;

    /**
     * 融合图分类
     */
    @ApiModelProperty(value = "融合图分类")
    @TableField("template_diagram_type")
    @Excel(name = "融合图分类")
    private Long templateDiagramType;

    /**
     * Male 男, Female 女
     */
    @ApiModelProperty(value = "Male 男, Female 女")
    @Length(max = 255, message = "Male 男, Female 女长度不能超过255")
    @TableField(value = "gender", condition = LIKE)
    @Excel(name = "Male 男, Female 女")
    private String gender;


    @Builder
    public MegviiTemplateDiagram(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Integer order, String imageBase64, String faceRectangle, String landmark, String attributes, 
                    Long templateDiagramType, String gender) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.order = order;
        this.imageBase64 = imageBase64;
        this.faceRectangle = faceRectangle;
        this.landmark = landmark;
        this.attributes = attributes;
        this.templateDiagramType = templateDiagramType;
        this.gender = gender;
    }

}
