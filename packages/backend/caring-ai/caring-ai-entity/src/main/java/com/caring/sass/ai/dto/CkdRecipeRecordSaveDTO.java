package com.caring.sass.ai.dto;

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
 * 用户食谱记录
 * </p>
 *
 * @author leizhi
 * @since 2025-04-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdRecipeRecordSaveDTO", description = "用户食谱记录")
public class CkdRecipeRecordSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    private String openId;
    @ApiModelProperty(value = "")
    @Length(max = 200, message = "长度不能超过200")
    private String imageUrl;
    /**
     * 食谱名
     */
    @ApiModelProperty(value = "食谱名")
    @Length(max = 255, message = "食谱名长度不能超过255")
    private String recipeName;
    /**
     * 食谱类型
     */
    @ApiModelProperty(value = "食谱类型")
    @Length(max = 32, message = "食谱类型长度不能超过32")
    private String mealType;
    /**
     * 卡路里
     */
    @ApiModelProperty(value = "卡路里")
    @Length(max = 32, message = "卡路里长度不能超过32")
    private String calories;
    /**
     * 营养数据json
     */
    @ApiModelProperty(value = "营养数据json")
    @Length(max = 2048, message = "营养数据json长度不能超过2048")
    private String nutrition;
    /**
     * 食材列表json
     */
    @ApiModelProperty(value = "食材列表json")
    @Length(max = 1024, message = "食材列表json长度不能超过1024")
    private String ingredients;

}
