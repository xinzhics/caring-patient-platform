package com.caring.sass.nursing.dto.unfinished;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("未完成提醒记录列表")
@Data
public class UnfinishedListResult {


    @ApiModelProperty("提醒记录")
    private Long id;

    @ApiModelProperty("患者ID")
    private Long patientId;

    @ApiModelProperty("患者头像")
    private String patientAvatar;

    @ApiModelProperty("患者名称")
    private String patientName;

    @ApiModelProperty("患者IM账号")
    private String patientImAccount;

    @ApiModelProperty("患者关注状态")
    private Integer patientFollowStatus;

    @ApiModelProperty("查看状态0 未查看 1 已查看")
    private Integer seeStatus;

    private Long doctorId;

    @ApiModelProperty("医生名称")
    private String doctorName;


    @ApiModelProperty("任务推送时间")
    private LocalDateTime remindTime;

    @ApiModelProperty("处理时间")
    private LocalDateTime handleTime;


}
