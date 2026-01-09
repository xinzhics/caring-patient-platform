package com.caring.sass.nursing.dto.form;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 血糖表
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
@ApiModel(value = "SugarFormResultSaveDTO", description = "血糖表")
public class SugarFormResultSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 血糖值
     */
    @ApiModelProperty(value = "血糖值")
    private Float sugarValue;
    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    @NotNull(message = "会员ID不能为空")
    private Long patientId;

    @ApiModelProperty(value = "血糖从待办中进入后提交记录时需要上传")
    private Long planDetailTimeId;

    @ApiModelProperty(value = "")
    private Integer delFlag;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 100, message = "备注长度不能超过100")
    private String remarks;
    /**
     * 类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)
     */
    @ApiModelProperty(value = "类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)")
    private Integer type;


    @ApiModelProperty(value = "记录的日期(非填写日期) 比如 2020年1月1日0时0分0秒的 13位时间戳")
    public Long createDay;

    @ApiModelProperty(value = "记录的时间点 比如 15:25")
    public String time;

    @ApiModelProperty(value = "推送消息的ID")
    private Long messageId;

    @ApiModelProperty(value = "IM推荐功能回执 此字段为1 表示提交的表单是因为im的推荐功能提交的，其中的messageId是chat对象的id")
    @TableField(exist = false)
    private Integer imRecommendReceipt;
}
