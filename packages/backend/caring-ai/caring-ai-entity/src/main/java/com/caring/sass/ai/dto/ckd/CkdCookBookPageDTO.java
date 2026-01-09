package com.caring.sass.ai.dto.ckd;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 
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
@ApiModel(value = "CkdCookBookPageDTO", description = "")
public class CkdCookBookPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 碳水化合物
     */
    @ApiModelProperty(value = "碳水化合物")
    private Double carbohydrates;
    /**
     * 食谱详情
     */
    @ApiModelProperty(value = "食谱详情")
    private String detailContent;
    /**
     * 脂肪
     */
    @ApiModelProperty(value = "脂肪")
    private Double fat;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 255, message = "名称长度不能超过255")
    private String name;
    /**
     * 蛋白质
     */
    @ApiModelProperty(value = "蛋白质")
    private Double protein;
    /**
     * 味道
     */
    @ApiModelProperty(value = "味道")
    @Length(max = 255, message = "味道长度不能超过255")
    private String taste;
    /**
     * 
     * 总热量
     */
    @ApiModelProperty(value = "")
    private Double totalHeatEnergy;
    /**
     * 早中晚类型
     */
    @ApiModelProperty(value = "早中晚类型")
    @Length(max = 255, message = "早中晚类型长度不能超过255")
    private String type;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    private String describeInfo;
    /**
     * 食谱图片
     */
    @ApiModelProperty(value = "食谱图片")
    @Length(max = 255, message = "食谱图片长度不能超过255")
    private String fileUrl;

}
