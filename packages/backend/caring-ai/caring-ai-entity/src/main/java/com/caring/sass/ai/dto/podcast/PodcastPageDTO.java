package com.caring.sass.ai.dto.podcast;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.article.TaskStatus;
import com.caring.sass.ai.entity.article.TaskType;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
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
 * 播客
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PodcastPageDTO", description = "播客")
public class PodcastPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 任务状态 生成对话，制作音频
     */
    @ApiModelProperty(value = "任务状态 生成对话，制作音频")
    private TaskStatus taskStatus;

    /**
     * 播客名称
     */
    @ApiModelProperty(value = "播客名称")
    @Length(max = 20, message = "播客名称长度不能超过20")
    private String podcastName;
    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @NotNull(message = "所属用户ID不能为空")
    private Long userId;

    @ApiModelProperty(value = "是否展示到docuKnow")
    private Boolean showAIKnowledge;

    @ApiModelProperty(value = "是否发布")
    private Boolean releaseStatus;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
