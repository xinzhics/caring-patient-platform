package com.caring.sass.ai.dto.podcast;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.podcast.PodcastInputType;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
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
@ApiModel(value = "PodcastUpdateDTO", description = "播客")
public class PodcastUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 播客名称
     */
    @ApiModelProperty(value = "播客音频名称")
//    @Length(max = 20, message = "音频名称长度不能超过20")
    private String podcastName;
    /**
     * 播客素材类型 文章链接，文档，文字输入
     */
    @ApiModelProperty(value = "播客素材类型 文章链接，文档，文字输入")
    @NotNull
    private PodcastInputType podcastInputType;
    /**
     * 文章链接或文档url
     */
    @ApiModelProperty(value = "文章链接或文档url")
    @Length(max = 10000, message = "文章链接或文档url长度不能超过10000")
    private String podcastInputContent;
    /**
     * 文字内容
     */
    @ApiModelProperty(value = "inputype是文字输入时，输入框的文字内容")
    private String podcastInputTextContent;
    /**
     * 文档的文件名称
     */
    @ApiModelProperty(value = "文档的文件名称")
    @Length(max = 200, message = "文档的文件名称长度不能超过200")
    private String podcastInputFileName;
    /**
     * 文档的文件大小
     */
    @ApiModelProperty(value = "文档的文件大小（kb）")
    private Integer podcastInputFileSize;

    @ApiModelProperty(value = "播客类型 科普创作： article, 文献解读： textual ")
    private String podcastType;


}
