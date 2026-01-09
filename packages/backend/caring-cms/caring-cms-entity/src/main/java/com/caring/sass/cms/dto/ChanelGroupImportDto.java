package com.caring.sass.cms.dto;

import com.caring.sass.common.enums.CmsRoleRemark;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ChanelGroup
 * @Description
 * @Author yangShuai
 * @Date 2021/10/21 9:44
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ChanelGroupImportDto", description = "页面导入功能")
public class ChanelGroupImportDto {

    @NotNull(message = "栏目ID不能为空")
    private Long chanelId;

    private CmsRoleRemark cmsRoleRemark;

}
