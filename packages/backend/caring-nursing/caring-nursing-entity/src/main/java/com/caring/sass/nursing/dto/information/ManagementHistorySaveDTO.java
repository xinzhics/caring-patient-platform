package com.caring.sass.nursing.dto.information;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 管理历史
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
@ApiModel(value = "ManagementHistorySaveDTO", description = "管理历史")
public class ManagementHistorySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 信息完整度
     */
    @ApiModelProperty(value = "信息完整度")
    @Length(max = 255, message = "信息完整度长度不能超过255")
    private String historyType;
    /**
     * 患者数量
     */
    @ApiModelProperty(value = "患者数量")
    private Integer patientNumber;
    /**
     * 医助的ID
     */
    @ApiModelProperty(value = "医助的ID")
    private Long nursingId;
    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    private Long orgId;

}
