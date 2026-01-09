package com.caring.sass.file.dto.image;

import com.caring.sass.file.constant.ClassificationBelongEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 图片分组
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
@ApiModel(value = "FileClassificationSaveDTO", description = "图片分组")
public class FileClassificationSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String classificationName;
    /**
     * 分组的排序
     */
    @ApiModelProperty(value = "分组的排序")
    private Integer classificationSort;
    /**
     * 分组的归属(公众图片，我的图片)
     */
    @ApiModelProperty(value = "分组的归属(公众图片 PUBLIC_IMAGE，我的图片 MY_IMAGE)")
    @Length(max = 20, message = "分组的归属(公众图片，我的图片)长度不能超过20")
    private String classificationBelong;

}
