package com.caring.sass.ai.dto.ckd;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdCookbookPlanSaveDTO", description = "用户食谱计划")
public class CkdCookbookPlanSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    private String openId;
    /**
     * 生成时间
     */
    @ApiModelProperty(value = "生成时间")
    private LocalDateTime presentDate;
    /**
     * 早餐
     */
    @ApiModelProperty(value = "早餐")
    @NotNull(message = "早餐不能为空")
    private Long breakfastId;
    /**
     * 午餐
     */
    @ApiModelProperty(value = "午餐")
    @NotNull(message = "午餐不能为空")
    private Long lunchId;
    /**
     * 晚餐
     */
    @ApiModelProperty(value = "晚餐")
    @NotNull(message = "晚餐不能为空")
    private Long dinnerId;
    /**
     * 是否复制
     */
    @ApiModelProperty(value = "是否复制")
    @NotNull(message = "是否复制不能为空")
    private Integer isDuplicate;

}
