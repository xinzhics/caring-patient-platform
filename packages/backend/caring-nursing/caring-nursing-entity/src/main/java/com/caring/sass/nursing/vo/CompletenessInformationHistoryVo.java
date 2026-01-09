package com.caring.sass.nursing.vo;

import com.caring.sass.nursing.constant.SendType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 患者信息完整度管理历史VO对象
 *
 * @author Evan Zhai
 */
@Data
@ApiModel(value = "CompletenessInformationHistoryVo", description = "患者信息完整度管理历史VO对象")
public class CompletenessInformationHistoryVo implements Serializable {
    private static final long serialVersionUID = -9212395643625061169L;

    /**
     * 信息完整度展示字段
     */
    @ApiModelProperty(value = "信息完整度展示字段")
    private String desc;

    /**
     * 管理页面总数
     */
    @ApiModelProperty(value = "管理页面总数")
    private Integer num;

    /**
     * 时间
     */
    @ApiModelProperty(value = "时间")
    private LocalDateTime dateTime;

    /**
     * 发送方式
     */
    @ApiModelProperty(value = "发送方式，1->单发，2->群发")
    private SendType sendType;

    @ApiModelProperty(value = "患者详情")
    private List<CompletenessInformationVo>  information;
}
