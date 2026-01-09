package com.caring.sass.ai.dto.humanVideo;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
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
@ApiModel(value = "BusinessVolcengineTimbreUpdateDTO", description = "火山音色管理")
public class BusinessVolcengineTimbreUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
    private LocalDateTime expirationDate;
    /**
     * 剩余次数
     */
    @ApiModelProperty(value = "剩余次数")
    private Integer remainingTimes;

}
