package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ContentCollect
 * @Description
 * @Author yangShuai
 * @Date 2021/3/11 13:36
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_content_collect")
@ApiModel(value = "ContentCollect", description = "收藏")
@AllArgsConstructor
public class ContentCollect extends SuperEntity<Long> {


    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "收藏人id")
    @NotNull(message = "收藏人id不能为空")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "角色")
    @NotNull(message = "角色不能为空")
    @TableField("role_type")
    @Excel(name = "角色")
    private String roleType;

    @ApiModelProperty(value = "内容id")
    @NotNull(message = "内容id不能为空")
    @TableField("content_id")
    @Excel(name = "内容id")
    private Long contentId;

    @ApiModelProperty(value = "收藏状态 1收藏 0 取消")
    @TableField("collect_status")
    private Integer collectStatus;

    @ApiModelProperty(value = "内容")
    @TableField(exist = false)
    private ChannelContent channelContent;

}
