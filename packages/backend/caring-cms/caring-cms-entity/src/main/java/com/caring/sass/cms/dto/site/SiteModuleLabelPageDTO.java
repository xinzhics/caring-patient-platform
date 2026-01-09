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
 * 建站多功能导航标签
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
@ApiModel(value = "SiteModuleLabelPageDTO", description = "建站多功能导航标签")
public class SiteModuleLabelPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID")
    private Long folderId;
    /**
     * 页面ID
     */
    @ApiModelProperty(value = "页面ID")
    private Long pageId;
    /**
     * 组件ID
     */
    @ApiModelProperty(value = "组件ID")
    private Long moduleId;
    /**
     * 标签的名称
     */
    @ApiModelProperty(value = "标签的名称")
    @Length(max = 100, message = "标签的名称长度不能超过100")
    private String labelName;
    /**
     * 标签排序下标(从小到大)
     */
    @ApiModelProperty(value = "标签排序下标(从小到大)")
    private Integer labelSortIndex;

}
