package com.caring.sass.tenant.entity;

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
 * @ClassName LibrartUser
 * @Description
 * @Author yangShuai
 * @Date 2022/5/5 13:08
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_tenant_library_global_user")
@ApiModel(value = "LibraryGlobalUser", description = "内容库的权限账号")
@AllArgsConstructor
public class LibraryGlobalUser extends Entity<Long> {

    @ApiModelProperty(value = "全局账号ID")
    @NotNull(message = "全局账号ID不能为空")
    @TableField("global_user_id")
    @Excel(name = "全局账号ID")
    private Long globalUserId;

    @ApiModelProperty(value = "内容库ID")
    @NotNull(message = "内容库ID不能为空")
    @TableField("library_id")
    @Excel(name = "内容库ID")
    private Long libraryId;

    @ApiModelProperty(value = "内容库名称")
    @TableField(exist = false)
    private String libraryName;


}
