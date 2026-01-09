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
@ApiModel(value = "FileMyUpdateDTO", description = "我的图片")
public class FileMyUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 文件id
     */
    @ApiModelProperty(value = "我的图片关联表id")
    @NotNull(message = "我的图片关联表id不能为空")
    private Long businessId;
    /**
     * 图片分组ID
     */
    @ApiModelProperty(value = "图片分组ID")
    private Long fileClassificationId;

    @ApiModelProperty(value = "图片名称")
    private String fileName;


    @ApiModelProperty(value = "租户, 不传咋从请求头中获取")
    private String tenantCode;

}
