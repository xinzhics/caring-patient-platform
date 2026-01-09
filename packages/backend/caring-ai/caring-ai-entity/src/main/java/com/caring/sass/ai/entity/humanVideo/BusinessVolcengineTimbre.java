package com.caring.sass.ai.entity.humanVideo;

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
 * 火山音色管理
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-14
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Deprecated
@TableName("m_ai_business_volcengine_timbre")
@ApiModel(value = "BusinessVolcengineTimbre", description = "火山音色管理")
@AllArgsConstructor
public class BusinessVolcengineTimbre extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 声音ID
     */
    @ApiModelProperty(value = "声音ID")
    @NotEmpty(message = "声音ID不能为空")
    @Length(max = 255, message = "声音ID长度不能超过255")
    @TableField(value = "timbre_id", condition = LIKE)
    @Excel(name = "声音ID")
    private String timbreId;

    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    @TableField("expiration_date")
    @Excel(name = "到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationDate;

    /**
     * 剩余次数
     */
    @ApiModelProperty(value = "剩余次数")
    @TableField("remaining_times")
    @Excel(name = "剩余次数")
    private Integer remainingTimes;

    /**
     * 音色状态(空闲中，训练中，语音合成中，无效)
     */
    @ApiModelProperty(value = "音色状态(空闲中，训练中，语音合成中，无效)")
    @NotEmpty(message = "音色状态(空闲中，训练中，语音合成中，无效)不能为空")
    @TableField(value = "timbre_status", condition = LIKE)
    @Excel(name = "音色状态(空闲中，训练中，语音合成中，无效)")
    private VolcengineTimbreStatus timbreStatus;


    @Builder
    public BusinessVolcengineTimbre(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String timbreId, LocalDateTime expirationDate, Integer remainingTimes, VolcengineTimbreStatus timbreStatus) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.timbreId = timbreId;
        this.expirationDate = expirationDate;
        this.remainingTimes = remainingTimes;
        this.timbreStatus = timbreStatus;
    }

}
