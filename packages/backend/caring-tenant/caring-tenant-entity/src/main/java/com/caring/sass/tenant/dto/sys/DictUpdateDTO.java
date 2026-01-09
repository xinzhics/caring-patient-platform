package com.caring.sass.tenant.dto.sys;

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
 * 系统字典类型
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
@ApiModel(value = "DictUpdateDTO", description = "系统字典类型")
public class DictUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 编码	一颗树仅仅有一个统一的编码
     */
    @ApiModelProperty(value = "编码	一颗树仅仅有一个统一的编码")
    @NotEmpty(message = "编码	一颗树仅仅有一个统一的编码不能为空")
    @Length(max = 64, message = "编码	一颗树仅仅有一个统一的编码长度不能超过64")
    private String type;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    private String name;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 200, message = "描述长度不能超过200")
    private String describe;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Boolean status;
}
