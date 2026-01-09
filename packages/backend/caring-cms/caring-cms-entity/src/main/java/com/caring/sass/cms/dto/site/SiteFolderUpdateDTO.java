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
@ApiModel(value = "SiteFolderUpdateDTO", description = "建站文件夹表")
public class SiteFolderUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
    private Integer template;
    /**
     * 复制次数
     */
    @ApiModelProperty(value = "复制次数")
    private Integer copyNumber;
    /**
     * 复制品（0, 1）
     */
    @ApiModelProperty(value = "复制品（0, 1）")
    private Integer replica;
}
