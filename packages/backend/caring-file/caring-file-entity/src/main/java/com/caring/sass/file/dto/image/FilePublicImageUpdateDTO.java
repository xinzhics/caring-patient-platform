package com.caring.sass.file.dto.image;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 公共图片
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
@ApiModel(value = "FilePublicImageUpdateDTO", description = "公共图片")
public class FilePublicImageUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @ApiModelProperty(value = "公共图片的业务id")
    @NotNull(message = "公共图片的业务id不能为空")
    private Long businessId;
    /**
     * 图片分组ID
     */
    @ApiModelProperty(value = "图片分组ID")
    private Long fileClassificationId;

    @ApiModelProperty(value = "图片名称")
    private String fileName;
}
