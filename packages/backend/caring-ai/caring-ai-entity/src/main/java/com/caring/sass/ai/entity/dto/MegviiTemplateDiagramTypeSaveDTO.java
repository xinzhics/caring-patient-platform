package com.caring.sass.ai.entity.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 模版图分类
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MegviiTemplateDiagramTypeSaveDTO", description = "模版图分类")
public class MegviiTemplateDiagramTypeSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    private Integer order;
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    @Length(max = 50, message = "分类名称长度不能超过50")
    private String name;

    @ApiModelProperty(value = "Male 男, Female 女")
    private String gender;

}
