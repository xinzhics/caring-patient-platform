package com.caring.sass.nursing.entity.plan;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_plan_tag")
@ApiModel(value = "PlanTag", description = "")
@AllArgsConstructor
public class PlanTag extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @ApiModelProperty(value = "标签ID")
    @TableField("tag_id")
    @Excel(name = "标签ID")
    private Long tagId;

    /**
     * 业务ID(学习计划是详情时间ID，护理计划是计划ID，护理目标是护理目标ID)
     */
    @ApiModelProperty(value = "业务ID(学习计划是详情时间ID，护理计划是计划ID，护理目标是护理目标ID)")
    @TableField("nursing_plan_id")
    @Excel(name = "业务ID(学习计划是详情时间ID，护理计划是计划ID，护理目标是护理目标ID)")
    private Long nursingPlanId;


    @Builder
    public PlanTag(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long tagId, Long nursingPlanId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.tagId = tagId;
        this.nursingPlanId = nursingPlanId;
    }

}
