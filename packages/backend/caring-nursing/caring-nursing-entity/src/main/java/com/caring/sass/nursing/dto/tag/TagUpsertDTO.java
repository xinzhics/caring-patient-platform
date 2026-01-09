package com.caring.sass.nursing.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author leizhi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TagUpsertDTO", description = "标签更新插入")
public class TagUpsertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，存在更新，不存在插入")
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    @NotEmpty
    @Length(max = 255, message = "标签名称长度不能超过255")
    private String name;

    @ApiModelProperty(value = "标签属性列表")
    private List<TagAttrUpsertDTO> tagAttrs;
}
