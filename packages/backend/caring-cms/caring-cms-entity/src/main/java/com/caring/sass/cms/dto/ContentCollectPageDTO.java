package com.caring.sass.cms.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ContentCollectPageDTO", description = "收藏记录")
public class ContentCollectPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "收藏人id")
    @NotNull(message = "收藏人id不能为空")
    @TableField("user_id")
    @Excel(name = "收藏人id")
    private Long userId;

    @ApiModelProperty(value = "角色")
    private String roleType;
}
