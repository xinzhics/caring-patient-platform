package com.caring.sass.nursing.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * @author Evan Zhai
 */
@Data
public class NotificationQo implements Serializable {

    private static final long serialVersionUID = -7702516967820194687L;

    @ApiModelProperty(value = "患者ID")
    @Null
    private Long patientId;

    @ApiModelProperty(value = "医助ID")
    private Long nursingId;

}
