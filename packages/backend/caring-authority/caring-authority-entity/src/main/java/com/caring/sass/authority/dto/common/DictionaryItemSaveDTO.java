package com.caring.sass.authority.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 字典项
 * </p>
 *
 * @author caring
 * @since 2020-01-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DictionaryItemSaveDTO", description = "字典项")
public class DictionaryItemSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    @ApiModelProperty(value = "类型ID")
    @Deprecated
    private Long dictionaryId;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @Deprecated
    private String dictionaryType;
    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @NotEmpty(message = "编码不能为空")
    @Length(max = 64, message = "编码长度不能超过64")
    private String code;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    private String name;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Boolean status;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    private String describe;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;

}
