package com.caring.sass.ai.entity.call;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 通话充值配置
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-02
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_call_config")
@ApiModel(value = "CallConfig", description = "通话充值配置")
@AllArgsConstructor
public class CallConfig extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 分钟数量 100  500，other
     */
    @ApiModelProperty(value = "分钟数量 100  500，other")
    @Length(max = 100, message = "分钟数量 100  500，other长度不能超过100")
    @TableField(value = "minute_quantity", condition = LIKE)
    @Excel(name = "分钟数量 100  500，other")
    private String minuteQuantity;


    @ApiModelProperty(value = "自定义分钟数")
    @TableField(exist = false)
    private Integer minute;

    /**
     * 金额(分)
     */
    @ApiModelProperty(value = "金额(分)")
    @TableField("cost_")
    @Excel(name = "金额(分)")
    private Long cost;


    @Builder
    public CallConfig(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String minuteQuantity, Long cost) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.minuteQuantity = minuteQuantity;
        this.cost = cost;
    }

}
