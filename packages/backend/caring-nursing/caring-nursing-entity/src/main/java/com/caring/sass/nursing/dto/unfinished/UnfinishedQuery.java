package com.caring.sass.nursing.dto.unfinished;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("查询未完成跟踪记录")
public class UnfinishedQuery {

    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "设置ID")
    @NotNull(message = "参数不能为空")
    private Long unFinishedSettingId;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者名称")
    private String patientName;
    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @NotNull(message = "医助ID不能为空")
    private Long nursingId;


    @ApiModelProperty(value = "医生ID")
    private List<Long> doctorIds;


    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;


    @ApiModelProperty(value = "查看状态 0 未查看， 1已查看")
    private Integer seeStatus;

    @ApiModelProperty(value = "关注状态 2 取关， 1 关注")
    private Integer patientFollowStatus;

    /**
     * 处理状态（0 未处理， 1 已处理）
     */
    @ApiModelProperty(value = "处理状态（0 未处理， 1 已处理）")
    @NotNull(message = "处理状态（0 未处理， 1 已处理）不能为空")
    private Integer handleStatus;


    @ApiModelProperty(value = "desc 时间倒序， asc 时间升序， 默认 desc")
    private String sort;


}
