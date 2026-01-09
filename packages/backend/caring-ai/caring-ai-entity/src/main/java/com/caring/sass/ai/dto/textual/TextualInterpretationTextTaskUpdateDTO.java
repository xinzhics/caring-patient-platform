package com.caring.sass.ai.dto.textual;

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
import com.caring.sass.base.entity.SuperEntity;
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
 * 文献解读txt
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
@ApiModel(value = "TextualInterpretationTextTaskUpdateDTO", description = "文献解读txt")
public class TextualInterpretationTextTaskUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 上传文件名称
     */
    @ApiModelProperty(value = "上传文件名称")
    @Length(max = 500, message = "上传文件名称长度不能超过500")
    private String fileName;
    /**
     * 选择的标题
     */
    @ApiModelProperty(value = "选择的标题")
    @Length(max = 300, message = "选择的标题长度不能超过300")
    private String title;
    /**
     * 任务状态 0 创作中 1 创作完成 2 存为草稿
     */
    @ApiModelProperty(value = "任务状态 0 创作中 1 创作完成 2 存为草稿")
    private Integer taskStatus;
    /**
     * 生产的文稿
     */
    @ApiModelProperty(value = "生产的文稿")
    private String content;
    /**
     * 是否展示到ai医生数字分身平台
     */
    @ApiModelProperty(value = "是否展示到ai医生数字分身平台")
    private Integer showAiKnowledge;
}
