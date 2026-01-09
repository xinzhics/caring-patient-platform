package com.caring.sass.tenant.dto;

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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 项目运营配置
 * </p>
 *
 * @author 杨帅
 * @since 2023-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantOperateSaveDTO", description = "项目运营配置")
public class TenantOperateSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 机构是否限制 0：不 1：限制
     */
    @ApiModelProperty(value = "机构是否限制 0：不 1：限制")
    private Integer organLimitation;
    /**
     * 机构限制数量
     */
    @ApiModelProperty(value = "机构限制数量")
    private Integer organLimitationNumber;
    /**
     * 医助是否限制 0：不 1：限制
     */
    @ApiModelProperty(value = "医助是否限制 0：不 1：限制")
    private Integer nursingLimitation;
    /**
     * 医助限制数量
     */
    @ApiModelProperty(value = "医助限制数量")
    private Integer nursingLimitationNumber;
    /**
     * 医生是否限制
     */
    @ApiModelProperty(value = "医生是否限制")
    private Integer doctorLimitation;
    /**
     * 医生限制数量
     */
    @ApiModelProperty(value = "医生限制数量")
    private Integer doctorLimitationNumber;
    /**
     * 时长是否限制 0：不， 1：限制
     */
    @ApiModelProperty(value = "时长是否限制 0：不， 1：限制")
    private Integer durationLimitation;
    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "项目的租户")
    @NotEmpty(message = "租户不能为空")
    private String tenantCode;

}
