package com.caring.sass.nursing.dto.form;

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
import com.caring.sass.base.entity.SuperEntity;
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
 * 表单设置的成绩规则
 * </p>
 *
 * @author 杨帅
 * @since 2023-10-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FormScoreRuleUpdateDTO", description = "表单设置的成绩规则")
public class FormScoreRuleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 结果计算方式(sum_score, average_score, sum_average_score)
     */
    @ApiModelProperty(value = "结果计算方式(sum_score, average_score, sum_average_score)")
    @Length(max = 255, message = "结果计算方式(sum_score, average_score, sum_average_score)长度不能超过255")
    private String formResultCountWay;
    /**
     * 展示总分
     */
    @ApiModelProperty(value = "展示总分")
    private Integer showResultSum;
    /**
     * 展示分组之和
     */
    @ApiModelProperty(value = "展示分组之和")
    private Integer showGroupSum;
    /**
     * 展示平均分
     */
    @ApiModelProperty(value = "展示平均分")
    private Integer showAverage;
    /**
     * 所属表单ID
     */
    @ApiModelProperty(value = "所属表单ID")
    private Long formId;
}
