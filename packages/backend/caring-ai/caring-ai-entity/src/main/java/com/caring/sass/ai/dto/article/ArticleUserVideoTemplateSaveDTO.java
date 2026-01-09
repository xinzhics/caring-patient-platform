package com.caring.sass.ai.dto.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 视频底板
 * </p>
 *
 * @author leizhi
 * @since 2025-02-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserVideoTemplateSaveDTO", description = "视频底板")
public class ArticleUserVideoTemplateSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 图片链接
     */
    @ApiModelProperty(value = "照片图片链接")
    private String avatarUrl;


    @ApiModelProperty(value = "类型1 图片, 2视频")
    @NotNull
    private Integer type;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;
    /**
     * 底板视频素材文件 ID
     */
    @ApiModelProperty(value = "底板视频素材文件 ID")
    @Length(max = 255, message = "底板视频素材文件 ID长度不能超过255")
    private String templateId;

    /**
     * 底板 ID
     */
    @ApiModelProperty(value = "底板 ID")
    @Length(max = 255, message = "底板 ID长度不能超过255")
    private String templateVideoId;

    /**
     * 视频链接
     */
    @ApiModelProperty(value = "视频链接")
    @Length(max = 255, message = "视频链接长度不能超过255")
    private String videoUrl;

    /**
     * 视频链接
     */
    @ApiModelProperty(value = "授权视频")
    private String authVideoUrl;
    /**
     * 底板名称
     */
    @ApiModelProperty(value = "底板名称")
    @Length(max = 255, message = "底板名称长度不能超过255")
    private String videoName;
    /**
     * 文件原始名称
     */
    @ApiModelProperty(value = "文件原始名称")
    @Length(max = 255, message = "文件原始名称长度不能超过255")
    private String fileName;

    @NotNull(message = "视频宽不能为空")
    private Integer videoWidth;

    @NotNull(message = "视频高不能为空")
    private Integer videoHeight;

    @ApiModelProperty(value = "是否克隆音频")
    private Boolean cloneVoice;

}
