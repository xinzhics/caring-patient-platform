package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.TreeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_sys_drugs_category")
@ApiModel(value = "SysDrugsCategory", description = "药品类别")
@AllArgsConstructor
public class SysDrugsCategory extends TreeEntity<SysDrugsCategory, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    @Length(max = 32, message = "父ID长度不能超过32")
    @TableField(value = "class_code", condition = EQUAL)
    @Excel(name = "父ID")
    private String classCode;

    @TableField(exist = false)
    private List<SysDrugsCategory> categories;

    @Builder
    public SysDrugsCategory(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                            String label, Long parentId, String classCode, Integer sortValue) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.parentId = parentId;
        this.classCode = classCode;
        this.label = label;
        this.sortValue = sortValue;
    }

}
