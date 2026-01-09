package com.caring.sass.cms.dto.site;

import com.baomidou.mybatisplus.annotation.TableField;
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

/**
 * <p>
 * 实体类
 * 建站文件夹表
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
@ApiModel(value = "SiteFolderPageDTO", description = "建站文件夹表")
public class SiteFolderPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹名称
     */
    @ApiModelProperty(value = "文件夹名称")
    @Length(max = 50, message = "文件夹名称长度不能超过50")
    private String folderName;
    /**
     * 是否为模板(0 不是， 1 是)
     */
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    private Integer template = 0;


    @ApiModelProperty(value = "删除状态（0, 1）")
    private Integer deleteStatus;

}
