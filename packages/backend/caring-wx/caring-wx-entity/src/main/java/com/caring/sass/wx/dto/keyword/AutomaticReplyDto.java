package com.caring.sass.wx.dto.keyword;

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
 * 自动回复 信息设置
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KeywordSaveDTO", description = "微信服务号自动回复关键词")
public class AutomaticReplyDto implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "主键", required = false, allowEmptyValue = true)
    private Long id;

    /**
     * 消息类型（0：文本  1：图文  ）
     */
    @ApiModelProperty(value = "消息类型（0：文本  1：图片  ）")
    @Length(max = 10, message = "消息类型（0：文本  1：图片  ）长度不能超过10")
    private Integer msgType;

    /**
     * 回复
     */
    @ApiModelProperty(value = "回复")
    @Length(max = 200, message = "回复长度不能超过200")
    private String reply;

}
