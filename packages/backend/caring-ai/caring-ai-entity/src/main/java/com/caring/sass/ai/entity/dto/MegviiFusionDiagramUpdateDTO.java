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
import com.caring.sass.base.entity.SuperEntity;
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
 * 融合图管理
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
@ApiModel(value = "MegviiFusionDiagramUpdateDTO", description = "融合图管理")
public class MegviiFusionDiagramUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 人脸融合模版图IDS
     */
    @ApiModelProperty(value = "人脸融合模版图分类IDS")
    private String templateDiagramTypeIds;
    /**
     * 人脸融合图base64
     */
    @ApiModelProperty(value = "人脸融合图base64")
    private String imageBase64;
    /**
     * 失败的异常信息
     */
    @ApiModelProperty(value = "失败的异常信息")
    @Length(max = 300, message = "失败的异常信息长度不能超过300")
    private String errorMessage;

    private Long userId;
}
