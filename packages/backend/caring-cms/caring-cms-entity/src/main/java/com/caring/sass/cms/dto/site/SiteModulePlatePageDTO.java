package com.caring.sass.cms.dto.site;

import com.caring.sass.cms.constant.SitePlateType;
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
import java.util.List;

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
@ApiModel(value = "SiteModulePlatePageDTO", description = "建站组件的板块表")
public class SiteModulePlatePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "页面的ID列表")
    private List<Long> pageId;


    /**
     * 板块类型(图片，文章，视频)
     */
    @ApiModelProperty(value = "板块类型(图片，文章，视频)")
    @Length(max = 100, message = "板块类型(图片，文章，视频)长度不能超过100")
    private SitePlateType plateType;

    /**
     * 文章或视频的标题
     */
    @ApiModelProperty(value = "文章或视频的标题")
    @Length(max = 200, message = "文章或视频的标题长度不能超过200")
    private String plateTitle;

}
