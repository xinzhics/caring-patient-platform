package com.caring.sass.nursing.dto.traceInto;

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
 * 选项跟踪异常题目明细记录表
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TraceIntoResultFieldAbnormalUpdateDTO", description = "选项跟踪异常题目明细记录表")
public class TraceIntoResultFieldAbnormalUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 记录当前选项异常结果所属哪条选项结果
     */
    @ApiModelProperty(value = "记录当前选项异常结果所属哪条选项结果")
    @NotNull(message = "记录当前选项异常结果所属哪条选项结果不能为空")
    private Long traceIntoResultId;
    /**
     * 表单结果ID
     */
    @ApiModelProperty(value = "表单结果ID")
    @NotNull(message = "表单结果ID不能为空")
    private Long formResultId;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @NotNull(message = "医助ID不能为空")
    private Long nursingId;
    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "随访计划ID")
    @NotNull(message = "随访计划ID不能为空")
    private Long planId;
    /**
     * 父题目的ID
     */
    @ApiModelProperty(value = "父题目的ID")
    @Length(max = 100, message = "父题目的ID长度不能超过100")
    private String parentFieldId;
    /**
     * 父题目选项ID
     */
    @ApiModelProperty(value = "父题目选项ID")
    @Length(max = 100, message = "父题目选项ID长度不能超过100")
    private String parentFieldOptionId;
    /**
     * 异常选项所在的题目ID
     */
    @ApiModelProperty(value = "异常选项所在的题目ID")
    @Length(max = 100, message = "异常选项所在的题目ID长度不能超过100")
    private String formFieldId;
    /**
     * 异常选项的ID
     */
    @ApiModelProperty(value = "异常选项的ID")
    @Length(max = 100, message = "异常选项的ID长度不能超过100")
    private String formFieldOptionId;
}
