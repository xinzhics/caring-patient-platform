package com.caring.sass.user.dto;

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
@ApiModel(value = "KeywordReplySaveDTO", description = "关键字回复内容")
public class KeywordReplySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名")
    @Length(max = 60, message = "规则名长度不能超过60")
    private String ruleName;
    /**
     * 回复类型 (text, cms)
     */
    @ApiModelProperty(value = "回复类型 (text, cms)")
    @Length(max = 255, message = "回复类型 (text, cms)长度不能超过255")
    private String replyType;
    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    @Length(max = 600, message = "回复内容长度不能超过600")
    private String replyContent;


    @ApiModelProperty(value = "关键字")
    @NotNull
    private List<KeywordSettingSaveDTO> keywordSettingDTOList;


}
