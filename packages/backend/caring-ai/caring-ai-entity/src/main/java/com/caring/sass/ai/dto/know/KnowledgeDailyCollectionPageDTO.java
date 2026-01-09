package com.caring.sass.ai.dto.know;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
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
@ApiModel(value = "KnowledgeDailyCollectionPageDTO", description = "日常收集文本内容")
public class KnowledgeDailyCollectionPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 语音文本标题
     */
    @ApiModelProperty(value = "语音文本标题")
    @Length(max = 200, message = "语音文本标题长度不能超过200")
    private String textTitle;


    @ApiModelProperty(value = "数据类型(1 正常日常收集， 2 病历录音内容)")
    @TableField("data_type")
    private Integer dataType;

    @ApiModelProperty(value = "博主用户id")
    private Long userId;

}
