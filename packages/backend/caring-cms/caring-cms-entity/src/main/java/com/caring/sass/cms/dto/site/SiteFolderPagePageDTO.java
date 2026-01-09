package com.caring.sass.cms.dto.site;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.SitePageTemplateType;
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
 * 建站文件夹中的页面表
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
@ApiModel(value = "SiteFolderPagePageDTO", description = "建站文件夹中的页面表")
public class SiteFolderPagePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹名称
     */
    @ApiModelProperty(value = "文件夹名称")
    @Length(max = 50, message = "文件夹名称长度不能超过50")
    private String pageName;
    /**
     * 是否为模板(0 不是， 1 是)
     */
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    private Integer template;
    /**
     * 模板类型(page 单页模板 ， folder 文件夹模板)
     */
    @ApiModelProperty(value = "模板类型(PAGE 单页模板 ， FOLDER 文件夹模板)")
    @Length(max = 50, message = "模板类型(page 单页模板 ， folder 文件夹模板)长度不能超过50")
    private SitePageTemplateType templateType;

    @ApiModelProperty(value = "文件夹的ID")
    @TableField("folder_id")
    @Excel(name = "文件夹的ID")
    private Long folderId;

    @ApiModelProperty(value = "租户")
    private String tenantCode;
}
