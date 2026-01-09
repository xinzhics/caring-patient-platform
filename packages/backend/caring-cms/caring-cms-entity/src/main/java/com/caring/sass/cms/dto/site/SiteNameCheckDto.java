package com.caring.sass.cms.dto.site;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @className: SiteNameCheckDto
 * @author: 杨帅
 * @date: 2023/9/25
 */
@Data
@ApiModel("校验名称可用时使用")
public class SiteNameCheckDto {

    @ApiModelProperty(value = "主键, 修改名称时必传")
    private Long id;

    /**
     * 文件夹名称
     */
    @ApiModelProperty(value = "文件夹名称")
    private String folderName;
    /**
     * 是否为模板(0 不是， 1 是)
     */
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    private Integer template;
}
