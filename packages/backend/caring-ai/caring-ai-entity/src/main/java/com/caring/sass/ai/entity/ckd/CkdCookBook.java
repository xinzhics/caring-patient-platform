package com.caring.sass.ai.entity.ckd;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @author 杨帅
 * @since 2025-02-08
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_cook_book")
@ApiModel(value = "CkdCookBook", description = "")
@AllArgsConstructor
public class CkdCookBook extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 碳水化合物
     */
    @ApiModelProperty(value = "碳水化合物")
    @TableField("carbohydrates")
    @Excel(name = "碳水化合物")
    private Double carbohydrates;

    /**
     * 食谱详情
     */
    @ApiModelProperty(value = "食谱详情")

    @TableField("detail_content")
    @Excel(name = "食谱详情")
    private String detailContent;

    /**
     * 脂肪
     */
    @ApiModelProperty(value = "脂肪")
    @TableField("fat")
    @Excel(name = "脂肪")
    private Double fat;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 255, message = "名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 蛋白质
     */
    @ApiModelProperty(value = "蛋白质")
    @TableField("protein")
    @Excel(name = "蛋白质")
    private Double protein;

    /**
     * 味道
     */
    @ApiModelProperty(value = "味道")
    @Length(max = 255, message = "味道长度不能超过255")
    @TableField(value = "taste", condition = LIKE)
    @Excel(name = "味道")
    private String taste;

    /**
     * 
     * 总热量
     */
    @ApiModelProperty(value = "总热量")
    @TableField("total_heat_energy")
    private Double totalHeatEnergy;

    /**
     * 早中晚类型
     */
    @ApiModelProperty(value = "早中晚类型")
    @Length(max = 255, message = "早中晚类型长度不能超过255")
    @TableField(value = "type", condition = LIKE)
    @Excel(name = "早中晚类型")
    private String type;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_info", condition = LIKE)
    @Excel(name = "描述")
    private String describeInfo;

    /**
     * 食谱图片
     */
    @ApiModelProperty(value = "食谱图片")
    @Length(max = 255, message = "食谱图片长度不能超过255")
    @TableField(value = "file_url", condition = LIKE)
    @Excel(name = "食谱图片")
    private String fileUrl;




}
