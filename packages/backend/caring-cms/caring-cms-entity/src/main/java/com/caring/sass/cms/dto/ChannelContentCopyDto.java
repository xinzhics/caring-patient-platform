package com.caring.sass.cms.dto;

import com.caring.sass.common.enums.CmsRoleRemark;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ChannelContentCopyDto
 * @Description
 * @Author yangShuai
 * @Date 2020/9/9 15:21
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "ChannelContentCopyDto", description = "内容复制类")
public class ChannelContentCopyDto {

    @ApiModelProperty(value = "内容库ID")
    private Long libraryId;

    @ApiModelProperty(value = "系统内容库栏目(分类)Id")
    private String libChannelId;

    @ApiModelProperty(value = "目标栏目(分类)Id", required = true)
    private String targetChannelId;

    @ApiModelProperty(value = "是否是医生学院，true是，false否")
    private Boolean doctorOwner;


    @ApiModelProperty(value = "选择要复制的内容Ids集合, allCopy为 false 时起效。")
    private List<String> libContentIds;

    @ApiModelProperty(value = "分组ID")
    private Long channelGroupId;

    private CmsRoleRemark cmsRoleRemark;
}
