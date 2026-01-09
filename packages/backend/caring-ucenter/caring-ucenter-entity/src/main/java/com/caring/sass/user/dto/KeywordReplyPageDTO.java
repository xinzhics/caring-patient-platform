package com.caring.sass.user.dto;

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
@ApiModel(value = "KeywordReplyPageDTO", description = "关键字回复内容")
public class KeywordReplyPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名/关键字名字")
    private String ruleName;
    /**
     * 回复类型 (text, cms)
     */
    @ApiModelProperty(value = "回复类型 (text, cms)")
    private String replyType;


}
