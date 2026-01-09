package com.caring.sass.file.dto.image;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @className: CatcherImageDto
 * @author: 杨帅
 * @date: 2023/9/25
 */
@Data
@ApiModel("抓取远程图片")
public class CatcherImageDto {

    String tenantCode;

    String imageUrl;

    Long folderId;

}
