package com.caring.sass.nursing.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 发送通知消息的请求对象
 *
 * @author Evan Zhai
 */
@Data
@ApiModel(value = "ReminderQo", description = "发送通知消息的请求对象")
public class ReminderQo implements Serializable {

    private static final long serialVersionUID = 2852952012367499021L;

    @ApiModelProperty(value = "区间ID")
    private List<Long> IntervalId;

    @NotNull
    @ApiModelProperty(value = "专员ID")
    private Long nursingId;

}
