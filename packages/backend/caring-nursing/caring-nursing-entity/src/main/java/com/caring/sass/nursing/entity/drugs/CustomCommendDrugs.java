package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 自定义推荐药品
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
@TableName("t_custom_commend_drugs")
@ApiModel(value = "CustomCommendDrugs", description = "自定义推荐药品")
@AllArgsConstructor
public class CustomCommendDrugs extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    @TableField(value = "drugs_id")
    @Excel(name = "")
    private Long drugsId;

    @ApiModelProperty(value = "")
    @TableField("number")
    @Excel(name = "")
    private Integer number;

    @ApiModelProperty(value = "")
    @TableField("dosage")
    @Excel(name = "")
    private Float dosage;

    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "time", condition = LIKE)
    @Excel(name = "")
    private String time;

    @ApiModelProperty(value = "")
    @TableField("cycle")
    @Excel(name = "")
    private Integer cycle;

    @ApiModelProperty(value = "")
    @TableField("order_")
    @Excel(name = "")
    private Integer order;

    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 255, message = "药品名称长度不能超过255")
    @TableField(value = "drugs_name", condition = LIKE)
    @Excel(name = "药品名称")
    private String drugsName;


    @Builder
    public CustomCommendDrugs(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                              Long drugsId, Integer number, Float dosage, String time, Integer cycle,
                              Integer order, String drugsName) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.drugsId = drugsId;
        this.number = number;
        this.dosage = dosage;
        this.time = time;
        this.cycle = cycle;
        this.order = order;
        this.drugsName = drugsName;
    }

}
