package com.caring.sass.nursing.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 群发消息请求对象
 *
 * @author Evan Zhai
 */
@Data
@ApiModel(value = "IntervalQo", description = "群发消息请求对象")
public class IntervalQo implements Serializable {

    private static final long serialVersionUID = -2489777926662992356L;

    /**
     * min = 1
     * 低于60：1
     */
    @ApiModelProperty(value = "最小值")
    @Null
    private Integer min;

    /**
     * 如果是全部 max = 99
     * max = 59
     */
    @ApiModelProperty(value = "最大值")
    @NotNull
    private Integer max;
}
