package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaasDoctorQrCode {


    @ApiModelProperty(value = "医生二维码地址")
    String doctorQrUrl;

    @ApiModelProperty(value = "患者二维码")
    String qrCodeData;


}
