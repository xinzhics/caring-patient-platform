package com.caring.sass.file.dto.image;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 我的图片
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
@ApiModel(value = "FileMyPageDTO", description = "我的图片")
public class FileMyPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 图片分组ID
     */
    @ApiModelProperty(value = "图片分组ID")
    private Long fileClassificationId;

    @ApiModelProperty(value = "图片名称")
    private String fileName;

    @ApiModelProperty(value = "租户。超管端为admin， 项目端为各种的租户")
    private String tenantCode;


}
