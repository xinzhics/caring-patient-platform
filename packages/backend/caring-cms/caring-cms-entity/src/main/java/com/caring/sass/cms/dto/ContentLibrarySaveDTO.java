package com.caring.sass.cms.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName ContentLibrartSaveDTO
 * @Description
 * @Author yangShuai
 * @Date 2022/5/5 13:13
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ContentLibrarySaveDTO", description = "内容库")
public class ContentLibrarySaveDTO implements Serializable {



    @ApiModelProperty(value = "内容库名称")
    @NotNull(message = "内容库名称不能为空")
    @Excel(name = "内容库名称")
    private String libraryName;



    @ApiModelProperty(value = "内容库描述")
    @Excel(name = "内容库描述")
    private String libraryDesc;


}
