package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
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
 * 科普创作会员免费额度
 * </p>
 *
 * @author 杨帅
 * @since 2025-03-27
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_user_free_limit")
@ApiModel(value = "ArticleUserFreeLimit", description = "科普创作会员免费额度")
@AllArgsConstructor
public class ArticleUserFreeLimit extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 音色克隆次数
     */
    @ApiModelProperty(value = "音色克隆次数")
    @NotNull(message = "音色克隆次数不能为空")
    @TableField("voice_remaining_times")
    @Excel(name = "音色克隆次数")
    private Integer voiceRemainingTimes;

    /**
     * 形象次数
     */
    @ApiModelProperty(value = "形象次数")
    @NotNull(message = "形象次数不能为空")
    @TableField("human_remaining_times")
    @Excel(name = "形象次数")
    private Integer humanRemainingTimes;

    /**
     * 可用额度到期时间
     */
    @ApiModelProperty(value = "可用额度到期时间")
    @TableField("expiration_date")
    @Excel(name = "可用额度到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationDate;


    @Builder
    public ArticleUserFreeLimit(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, Integer voiceRemainingTimes, Integer humanRemainingTimes, LocalDateTime expirationDate) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.voiceRemainingTimes = voiceRemainingTimes;
        this.humanRemainingTimes = humanRemainingTimes;
        this.expirationDate = expirationDate;
    }

}
