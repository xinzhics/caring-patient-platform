package com.caring.sass.nursing.entity.tag;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 业务关联标签记录表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_tag_association")
@ApiModel(value = "Association", description = "业务关联标签记录表")
@AllArgsConstructor
public class Association extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签Id
     */
    @ApiModelProperty(value = "标签Id")
    @TableField("tag_id")
    @Excel(name = "标签Id")
    private Long tagId;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id(患者ID)")
    @TableField(value = "association_id", condition = EQUAL)
    @Excel(name = "业务Id(患者ID)")
    private String associationId;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @Length(max = 3, message = "排序长度不能超过3")
    @TableField(value = "order_", condition = EQUAL)
    @Excel(name = "排序")
    private String order;


}
