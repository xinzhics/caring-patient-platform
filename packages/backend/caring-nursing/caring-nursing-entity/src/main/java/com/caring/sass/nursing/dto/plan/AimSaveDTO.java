package com.caring.sass.nursing.dto.plan;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AimSaveDTO", description = "护理目标")
public class AimSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 20, message = "名称长度不能超过20")
    private String name;
    /**
     * 图片URL
     */
    @ApiModelProperty(value = "图片URL")
    @Length(max = 255, message = "图片URL长度不能超过255")
    private String url;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    private String describe;
    /**
     * 是否包含（0：不包含  1：包含）
     */
    @ApiModelProperty(value = "是否包含（0：不包含  1：包含）")
    private Integer isContain;
    /**
     * 项目ID
     */
    @ApiModelProperty(value = "项目ID")
    private Long projectId;
    /**
     * 是否是默认目标 （0：不是  1：是）
     */
    @ApiModelProperty(value = "是否是默认目标 （0：不是  1：是）")
    private Integer isDefault;

}
