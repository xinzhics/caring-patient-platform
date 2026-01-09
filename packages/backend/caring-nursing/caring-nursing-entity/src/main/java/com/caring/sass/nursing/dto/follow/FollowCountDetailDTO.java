package com.caring.sass.nursing.dto.follow;

import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "FollowCountDetailDTO", description = "随访任务统计的简介")
public class FollowCountDetailDTO {

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "列表栏目")
    private List<FollowTaskContent> followTaskContentList;


}
