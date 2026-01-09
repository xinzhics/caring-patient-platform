package com.caring.sass.authority.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 字典类型
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
@ApiModel(value = "CitySaveDTO", description = "市")
public class CitySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("市区名称")
    @NotEmpty
    private String city;

    @ApiModelProperty("市区code")
    @NotEmpty
    private String cityCode;

    @ApiModelProperty("省ID")
    @NotNull
    private Long provinceId;

    @ApiModelProperty("省code")
    @NotEmpty
    private String provinceCode;

    @ApiModelProperty("省名称")
    @NotEmpty
    private String provinceName;


}
