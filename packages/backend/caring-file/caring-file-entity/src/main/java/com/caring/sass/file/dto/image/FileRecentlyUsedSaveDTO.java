package com.caring.sass.file.dto.image;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 实体类
 * 最近使用图片
 * </p>
 *
 * @author 杨帅
 * @since 2022-08-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FileRecentlyUsedSaveDTO", description = "最近使用图片")
public class FileRecentlyUsedSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("(my)我的图片、(publicImage)公共图片， (recentlyUsed) 最近使用")
    @NotNull
    private String fileType;

    @ApiModelProperty(value = "业务ID")
    @NotNull(message = "业务ID不能为空")
    private List<Long> businessIds;

    @ApiModelProperty("租户")
    private String tenantCode;
}
