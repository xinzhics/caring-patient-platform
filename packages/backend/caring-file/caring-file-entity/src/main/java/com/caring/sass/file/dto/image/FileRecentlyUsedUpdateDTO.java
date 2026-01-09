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
@ApiModel(value = "FileRecentlyUsedUpdateDTO", description = "最近使用图片")
public class FileRecentlyUsedUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "最近使用的业务ID")
    @NotNull(message = "最近使用的业务ID不能为空")
    private Long businessId;

    @ApiModelProperty(value = "文件名称")
    public String fileName;

    public String tenantCode;
}
