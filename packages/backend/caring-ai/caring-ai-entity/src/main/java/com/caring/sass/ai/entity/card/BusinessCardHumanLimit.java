package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 用户数字人额度
 * </p>
 *
 * @author 杨帅
 * @since 2025-03-14
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_human_limit")
@ApiModel(value = "BusinessCardHumanLimit", description = "用户数字人额度")
@AllArgsConstructor
public class BusinessCardHumanLimit extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 剩余次数
     */
    @ApiModelProperty(value = "剩余次数")
    @NotNull(message = "剩余次数不能为空")
    @TableField("remaining_times")
    @Excel(name = "剩余次数")
    private Integer remainingTimes;

    /**
     * 可用额度到期时间
     */
    @ApiModelProperty(value = "可用额度到期时间")
    @TableField("expiration_date")
    @Excel(name = "可用额度到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationDate;


    @Builder
    public BusinessCardHumanLimit(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, Integer remainingTimes, LocalDateTime expirationDate) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.remainingTimes = remainingTimes;
        this.expirationDate = expirationDate;
    }

}
