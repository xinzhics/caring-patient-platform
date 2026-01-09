package com.caring.sass.nursing.dto.tag;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 标签管理
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TagUpdateDTO", description = "标签管理")
public class TagUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    @NotEmpty
    @Length(max = 255, message = "标签名称长度不能超过255")
    private String name;
    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    private Integer order;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 255, message = "图标长度不能超过255")
    private String icon;

}
