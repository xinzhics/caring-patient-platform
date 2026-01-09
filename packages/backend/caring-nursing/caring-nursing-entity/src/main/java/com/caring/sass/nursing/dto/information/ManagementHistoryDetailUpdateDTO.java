package com.caring.sass.nursing.dto.information;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 管理历史详细记录
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
@ApiModel(value = "ManagementHistoryDetailUpdateDTO", description = "管理历史详细记录")
public class ManagementHistoryDetailUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 患者数量
     */
    @ApiModelProperty(value = "患者数量")
    private Integer patientNumber;
    /**
     * 管理历史ID
     */
    @ApiModelProperty(value = "管理历史ID")
    private Long historyId;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Long doctorId;
}
