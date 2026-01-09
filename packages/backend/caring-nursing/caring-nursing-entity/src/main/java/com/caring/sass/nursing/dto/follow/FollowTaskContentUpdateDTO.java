package com.caring.sass.nursing.dto.follow;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 随访任务内容列表
 * </p>
 *
 * @author 杨帅
 * @since 2023-01-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FollowTaskContentUpdateDTO", description = "随访任务内容列表")
public class FollowTaskContentUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 显示或隐藏（0：隐藏  1：显示）
     */
    @ApiModelProperty(value = "显示或隐藏（0：隐藏  1：显示）")
    private Integer showContent;


    @ApiModelProperty(value = "项目code")
    private String tenantCode;

}
