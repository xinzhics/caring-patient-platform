package com.caring.sass.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: TenantLibraryDTO
 * @author: 杨帅
 * @date: 2023/7/19
 */
@ApiModel
@Data
public class TenantLibraryDTO {

    /**
     * 要替换的内容库ID集合。为空时 清空所有内容库关联
     */
    @ApiModelProperty("要替换的内容库ID集合。为空时 清空所有内容库关联")
    List<Long> libraryIds;

    @ApiModelProperty("租户的CODE")
    String tenantCode;

}
