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
 * AI创作日使用数据累计
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
@TableName("m_ai_article_daily_usage_data")
@ApiModel(value = "ArticleDailyUsageData", description = "AI创作日使用数据累计")
@AllArgsConstructor
public class ArticleDailyUsageData extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id")
    @TableField("user_id")
    @Excel(name = "用户Id")
    private Long userId;

    /**
     * 周次(2025年6月2日为初始日期)
     */
    @ApiModelProperty(value = "周次(2025年6月2日为初始日期)")
    @TableField("week_number")
    @Excel(name = "周次(2025年6月2日为初始日期)")
    private Integer weekNumber;

    /**
     * 注册用户数量
     */
    @ApiModelProperty(value = "注册用户数量")
    @TableField("register_number")
    @Excel(name = "注册用户数量")
    private Long registerNumber;

    /**
     * 活跃用户数量
     */
    @ApiModelProperty(value = "活跃用户数量")
    @TableField("active_number")
    @Excel(name = "活跃用户数量")
    private Long activeNumber;

    /**
     * 完成首次创作用户数量
     */
    @ApiModelProperty(value = "注册当天完成首次创作用户数量")
    @TableField("produce_first_complete_user_number")
    @Excel(name = "完成首次创作用户数量")
    private Long produceFirstCompleteUserNumber;

    /**
     * 完成创作用户数量
     */
    @ApiModelProperty(value = "完成创作用户数量")
    @TableField("produce_complete_user_number")
    @Excel(name = "完成创作用户数量")
    private Long produceCompleteUserNumber;

    /**
     * 开启创作用户数量
     */
    @ApiModelProperty(value = "开启创作用户数量")
    @TableField("produce_start_user_number")
    @Excel(name = "开启创作用户数量")
    private Long produceStartUserNumber;

    /**
     * 累计创作数量
     */
    @ApiModelProperty(value = "累计创作数量")
    @TableField("produce_complete_total_number")
    @Excel(name = "累计创作数量")
    private Long produceCompleteTotalNumber;

    /**
     * 创作异常数量(程序异常)
     */
    @ApiModelProperty(value = "创作异常数量(程序异常)")
    @TableField("produce_error_total_number")
    @Excel(name = "创作异常数量(程序异常)")
    private Long produceErrorTotalNumber;

    /**
     * 创作未完成数量(用户离开)
     */
    @ApiModelProperty(value = "创作未完成数量(用户离开)")
    @TableField("produce_unfinished_total_number")
    @Excel(name = "创作未完成数量(用户离开)")
    private Long produceUnfinishedTotalNumber;

    /**
     * 文章完成数量
     */
    @ApiModelProperty(value = "文章完成数量")
    @TableField("article_complete_number")
    @Excel(name = "文章完成数量")
    private Long articleCompleteNumber;


    /**
     * 原创科普文章创作
     */
    @ApiModelProperty(value = "原创科普文章创作")
    @TableField("article_original_science_number")
    @Excel(name = "原创科普文章创作")
    private Long articleOriginalScienceNumber;

    /**
     * 仿写科普文章创作
     */
    @ApiModelProperty(value = "仿写科普文章创作")
    @TableField("article_rewrite_science_number")
    @Excel(name = "仿写科普文章创作")
    private Long articleRewriteScienceNumber;

    /**
     * 原创社交媒体文案
     */
    @ApiModelProperty(value = "原创社交媒体文案")
    @TableField("article_original_social_number")
    @Excel(name = "原创社交媒体文案")
    private Long articleOriginalSocialNumber;

    /**
     * 仿写社交媒体文案
     */
    @ApiModelProperty(value = "仿写社交媒体文案")
    @TableField("article_rewrite_social_number")
    @Excel(name = "仿写社交媒体文案")
    private Long articleRewriteSocialNumber;

    /**
     * 原创短视频口播
     */
    @ApiModelProperty(value = "原创短视频口播")
    @TableField("article_original_video_number")
    @Excel(name = "原创短视频口播")
    private Long articleOriginalVideoNumber;

    /**
     * 仿写短视频口播
     */
    @ApiModelProperty(value = "仿写短视频口播")
    @TableField("article_rewrite_video_number")
    @Excel(name = "仿写短视频口播")
    private Long articleRewriteVideoNumber;

    /**
     * 数字人创作数量
     */
    @ApiModelProperty(value = "数字人创作数量")
    @TableField("article_original_digital_person_number")
    @Excel(name = "数字人创作数量")
    private Long articleOriginalDigitalPersonNumber;

    /**
     * 播客数量
     */
    @ApiModelProperty(value = "播客数量")
    @TableField("podcast_number")
    @Excel(name = "播客数量")
    private Long podcastNumber;

    /**
     * 播客完成数量
     */
    @ApiModelProperty(value = "播客完成数量")
    @TableField("podcast_complete_number")
    @Excel(name = "播客完成数量")
    private Long podcastCompleteNumber;

    /**
     * 视频完成数量
     */
    @ApiModelProperty(value = "视频完成数量")
    @TableField("video_complete_number")
    @Excel(name = "视频完成数量")
    private Long videoCompleteNumber;


    // 统计 前 8天注册的用户 留存率
    @ApiModelProperty(value = "7天留存率")
    @TableField(value = "day_7_active")
    private Integer day7Active;

    // 统计 前30天注册的用户， 留存率
    @ApiModelProperty(value = "30天留存率")
    @TableField(value = "day_30_active")
    private Integer day30Active;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    @Length(max = 255, message = "日期长度不能超过255")
    @TableField(value = "create_day", condition = LIKE)
    @Excel(name = "日期")
    private String createDay;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    @Length(max = 255, message = "月份长度不能超过255")
    @TableField(value = "create_month", condition = LIKE)
    @Excel(name = "月份")
    private String createMonth;


    @Builder
    public ArticleDailyUsageData(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, Integer weekNumber, Long registerNumber, Long activeNumber, Long produceFirstCompleteUserNumber, 
                    Long produceCompleteUserNumber, Long produceStartUserNumber, Long produceCompleteTotalNumber, Long produceErrorTotalNumber, Long produceUnfinishedTotalNumber, Long articleCompleteNumber, 
                    Long podcastCompleteNumber, Long videoCompleteNumber, String createDay, String createMonth) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.weekNumber = weekNumber;
        this.registerNumber = registerNumber;
        this.activeNumber = activeNumber;
        this.produceFirstCompleteUserNumber = produceFirstCompleteUserNumber;
        this.produceCompleteUserNumber = produceCompleteUserNumber;
        this.produceStartUserNumber = produceStartUserNumber;
        this.produceCompleteTotalNumber = produceCompleteTotalNumber;
        this.produceErrorTotalNumber = produceErrorTotalNumber;
        this.produceUnfinishedTotalNumber = produceUnfinishedTotalNumber;
        this.articleCompleteNumber = articleCompleteNumber;
        this.podcastCompleteNumber = podcastCompleteNumber;
        this.videoCompleteNumber = videoCompleteNumber;
        this.createDay = createDay;
        this.createMonth = createMonth;
    }

}
