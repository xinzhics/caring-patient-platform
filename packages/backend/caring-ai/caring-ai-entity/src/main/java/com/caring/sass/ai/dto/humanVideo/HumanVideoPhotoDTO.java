package com.caring.sass.ai.dto.humanVideo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("数字人视频和照片")
public class HumanVideoPhotoDTO {

    private String name;

    private LocalDateTime createTime;

    @ApiModelProperty("数字人照片")
    private String humanPhotoFileUrl;

    @ApiModelProperty("数字人视频封面")
    private String humanVideoCover;


    @ApiModelProperty("数字人视频")
    private String humanVideoFileUrl;

    @ApiModelProperty("created: 制作中， success: 已完成, error: 失败")
    private String status;

}
