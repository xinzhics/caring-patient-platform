package com.caring.sass.nursing.entity.form;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
 * 血糖表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_sugar_form_result")
@ApiModel(value = "SugarFormResult", description = "血糖表")
@AllArgsConstructor
public class SugarFormResult extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 血糖值
     */
    @ApiModelProperty(value = "血糖值")
    @TableField("sugar_value")
    @Excel(name = "血糖值")
    private Float sugarValue;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    @Length(max = 32, message = "会员ID长度不能超过32")
    @TableField(value = "patient_id", condition = LIKE)
    @Excel(name = "会员ID")
    private Long patientId;

    @ApiModelProperty(value = "血糖从待办中进入后提交记录时需要上传")
    @TableField(exist = false)
    private Long planDetailTimeId;

    @ApiModelProperty(value = "")
    @TableField("del_flag")
    @Excel(name = "")
    private Integer delFlag;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 100, message = "备注长度不能超过100")
    @TableField(value = "remarks", condition = LIKE)
    @Excel(name = "备注")
    private String remarks;

    /**
     * 类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)
     */
    @ApiModelProperty(value = "类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)")
    @TableField("type")
    @Excel(name = "类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)")
    private Integer type;

    @ApiModelProperty(value = "记录的日期(非填写日期) 比如 2020年1月1日0时0分0秒的 13位时间戳")
    @TableField("create_day")
    public Long createDay;

    @ApiModelProperty(value = "记录的时间点 比如 15:25")
    @TableField("time_")
    public String time;

    @ApiModelProperty(value = "推送消息的ID")
    @TableField("message_id")
    private Long messageId;

    @ApiModelProperty(value = "数据反馈的结果JSON, null不用显示")
    @TableField("data_feed_back")
    private String dataFeedBack;

    @ApiModelProperty(value = "IM推荐功能回执 此字段为1 表示提交的表单是因为im的推荐功能提交的，其中的messageId是chat对象的id")
    @TableField(exist = false)
    private Integer imRecommendReceipt;

}
