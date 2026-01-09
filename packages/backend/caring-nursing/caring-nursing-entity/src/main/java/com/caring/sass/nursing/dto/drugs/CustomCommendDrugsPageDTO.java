package com.caring.sass.nursing.dto.drugs;

import java.time.LocalDateTime;
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
 * 自定义推荐药品
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CustomCommendDrugsPageDTO", description = "自定义推荐药品")
public class CustomCommendDrugsPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Long drugsId;
    @ApiModelProperty(value = "")
    private Integer number;
    @ApiModelProperty(value = "")
    private Float dosage;
    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    private String time;
    @ApiModelProperty(value = "")
    private Integer cycle;
    @ApiModelProperty(value = "")
    private Integer order;
    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 255, message = "药品名称长度不能超过255")
    private String drugsName;

}
