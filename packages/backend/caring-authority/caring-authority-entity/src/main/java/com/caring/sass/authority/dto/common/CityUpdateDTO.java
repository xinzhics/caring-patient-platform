package com.caring.sass.authority.dto.common;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "CityUpdateDTO", description = "市")
public class CityUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty("市区名称")
    private String city;

    @ApiModelProperty("市区code")
    private String cityCode;

    @ApiModelProperty("省ID")
    private Long provinceId;

    @ApiModelProperty("省code")
    private String provinceCode;

    @ApiModelProperty("省名称")
    private String provinceName;

}
