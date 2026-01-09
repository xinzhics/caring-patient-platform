package com.caring.sass.cms.dto.site;

import com.caring.sass.cms.constant.SitePlateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 建站组件的板块表
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SiteModulePlateUpdateDTO", description = "建站组件的板块表")
public class SiteModulePlateUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty(value = "")
    private Long folderId;
    @ApiModelProperty(value = "")
    private Long pageId;
    @ApiModelProperty(value = "")
    private Long moduleId;
    /**
     * 组件标签ID
     */
    @ApiModelProperty(value = "组件标签ID")
    private Long moduleLabelId;
    /**
     * 板块类型(图片，文章，视频)
     */
    @ApiModelProperty(value = "板块类型(图片，文章，视频)")
    @Length(max = 100, message = "板块类型(图片，文章，视频)长度不能超过100")
    private SitePlateType plateType;
    /**
     * 板块顺序
     */
    @ApiModelProperty(value = "板块顺序")
    private Integer plateSortIndex;
    /**
     * 图片，文章视频封面地址
     */
    @ApiModelProperty(value = "图片，文章视频封面地址")
    @Length(max = 200, message = "图片，文章视频封面地址长度不能超过200")
    private String imageUrl;
    /**
     * 文章或视频的标题
     */
    @ApiModelProperty(value = "文章或视频的标题")
    @Length(max = 200, message = "文章或视频的标题长度不能超过200")
    private String plateTitle;
}
