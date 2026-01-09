package com.caring.sass.nursing.dto.information;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

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
@ApiModel(value = "ManagementHistoryDetailSaveDTO", description = "管理历史详细记录")
public class ManagementHistoryDetailSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
