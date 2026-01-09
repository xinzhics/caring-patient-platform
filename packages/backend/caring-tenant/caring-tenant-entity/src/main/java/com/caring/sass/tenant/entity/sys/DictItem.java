package com.caring.sass.tenant.entity.sys;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 字典项
 * </p>
 *
 * @author leizhi
 * @since 2021-03-25
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_sys_dict_item")
@ApiModel(value = "DictItem", description = "字典项")
@AllArgsConstructor
public class DictItem extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    @ApiModelProperty(value = "类型ID")
    @NotNull(message = "类型ID不能为空")
    @TableField("dictionary_id")
    @Excel(name = "类型ID")
    private Long dictionaryId;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @NotEmpty(message = "类型不能为空")
    @Length(max = 64, message = "类型长度不能超过64")
    @TableField(value = "dictionary_type", condition = LIKE)
    @Excel(name = "类型")
    private String dictionaryType;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @NotEmpty(message = "编码不能为空")
    @Length(max = 64, message = "编码长度不能超过64")
    @TableField(value = "code", condition = LIKE)
    @Excel(name = "编码")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @TableField("status_")
    @Excel(name = "状态", replace = {"是_true", "否_false", "_null"})
    private Boolean status;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "描述")
    private String describe;

    /**
     * 属性值1
     */
    @ApiModelProperty(value = "属性值1")
    @Length(max = 255, message = "属性值1长度不能超过255")
    @TableField(value = "attr1", condition = EQUAL)
    @Excel(name = "属性值1")
    private String attr1;


    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("sort_value")
    @Excel(name = "排序")
    private Integer sortValue;


    @Builder
    public DictItem(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    Long dictionaryId, String dictionaryType, String code, String name, Boolean status, 
                    String describe, Integer sortValue) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.dictionaryId = dictionaryId;
        this.dictionaryType = dictionaryType;
        this.code = code;
        this.name = name;
        this.status = status;
        this.describe = describe;
        this.sortValue = sortValue;
    }

}
