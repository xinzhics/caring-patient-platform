package com.caring.sass.nursing.dto.information;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 患者信息完整度概览表
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CompletenessInformationUpdateDTO", description = "患者信息完整度概览表")
public class CompletenessInformationUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;
    /**
     * 最后填写时间
     */
    @ApiModelProperty(value = "最后填写时间")
    private LocalDateTime lastWriteTime;
    /**
     * 患者信息完整度
     */
    @ApiModelProperty(value = "患者信息完整度")
    private Integer completion;
    /**
     * 患者信息是否完整
     */
    @ApiModelProperty(value = "患者信息是否完整")
    private Integer complete;
}
