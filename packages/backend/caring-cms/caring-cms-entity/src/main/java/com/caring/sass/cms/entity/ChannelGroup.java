package com.caring.sass.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.enums.CmsRoleRemark;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;


@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_channel_group")
@ApiModel(value = "ChannelGroup", description = "栏目组")
@AllArgsConstructor
public class ChannelGroup extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "分组名称")
    @Length(max = 200, message = "分组名称长度不能超过200")
    @TableField(value = "group_name", condition = LIKE)
    private String groupName;

    @ApiModelProperty(value = "角色分类")
    @TableField(value = "cms_role_remark")
    private CmsRoleRemark cmsRoleRemark;

}
