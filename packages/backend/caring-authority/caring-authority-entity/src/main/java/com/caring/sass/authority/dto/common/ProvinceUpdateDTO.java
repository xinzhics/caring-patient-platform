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
 * 地区表
 * </p>
 *
 * @author caring
 * @since 2020-02-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ProvinceUpdateDTO", description = "省")
public class ProvinceUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty("省的code")
    private String provinceCode;

    @ApiModelProperty("省名称")
    private String province;


}
