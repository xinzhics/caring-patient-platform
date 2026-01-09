package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 名片使用数据日统计
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
@TableName("m_ai_business_card_use_day_statistics")
@ApiModel(value = "BusinessCardUseDayStatistics", description = "名片使用数据日统计")
@AllArgsConstructor
public class BusinessCardUseDayStatistics extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名片ID
     */
    @ApiModelProperty(value = "名片ID")
    @NotNull(message = "名片ID不能为空")
    @TableField("business_card_id")
    @Excel(name = "名片ID")
    private Long businessCardId;

    @ApiModelProperty(value = "医生姓名")
    @Length(max = 100, message = "医生姓名长度不能超过100")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "医生姓名")
    private String doctorName;

    /**
     * 名片用户的医院名称
     */
    @ApiModelProperty(value = "名片用户的医院名称")
    @NotEmpty(message = "名片用户的医院名称不能为空")
    @Length(max = 255, message = "名片用户的医院名称长度不能超过255")
    @TableField(value = "hospital_name", condition = LIKE)
    @Excel(name = "名片用户的医院名称")
    private String hospitalName;

    /**
     * 名片用户的科室名称
     */
    @ApiModelProperty(value = "名片用户的科室名称")
    @NotEmpty(message = "名片用户的科室名称不能为空")
    @Length(max = 255, message = "名片用户的科室名称长度不能超过255")
    @TableField(value = "department_name", condition = LIKE)
    @Excel(name = "名片用户的科室名称")
    private String departmentName;

    /**
     * 浏览人数
     */
    @ApiModelProperty(value = "浏览人数 一个人一天只+1次")
    @TableField("people_of_views")
    @Excel(name = "浏览人数")
    private Integer peopleOfViews;

    /**
     * 浏览次数
     */
    @ApiModelProperty(value = "浏览次数")
    @TableField("number_of_views")
    @Excel(name = "浏览次数")
    private Integer numberOfViews;

    /**
     * 转发次数
     */
    @ApiModelProperty(value = "转发次数")
    @TableField("forwarding_frequency")
    @Excel(name = "转发次数")
    private Integer forwardingFrequency;

    /**
     * AI对话点击人次
     */
    @ApiModelProperty(value = "AI对话点击人次")
    @TableField("ai_dialogue_number_count")
    @Excel(name = "AI对话点击人次")
    private Integer aiDialogueNumberCount;

    /**
     * AI对话点击次数
     */
    @ApiModelProperty(value = "AI对话点击次数")
    @TableField("ai_dialogue_click_count")
    @Excel(name = "AI对话点击次数")
    private Integer aiDialogueClickCount;

    @ApiModelProperty(value = "本日名片点赞数量")
    @TableField("give_thumbs_up_count")
    private Integer giveThumbsUpCount;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    @TableField("use_date")
    @Excel(name = "日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate useDate;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @TableField("organ_id")
    @Excel(name = "机构ID")
    private Long organId;


    @ApiModelProperty("小程序分享二维码")
    @TableField(exist = false)
    private String businessQrCode;


    @ApiModelProperty("激活时间")
    @TableField(exist = false)
    private LocalDateTime activationTime;


    @ApiModelProperty(value = "机构名称")
    @TableField(exist = false)
    private String organName;





}
