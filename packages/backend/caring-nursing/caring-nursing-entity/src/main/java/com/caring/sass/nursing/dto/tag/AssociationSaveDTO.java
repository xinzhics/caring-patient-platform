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
 * 业务关联标签记录表
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
@ApiModel(value = "AssociationSaveDTO", description = "业务关联标签记录表")
public class AssociationSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签Id
     */
    @ApiModelProperty(value = "标签Id")
    private Long tagId;
    /**
     * 业务类型（类名）
     */
    @ApiModelProperty(value = "业务类型（类名）")
    @Length(max = 255, message = "业务类型（类名）长度不能超过255")
    private String associationType;
    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 32, message = "业务Id长度不能超过32")
    private String associationId;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @Length(max = 3, message = "排序长度不能超过3")
    private String order;
    /**
     * 业务获取到标签时，业务提供的数值
     */
    @ApiModelProperty(value = "业务获取到标签时，业务提供的数值")
    @Length(max = 255, message = "业务获取到标签时，业务提供的数值长度不能超过255")
    private String attrValue;

}
