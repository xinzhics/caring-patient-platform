package com.caring.sass.ai.dto.textual;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 文献解读用户声音
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TextualInterpretationUserVoicePageDTO", description = "文献解读用户声音")
public class TextualInterpretationUserVoicePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 用户id
     */
    @NotNull
    @ApiModelProperty(value = "用户id")
    private Long userId;


    @ApiModelProperty(value = "音频名称")
    private String name;

    @ApiModelProperty(value = "删除状态")
    private Boolean deleteStatus = false;

}
