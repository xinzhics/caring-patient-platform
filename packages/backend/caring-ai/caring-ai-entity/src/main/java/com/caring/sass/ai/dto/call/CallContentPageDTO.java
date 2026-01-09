package com.caring.sass.ai.dto.call;

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

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 分身通话内容
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CallContentPageDTO", description = "分身通话内容")
public class CallContentPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通话记录id
     */
    @ApiModelProperty(value = "通话记录id")
    private Long callRecordId;
    /**
     * 用户 user，智能体：agent
     */
    @ApiModelProperty(value = "用户 user，智能体：agent")
    @Length(max = 100, message = "用户 user，智能体：agent长度不能超过100")
    private String userType;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @Length(max = 2000, message = "消息内容长度不能超过2000")
    private String callContent;

}
