package com.caring.sass.user.dto;

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
 * 护理专员统计
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
@ApiModel(value = "StatisticsNursingStaffUpdateDTO", description = "护理专员统计")
public class StatisticsNursingStaffUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Long nursingStaffId;
    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    @Length(max = 32, message = "医生姓名长度不能超过32")
    private String name;
    /**
     * 医生头像
     */
    @ApiModelProperty(value = "医生头像")
    @Length(max = 250, message = "医生头像长度不能超过250")
    private String avatar;
    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构")
    @Length(max = 50, message = "所属机构长度不能超过50")
    private String organName;
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
}
