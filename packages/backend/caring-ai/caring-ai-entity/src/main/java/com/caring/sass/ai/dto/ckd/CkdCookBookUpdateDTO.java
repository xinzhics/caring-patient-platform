package com.caring.sass.ai.dto.ckd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

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
@ApiModel(value = "CkdCookBookUpdateDTO", description = "")
public class CkdCookBookUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 食谱图片
     */
    @ApiModelProperty(value = "食谱图片")
    @Length(max = 255, message = "食谱图片长度不能超过255")
    private String fileUrl;
}
