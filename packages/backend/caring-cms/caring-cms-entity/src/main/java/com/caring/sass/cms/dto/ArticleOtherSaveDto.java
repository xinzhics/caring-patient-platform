package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleOtherSaveDto", description = "保存其他素材")
public class ArticleOtherSaveDto {

    @ApiModelProperty(value = "素材saas的url")
    @NotNull
    private String mediaSaasUrl;

    @ApiModelProperty(value = "素材标题")
    @Length(max = 30, message = "素材标题不能超过200")
    private String title;

    @ApiModelProperty(value = "素材类型(image,voice,video,thumb)")
    private String materialType;

    @ApiModelProperty(value = "视频描述(视频素材必须传)")
    @TableField(value = "introduction")
    private String introduction;

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

}
