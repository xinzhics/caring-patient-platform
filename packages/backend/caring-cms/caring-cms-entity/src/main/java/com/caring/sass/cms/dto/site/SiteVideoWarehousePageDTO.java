package com.caring.sass.cms.dto.site;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
@ApiModel(value = "SiteVideoWarehousePageDTO", description = "建站视频库")
public class SiteVideoWarehousePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "视频名称")
    @Length(max = 50, message = "长度不能超过50")
    private String videoTitle;

    @ApiModelProperty(value = "这些视频不需要返回")
    private List<Long> filterVideoIds;

}
