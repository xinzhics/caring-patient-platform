package com.caring.sass.ai.entity.face;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 融合图结果
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-30
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_megvii_fusion_diagram_result")
@ApiModel(value = "MegviiFusionDiagramResult", description = "融合图结果")
@AllArgsConstructor
public class MegviiFusionDiagramResult extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 融合图ID
     */
    @ApiModelProperty(value = "融合图ID")
    @NotNull(message = "融合图ID不能为空")
    @TableField("fusion_diagram_id")
    @Excel(name = "融合图ID")
    private Long fusionDiagramId;

    @ApiModelProperty(value = "融合图分类ID")
    @NotNull(message = "融合图分类")
    @TableField("fusion_diagram_type_id")
    private Long fusionDiagramTypeId;

    @ApiModelProperty(value = "融合图模版id")
    @TableField("fusion_diagram_template_id")
    private Long fusionDiagramTemplateId;

    /**
     * 融合图结果
     */
    @ApiModelProperty(value = "融合图结果")
    @TableField("image_base64")
    @Excel(name = "融合图结果")
    private String imageBase64;

    @ApiModelProperty(value = "图片在obs的地址")
    @TableField("image_obs_url")
    @Excel(name = "图片在obs的地址")
    private String imageObsUrl;

    @ApiModelProperty(value = "融合异常的信息")
    @TableField("error_message")
    private String errorMessage;

    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;



}
