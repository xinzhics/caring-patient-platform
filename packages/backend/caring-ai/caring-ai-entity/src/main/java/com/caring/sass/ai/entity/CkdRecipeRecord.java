package com.caring.sass.ai.entity;

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
 * 用户食谱记录
 * </p>
 *
 * @author leizhi
 * @since 2025-04-23
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_recipe_record")
@ApiModel(value = "CkdRecipeRecord", description = "用户食谱记录")
@AllArgsConstructor
public class CkdRecipeRecord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    @ApiModelProperty(value = "")
    @Length(max = 200, message = "长度不能超过200")
    @TableField(value = "image_url", condition = LIKE)
    @Excel(name = "")
    private String imageUrl;

    /**
     * 食谱名
     */
    @ApiModelProperty(value = "食谱名")
    @Length(max = 255, message = "食谱名长度不能超过255")
    @TableField(value = "recipe_name", condition = LIKE)
    @Excel(name = "食谱名")
    private String recipeName;

    /**
     * 食谱类型
     */
    @ApiModelProperty(value = "食谱类型")
    @Length(max = 32, message = "食谱类型长度不能超过32")
    @TableField(value = "meal_type", condition = LIKE)
    @Excel(name = "食谱类型")
    private String mealType;

    /**
     * 卡路里
     */
    @ApiModelProperty(value = "卡路里")
    @Length(max = 32, message = "卡路里长度不能超过32")
    @TableField(value = "calories", condition = LIKE)
    @Excel(name = "卡路里")
    private String calories;

    /**
     * 营养数据json
     */
    @ApiModelProperty(value = "营养数据json")
    @Length(max = 2048, message = "营养数据json长度不能超过2048")
    @TableField(value = "nutrition", condition = LIKE)
    @Excel(name = "营养数据json")
    private String nutrition;

    /**
     * 食材列表json
     */
    @ApiModelProperty(value = "食材列表json")
    @Length(max = 1024, message = "食材列表json长度不能超过1024")
    @TableField(value = "ingredients", condition = LIKE)
    @Excel(name = "食材列表json")
    private String ingredients;


    @Builder
    public CkdRecipeRecord(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String openId, String imageUrl, String recipeName, String mealType, String calories, 
                    String nutrition, String ingredients) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.openId = openId;
        this.imageUrl = imageUrl;
        this.recipeName = recipeName;
        this.mealType = mealType;
        this.calories = calories;
        this.nutrition = nutrition;
        this.ingredients = ingredients;
    }

}
