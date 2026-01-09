package com.caring.sass.tenant.entity.sys;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 系统字典类型
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
@TableName("t_sys_dict")
@ApiModel(value = "Dict", description = "系统字典类型")
@AllArgsConstructor
public class Dict extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 编码	一颗树仅仅有一个统一的编码
     */
    @ApiModelProperty(value = "编码	一颗树仅仅有一个统一的编码")
    @NotEmpty(message = "编码	一颗树仅仅有一个统一的编码不能为空")
    @Length(max = 64, message = "编码	一颗树仅仅有一个统一的编码长度不能超过64")
    @TableField(value = "type_", condition = LIKE)
    @Excel(name = "编码	一颗树仅仅有一个统一的编码")
    private String type;

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
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 200, message = "描述长度不能超过200")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "描述")
    private String describe;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @TableField("status_")
    @Excel(name = "状态", replace = {"是_true", "否_false", "_null"})
    private Boolean status;


    @Builder
    public Dict(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String type, String name, String describe, Boolean status) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.type = type;
        this.name = name;
        this.describe = describe;
        this.status = status;
    }

}
