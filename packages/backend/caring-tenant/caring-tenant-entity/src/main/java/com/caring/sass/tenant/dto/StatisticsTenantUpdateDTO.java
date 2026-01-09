package com.caring.sass.tenant.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
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
 * 项目统计
 * </p>
 *
 * @author leizhi
 * @since 2021-11-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "StatisticsTenantUpdateDTO", description = "项目统计")
public class StatisticsTenantUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty(value = "")
    @Length(max = 20, message = "长度不能超过20")
    private String code;
    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    private String name;
    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    private String logo;
    /**
     * 域名
     */
    @ApiModelProperty(value = "域名")
    @Length(max = 20, message = "域名长度不能超过20")
    private String domainName;
    /**
     * 全部会员数
     */
    @ApiModelProperty(value = "全部会员数")
    private Integer patientNum;
    /**
     * 注册会员数
     */
    @ApiModelProperty(value = "注册会员数")
    private Integer patientRegisterNum;
    /**
     * 未注册会员数
     */
    @ApiModelProperty(value = "未注册会员数")
    private Integer patientNoRegisterNum;
    /**
     * 全部医生数
     */
    @ApiModelProperty(value = "全部医生数")
    private Integer doctorNum;
    /**
     * 注册医生（已登录）数
     */
    @ApiModelProperty(value = "注册医生（已登录）数")
    private Integer doctorRegisterNum;
    /**
     * 未注册医生数
     */
    @ApiModelProperty(value = "未注册医生数")
    private Integer doctorNoRegisterNum;
    /**
     * 全部医助数
     */
    @ApiModelProperty(value = "全部医助数")
    private Integer nursingStaffNum;
}
