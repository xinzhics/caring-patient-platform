package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.enums.CmsRoleRemark;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @className: ChannelContentGroupListDTO
 * @author: 杨帅
 * @date: 2023/9/11
 */
@Data
@Builder
@ApiModel("建站查询文章的分类")
public class ChannelContentGroupListDTO {

    @ApiModelProperty("患教库是0，医教库是1，其他是group的ID")
    private Long id;

    @ApiModelProperty("分类的名称")
    private String name;

    @ApiModelProperty(value = "角色分类")
    private CmsRoleRemark cmsRoleRemark;


}
