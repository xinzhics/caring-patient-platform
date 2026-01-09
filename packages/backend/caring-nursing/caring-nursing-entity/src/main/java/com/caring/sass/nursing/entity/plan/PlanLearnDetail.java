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
 * 护理计划-学习
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
@TableName("t_nursing_plan_learn_detail")
@ApiModel(value = "PlanLearnDetail", description = "护理计划-学习")
@AllArgsConstructor
public class PlanLearnDetail extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 学习计划ID
     */
    @ApiModelProperty(value = "学习计划ID")
    @TableField("learn_plan_id")
    @Excel(name = "学习计划ID")
    private Long learnPlanId;

    @ApiModelProperty(value = "")
    @TableField("nursing_plan_detail_time_id")
    @Excel(name = "")
    private Long nursingPlanDetailTimeId;

    /**
     * 0 未推送 1已推送
     */
    @ApiModelProperty(value = "0 未推送 1已推送")
    @TableField("status_")
    @Excel(name = "0 未推送 1已推送", replace = {"是_true", "否_false", "_null"})
    private Boolean status;

    /**
     * 学习计划顺序
     */
    @ApiModelProperty(value = "学习计划顺序")
    @TableField("order_")
    @Excel(name = "学习计划顺序")
    private Integer order;


    @Builder
    public PlanLearnDetail(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long learnPlanId, Long nursingPlanDetailTimeId, Boolean status, Integer order) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.learnPlanId = learnPlanId;
        this.nursingPlanDetailTimeId = nursingPlanDetailTimeId;
        this.status = status;
        this.order = order;
    }

}
