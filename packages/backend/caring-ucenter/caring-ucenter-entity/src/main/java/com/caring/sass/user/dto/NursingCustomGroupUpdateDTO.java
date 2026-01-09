package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 医生的自定义小组
 * </p>
 *
 * @author yangshuai
 * @since 2022-08-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "NursingCustomGroupUpdateDTO", description = "医助的自定义小组")
public class NursingCustomGroupUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "用户Id")
    @NotNull
    private Long userId;
    /**
     * 小组名称
     */
    @ApiModelProperty(value = "小组名称")
    @Length(max = 300, message = "小组名称长度不能超过300")
    private String name;

    @ApiModelProperty(value = "要加入的患者id集合")
    private Set<Long> joinPatientIds;


    @ApiModelProperty(value = "移出的患者id集合")
    private Set<Long> removePatientIds;


}
