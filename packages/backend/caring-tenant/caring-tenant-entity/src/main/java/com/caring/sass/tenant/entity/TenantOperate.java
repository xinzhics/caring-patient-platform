package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_operate")
@ApiModel(value = "TenantOperate", description = "项目运营配置")
@AllArgsConstructor
public class TenantOperate extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构是否限制 0：不 1：限制
     */
    @ApiModelProperty(value = "机构是否限制 0：不 1：限制")
    @TableField("organ_limitation")
    @Excel(name = "机构是否限制 0：不 1：限制")
    private Integer organLimitation;

    /**
     * 机构限制数量
     */
    @ApiModelProperty(value = "机构限制数量")
    @TableField("organ_limitation_number")
    @Excel(name = "机构限制数量")
    private Integer organLimitationNumber;

    /**
     * 医助是否限制 0：不 1：限制
     */
    @ApiModelProperty(value = "医助是否限制 0：不 1：限制")
    @TableField("nursing_limitation")
    @Excel(name = "医助是否限制 0：不 1：限制")
    private Integer nursingLimitation;

    /**
     * 医助限制数量
     */
    @ApiModelProperty(value = "医助限制数量")
    @TableField("nursing_limitation_number")
    @Excel(name = "医助限制数量")
    private Integer nursingLimitationNumber;

    /**
     * 医生是否限制
     */
    @ApiModelProperty(value = "医生是否限制")
    @TableField("doctor_limitation")
    @Excel(name = "医生是否限制")
    private Integer doctorLimitation;

    /**
     * 医生限制数量
     */
    @ApiModelProperty(value = "医生限制数量")
    @TableField("doctor_limitation_number")
    @Excel(name = "医生限制数量")
    private Integer doctorLimitationNumber;

    /**
     * 时长是否限制 0：不， 1：限制
     */
    @ApiModelProperty(value = "时长是否限制 0：不， 1：限制")
    @TableField("duration_limitation")
    @Excel(name = "时长是否限制 0：不， 1：限制")
    private Integer durationLimitation;

    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    @TableField("expiration_time")
    @Excel(name = "到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "剩余天数")
    @TableField(value = "days_remaining")
    private Integer daysRemaining;

    @ApiModelProperty(value = "项目的租户")
    @TableField(value = "tenant_code")
    private String tenantCode;

    @ApiModelProperty(value = "项目ID")
    @TableField(value = "tenant_id")
    private Long tenantId;

    @Builder
    public TenantOperate(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    Integer organLimitation, Integer organLimitationNumber, Integer nursingLimitation, Integer nursingLimitationNumber, Integer doctorLimitation, 
                    Integer doctorLimitationNumber, Integer durationLimitation, LocalDateTime expirationTime, Integer daysRemaining, String tenantCode, Long tenantId) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.organLimitation = organLimitation;
        this.organLimitationNumber = organLimitationNumber;
        this.nursingLimitation = nursingLimitation;
        this.nursingLimitationNumber = nursingLimitationNumber;
        this.doctorLimitation = doctorLimitation;
        this.doctorLimitationNumber = doctorLimitationNumber;
        this.durationLimitation = durationLimitation;
        this.expirationTime = expirationTime;
        this.daysRemaining = daysRemaining;
        this.tenantCode = tenantCode;
        this.tenantId = tenantId;
    }

}
