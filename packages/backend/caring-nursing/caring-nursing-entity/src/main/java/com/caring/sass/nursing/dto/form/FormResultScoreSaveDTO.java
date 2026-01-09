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
 * 表单结果的成绩计算
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
@ApiModel(value = "FormResultScoreSaveDTO", description = "表单结果的成绩计算")
public class FormResultScoreSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总分
     */
    @ApiModelProperty(value = "总分")
    private Integer formResultSumScore;
    /**
     * 平均分
     */
    @ApiModelProperty(value = "平均分")
    private Float formResultAverageScore;
    /**
     * 总分和+平均分
     */
    @ApiModelProperty(value = "总分和+平均分")
    private Float formResultSumAverageScore;
    /**
     * 各分组成绩json数据对象
     */
    @ApiModelProperty(value = "各分组成绩json数据对象")
    private String formFieldGroupSumInfo;

}
