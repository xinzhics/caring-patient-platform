package com.caring.sass.cms.dto.site;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 建站视频库
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SiteVideoWarehouseUpdateDTO", description = "建站视频库")
public class SiteVideoWarehouseUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty(value = "视频地址")
    @Length(max = 300, message = "长度不能超过300")
    private String videoUrl;


    @ApiModelProperty(value = "视频名称")
    @Length(max = 50, message = "长度不能超过50")
    private String videoTitle;
    /**
     * 视频时长
     */
    @ApiModelProperty(value = "视频时长")
    private Float videoDuration;
    /**
     * 视频大小(kb)
     */
    @ApiModelProperty(value = "视频大小(kb)")
    private Float videoFileSize;
    /**
     * 视频封面
     */
    @ApiModelProperty(value = "视频封面")
    @Length(max = 200, message = "视频封面长度不能超过200")
    private String videoCover;

    @ApiModelProperty(value = "obs视频对象名称")
    private String obsFileName;

    @ApiModelProperty(value = "视频描述")
    private String videoDesc;

    @ApiModelProperty(value = "是否同步信息")
    private Boolean synchronizeInformation;
}
