package com.caring.sass.ai.entity.article;

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
 * AI创作用户事件日志
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-02
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_event_log")
@ApiModel(value = "ArticleEventLog", description = "AI创作用户事件日志")
@AllArgsConstructor
public class ArticleEventLog extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    @Length(max = 255, message = "日期长度不能超过255")
    @TableField(value = "create_day", condition = LIKE)
    @Excel(name = "日期")
    private String createDay;

    /**
     * 周次(2025年6月2日为初始日期)
     */
    @ApiModelProperty(value = "周次(2025年6月2日为初始日期)")
    @TableField("week_number")
    @Excel(name = "周次(2025年6月2日为初始日期)")
    private Integer weekNumber;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    @Length(max = 255, message = "月份长度不能超过255")
    @TableField(value = "create_month", condition = LIKE)
    @Excel(name = "月份")
    private String createMonth;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    @Length(max = 400, message = "事件类型长度不能超过400")
    @TableField(value = "event_type", condition = LIKE)
    @Excel(name = "事件类型")
    private ArticleEventLogType eventType;

    /**
     * 事件相关的任务ID
     */
    @ApiModelProperty(value = "事件相关的任务ID")
    @TableField("task_id")
    @Excel(name = "事件相关的任务ID")
    private Long taskId;


    @Builder
    public ArticleEventLog(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String createDay, Integer weekNumber, String createMonth, Long userId, ArticleEventLogType eventType, Long taskId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.createDay = createDay;
        this.weekNumber = weekNumber;
        this.createMonth = createMonth;
        this.userId = userId;
        this.eventType = eventType;
        this.taskId = taskId;
    }

}
