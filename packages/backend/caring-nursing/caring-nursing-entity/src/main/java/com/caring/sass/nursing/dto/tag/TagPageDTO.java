package com.caring.sass.nursing.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

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
@ApiModel(value = "TagPageDTO", description = "标签管理")
public class TagPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    @Length(max = 255, message = "标签名称长度不能超过255")
    private String name;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 255, message = "图标长度不能超过255")
    private String icon;


}
