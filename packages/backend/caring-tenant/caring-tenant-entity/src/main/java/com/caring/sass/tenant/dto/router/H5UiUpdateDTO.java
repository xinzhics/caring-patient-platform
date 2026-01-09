package com.caring.sass.tenant.dto.router;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * UI组件
 * </p>
 *
 * @author leizhi
 * @since 2021-03-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "H5UiUpdateDTO", description = "UI组件")
public class H5UiUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    private String name;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    private String attribute1;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    private String attribute2;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;
    /**
     * ui组件类型：1图片 2其它
     */
    @ApiModelProperty(value = "ui组件类型：1图片 2其它")
    private Integer type;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @NotEmpty(message = "编码不能为空")
    @Length(max = 64, message = "编码长度不能超过64")
    private String code;
}
