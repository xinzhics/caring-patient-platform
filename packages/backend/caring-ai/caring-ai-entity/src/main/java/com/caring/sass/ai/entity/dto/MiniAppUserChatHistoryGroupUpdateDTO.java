package com.caring.sass.ai.entity.dto;

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
 * 敏智用户历史对话
 * </p>
 *
 * @author 杨帅
 * @since 2024-03-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MiniAppUserChatHistoryGroupUpdateDTO", description = "敏智用户历史对话")
public class MiniAppUserChatHistoryGroupUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 对话的历史标题
     */
    @ApiModelProperty(value = "对话的历史标题")
    private String contentTitle;
}
