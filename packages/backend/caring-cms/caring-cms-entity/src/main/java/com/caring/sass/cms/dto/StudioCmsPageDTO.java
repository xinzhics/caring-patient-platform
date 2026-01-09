package com.caring.sass.cms.dto;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.StudioCmsType;
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
 * 医生cms详情表
 * </p>
 *
 * @author 杨帅
 * @since 2025-11-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "StudioCmsPageDTO", description = "医生cms详情表")
public class StudioCmsPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @Length(max = 150, message = "标题长度不能超过150")
    private String cmsTitle;
    /**
     * cms类型（文章，声音，视频）
     */
    @ApiModelProperty(value = "cms类型（文章，声音，视频）")
    private StudioCmsType cmsType;


    /**
     * 文件类型(mp4,avi,mp3...)
     */
    @ApiModelProperty(value = "文件类型(mp4,avi,mp3...)")
    @Length(max = 40, message = "文件类型(mp4,avi,mp3...)长度不能超过40")
    private String cmsFileType;

    /**
     * 发布状态 0: 草稿， 1: 已发布
     */
    @ApiModelProperty(value = "发布状态 0: 草稿， 1: 已发布")
    private Integer releaseStatus;

    /**
     * 数据所属医生
     */
    @ApiModelProperty(value = "数据所属医生")
    @NotNull
    private Long doctorId;
    /**
     * 置顶状态0 不置顶， 1 置顶
     */
    @ApiModelProperty(value = "置顶状态0 不置顶， 1 置顶")
    private Integer pinToTop;

    /**
     * 科普创作数据id
     */
    @ApiModelProperty(value = "科普创作数据id")
    private Long articleDataId;

}
