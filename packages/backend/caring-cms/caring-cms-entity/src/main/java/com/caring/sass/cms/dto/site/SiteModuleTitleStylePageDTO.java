package com.caring.sass.cms.dto.site;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
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
 * 建站组件主题样式表
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
@ApiModel(value = "SiteModuleTitleStylePageDTO", description = "建站组件主题样式表")
public class SiteModuleTitleStylePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Long folderId;
    @ApiModelProperty(value = "")
    private Long pageId;
    @ApiModelProperty(value = "")
    private Long moduleId;
    /**
     * 标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)
     */
    @ApiModelProperty(value = "标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)")
    @Length(max = 50, message = "标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)长度不能超过50")
    private String titleType;
    /**
     * json格式的样式Style对象
     */
    @ApiModelProperty(value = "json格式的样式Style对象")
    @Length(max = 65535, message = "json格式的样式Style对象长度不能超过65,535")
    private String titleStyle;

}
