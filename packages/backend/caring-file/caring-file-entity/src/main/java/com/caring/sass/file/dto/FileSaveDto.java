package com.caring.sass.file.dto;

import com.caring.sass.file.enumeration.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName FileSaveDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/15 11:52
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "FileSaveDto", description = "上传文件后回调保存到系统")
public class FileSaveDto {

    @ApiModelProperty(value = "父文件夹ID")
    private Long folderId;

    @ApiModelProperty(value = "数据类型")
    private DataType dataType;

    @ApiModelProperty(value = "文件访问链接")
    private String url;

    @ApiModelProperty(value = "原始文件名")
    @Length(max = 255, message = "原始文件名长度不能超过255")
    private String submittedFileName;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    private String tenantCode;

    @ApiModelProperty(value = "文件类型")
    private String contextType;

    @ApiModelProperty(value = "文件名后缀")
    private String ext;

}
