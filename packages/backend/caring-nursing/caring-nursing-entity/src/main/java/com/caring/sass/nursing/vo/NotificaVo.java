package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 通知人数
 *
 * @author Evan Zhai
 */
@Data
@ApiModel(value = "NotificaVo", description = "通知人数")
public class NotificaVo implements Serializable {

    private static final long serialVersionUID = 7402097294211529287L;

    @ApiModelProperty(value = "人数")
    private Integer num;
}
