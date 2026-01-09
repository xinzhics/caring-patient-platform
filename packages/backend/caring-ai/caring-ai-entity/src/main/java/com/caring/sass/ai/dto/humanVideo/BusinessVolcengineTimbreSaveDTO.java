package com.caring.sass.ai.dto.humanVideo;

import java.time.LocalDateTime;

import com.caring.sass.ai.entity.humanVideo.VolcengineTimbreStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessVolcengineTimbreSaveDTO", description = "火山音色管理")
public class BusinessVolcengineTimbreSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 声音ID
     */
    @ApiModelProperty(value = "声音ID")
    @NotEmpty(message = "声音ID不能为空")
    @Length(max = 255, message = "声音ID长度不能超过255")
    private String timbreId;
    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    @NotNull
    private LocalDateTime expirationDate;
    /**
     * 剩余次数
     */
    @ApiModelProperty(value = "剩余次数")
    @NotNull
    private Integer remainingTimes;

}
