package com.caring.sass.nursing.dto.drugs;

import java.time.LocalDateTime;
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
 * 药品类别
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SysDrugsCategoryPageDTO", description = "药品类别")
public class SysDrugsCategoryPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类别名称
     */
    @ApiModelProperty(value = "类别名称")
    @Length(max = 255, message = "类别名称长度不能超过255")
    private String label;
    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    private Long parentId;
    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    @Length(max = 32, message = "父ID长度不能超过32")
    private String classCode;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;

}
