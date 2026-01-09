package com.caring.sass.msgs.dto;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ChatAutomaticReplyPageDTO", description = "")
public class ChatAutomaticReplyPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回复的内容
     */
    @ApiModelProperty(value = "回复的内容")
    @Length(max = 200, message = "回复的内容长度不能超过200")
    private String content;
    /**
     * 1 开启， 0关闭
     */
    @ApiModelProperty(value = "1 开启， 0关闭")
    private Boolean status;
    /**
     * 消息超出多少时间未回复
     */
    @ApiModelProperty(value = "消息超出多少时间未回复")
    private Integer timeOut;

}
