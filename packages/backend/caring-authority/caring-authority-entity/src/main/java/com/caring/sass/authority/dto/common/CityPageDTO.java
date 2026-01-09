package com.caring.sass.authority.dto.common;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 地区表
 * </p>
 *
 * @author caring
 * @since 2020-02-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CityPageDTO", description = "市")
public class CityPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("市区名称")
    private String city;


    @ApiModelProperty("省ID")
    private Long provinceId;


}
