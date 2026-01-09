package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ContentLibrary
 * @Description
 * @Author yangShuai
 * @Date 2022/5/5 9:58
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_content_library")
@ApiModel(value = "ContentLibrary", description = "内容库")
@AllArgsConstructor
public class ContentLibrary extends Entity<Long> {


    @ApiModelProperty(value = "内容库名称")
    @NotNull(message = "内容库名称不能为空")
    @TableField("library_name")
    @Excel(name = "内容库名称")
    private String libraryName;



    @ApiModelProperty(value = "内容库描述")
    @TableField("library_desc")
    @Excel(name = "内容库描述")
    private String libraryDesc;






}
