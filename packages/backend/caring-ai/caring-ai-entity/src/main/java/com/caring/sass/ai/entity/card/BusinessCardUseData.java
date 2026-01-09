package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
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
 * 名片使用数据收集
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-26
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_use_data")
@ApiModel(value = "BusinessCardUseData", description = "名片使用数据收集")
@AllArgsConstructor
public class BusinessCardUseData extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名片ID
     */
    @ApiModelProperty(value = "名片ID")
    @NotNull(message = "名片ID不能为空")
    @TableField("business_card_id")
    @Excel(name = "名片ID")
    private Long businessCardId;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @NotEmpty(message = "openId不能为空")
    @Length(max = 30, message = "openId长度不能超过30")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    /**
     * 数据类型
     */
    @ApiModelProperty(value = "数据类型")
    @NotNull(message = "数据类型不能为空")
    @TableField(value = "data_type", condition = LIKE)
    @Excel(name = "数据类型")
    private BusinessCardUseDataType dataType;




}
