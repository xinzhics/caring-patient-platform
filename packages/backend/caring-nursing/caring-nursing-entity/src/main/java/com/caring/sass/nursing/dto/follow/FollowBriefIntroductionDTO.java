package com.caring.sass.nursing.dto.follow;

import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "FollowBriefIntroductionDTO", description = "随访任务简介")
public class FollowBriefIntroductionDTO {


    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "背景图")
    @Length(max = 255, message = "长度不能超过255")
    private String url;

    @ApiModelProperty(value = "使用默认背景图 (1: 使用默认，0 使用上传的)")
    private int useDefault;

    /**
     * 是否显示概要（0：不显示  1：显示）
     */
    @ApiModelProperty(value = "是否显示概要（0：不显示  1：显示）")
    private int showOutline;


    @ApiModelProperty(value = "项目的创建时间/患者注册时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "历时天数")
    private long days;

    @ApiModelProperty(value = "随访项目数量")
    private int followProjectNumber;

    @ApiModelProperty(value = "管理人员数量")
    private int managePatientNumber;

    @ApiModelProperty(value = "随访任务的内容")
    private List<FollowTaskContent> followTaskContentList;

}
