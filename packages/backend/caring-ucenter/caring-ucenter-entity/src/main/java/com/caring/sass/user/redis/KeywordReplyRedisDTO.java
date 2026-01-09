package com.caring.sass.user.redis;

import com.caring.sass.user.dto.KeywordSettingSaveDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 实体类
 * 关键字回复内容
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KeywordReplyRedisDTO", description = "关键字回复内容redis存储")
public class KeywordReplyRedisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名")
    private String ruleName;
    /**
     * 回复类型 (text, cms)
     * {@link com.caring.sass.user.constant.KeyWordEnum}
     */
    @ApiModelProperty(value = "回复类型 (text, cms)")
    private String replyType;
    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    private String replyContent;

}
