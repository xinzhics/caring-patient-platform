package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MiniQrCode {

    public String path;

    public Integer width;

}
