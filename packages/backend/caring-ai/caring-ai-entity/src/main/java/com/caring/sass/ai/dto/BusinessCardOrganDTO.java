package com.caring.sass.ai.dto;

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
 * AI名片
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessCardOrganDTO", description = "AI名片")
public class BusinessCardOrganDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;


    @ApiModelProperty(value = "机构ID")
    @NotNull(message = "机构不能为空", groups = SuperEntity.Update.class)
    private Long organId;


}
