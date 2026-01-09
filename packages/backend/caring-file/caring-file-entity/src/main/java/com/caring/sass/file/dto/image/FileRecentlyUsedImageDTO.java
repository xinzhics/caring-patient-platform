package com.caring.sass.file.dto.image;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FileRecentlyUsedImageDTO", description = "CMS内容中图片地址")
public class FileRecentlyUsedImageDTO {

    @ApiModelProperty("当前登录人")
    private Long userId;

    @ApiModelProperty("当前登录身份")
    private String userType;

    @ApiModelProperty("租户")
    private String tenant;

    @ApiModelProperty("图片地址列表")
    private List<String> imageUrl;


}
