package com.caring.sass.ai.dto.podcast;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.podcast.PodcastStyle;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 播客声音设置
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
@ApiModel(value = "PodcastSoundSetSaveDTO", description = "播客声音设置")
public class PodcastSoundSetSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 播客ID
     */
    @ApiModelProperty(value = "播客ID")
    @NotNull(message = "播客ID不能为空")
    private Long podcastId;

    @ApiModelProperty(value = "播客名称")
    private String podcastName;
    /**
     * 播客风格
     */
    @ApiModelProperty(value = "播客风格")
    @NotNull
    private PodcastStyle podcastStyle;


    @ApiModelProperty(value = "声音设置的列表")
    private List<PodcastSoundSetUpdateDTO> soundList;

}
