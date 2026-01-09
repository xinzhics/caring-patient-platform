package com.caring.sass.ai.dto.know;

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
 * 日常收集文本内容
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeDailyCollectionTitleDTO", description = "日常收集文本内容")
public class KnowledgeDailyCollectionTitleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 语音文本内容
     */
    @ApiModelProperty(value = "语音文本内容")
    @NotNull
    private String textContent;

}
