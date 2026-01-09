package com.caring.sass.nursing.dto.information;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "ManagementHistoryUpdateDTO", description = "管理历史")
public class ManagementHistoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
