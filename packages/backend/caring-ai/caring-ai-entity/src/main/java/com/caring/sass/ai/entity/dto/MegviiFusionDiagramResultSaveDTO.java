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
 * 融合图结果
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
@ApiModel(value = "MegviiFusionDiagramResultSaveDTO", description = "融合图结果")
public class MegviiFusionDiagramResultSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 融合图ID
     */
    @ApiModelProperty(value = "融合图ID")
    @NotNull(message = "融合图ID不能为空")
    private Long fusionDiagramId;
    /**
     * 融合图结果
     */
    @ApiModelProperty(value = "融合图结果")
    private String imageBase64;

}
