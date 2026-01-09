package com.caring.sass.ai.entity.ckd;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * 用户食谱计划
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-08
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_cookbook_plan")
@ApiModel(value = "CkdCookbookPlan", description = "用户食谱计划")
@AllArgsConstructor
public class CkdCookbookPlan extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    /**
     * 生成时间
     */
    @ApiModelProperty(value = "生成时间")
    @TableField("present_date")
    private LocalDate presentDate;

    /**
     * 早餐
     */
    @ApiModelProperty(value = "早餐")
    @NotNull(message = "早餐不能为空")
    @TableField("breakfast_id")
    @Excel(name = "早餐")
    private Long breakfastId;

    /**
     * 午餐
     */
    @ApiModelProperty(value = "午餐")
    @NotNull(message = "午餐不能为空")
    @TableField("lunch_id")
    @Excel(name = "午餐")
    private Long lunchId;

    /**
     * 晚餐
     */
    @ApiModelProperty(value = "晚餐")
    @NotNull(message = "晚餐不能为空")
    @TableField("dinner_id")
    @Excel(name = "晚餐")
    private Long dinnerId;

    /**
     * 是否复制
     */
    @ApiModelProperty(value = "是否复制")
    @NotNull(message = "是否复制不能为空")
    @TableField("is_duplicate")
    @Excel(name = "是否复制")
    private Boolean isDuplicate;

    @TableField(exist = false)
    private CkdCookBook breakfast;

    @TableField(exist = false)
    private CkdCookBook lunch;

    @TableField(exist = false)
    private CkdCookBook dinner;


    @Builder
    public CkdCookbookPlan(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String openId, LocalDate presentDate, Long breakfastId, Long lunchId, Long dinnerId, Boolean isDuplicate) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.openId = openId;
        this.presentDate = presentDate;
        this.breakfastId = breakfastId;
        this.lunchId = lunchId;
        this.dinnerId = dinnerId;
        this.isDuplicate = isDuplicate;
    }

}
