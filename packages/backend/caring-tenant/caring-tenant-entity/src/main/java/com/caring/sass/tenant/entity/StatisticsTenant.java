package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("s_statistics_tenant")
@ApiModel(value = "StatisticsTenant", description = "项目统计")
@AllArgsConstructor
public class StatisticsTenant extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    @Length(max = 20, message = "长度不能超过20")
    @TableField(value = "code", condition = LIKE)
    @Excel(name = "")
    private String code;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "")
    private String name;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "logo", condition = LIKE)
    @Excel(name = "")
    private String logo;

    /**
     * 域名
     */
    @ApiModelProperty(value = "域名")
    @Length(max = 20, message = "域名长度不能超过20")
    @TableField(value = "domain_name", condition = LIKE)
    @Excel(name = "域名")
    private String domainName;

    /**
     * 全部会员数
     */
    @ApiModelProperty(value = "全部会员数")
    @TableField("patient_num")
    @Excel(name = "全部会员数")
    private Integer patientNum;

    /**
     * 注册会员数
     */
    @ApiModelProperty(value = "注册会员数")
    @TableField("patient_register_num")
    @Excel(name = "注册会员数")
    private Integer patientRegisterNum;

    /**
     * 未注册会员数
     */
    @ApiModelProperty(value = "未注册会员数")
    @TableField("patient_no_register_num")
    @Excel(name = "未注册会员数")
    private Integer patientNoRegisterNum;

    /**
     * 全部医生数
     */
    @ApiModelProperty(value = "全部医生数")
    @TableField("doctor_num")
    @Excel(name = "全部医生数")
    private Integer doctorNum;

    /**
     * 注册医生（已登录）数
     */
    @ApiModelProperty(value = "注册医生（已登录）数")
    @TableField("doctor_register_num")
    @Excel(name = "注册医生（已登录）数")
    private Integer doctorRegisterNum;

    /**
     * 未注册医生数
     */
    @ApiModelProperty(value = "未注册医生数")
    @TableField("doctor_no_register_num")
    @Excel(name = "未注册医生数")
    private Integer doctorNoRegisterNum;

    /**
     * 全部医助数
     */
    @ApiModelProperty(value = "全部医助数")
    @TableField("nursing_staff_num")
    @Excel(name = "全部医助数")
    private Integer nursingStaffNum;


    @Builder
    public StatisticsTenant(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String code, String name, String logo, String domainName, Integer patientNum, 
                    Integer patientRegisterNum, Integer patientNoRegisterNum, Integer doctorNum, Integer doctorRegisterNum, Integer doctorNoRegisterNum, Integer nursingStaffNum) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.code = code;
        this.name = name;
        this.logo = logo;
        this.domainName = domainName;
        this.patientNum = patientNum;
        this.patientRegisterNum = patientRegisterNum;
        this.patientNoRegisterNum = patientNoRegisterNum;
        this.doctorNum = doctorNum;
        this.doctorRegisterNum = doctorRegisterNum;
        this.doctorNoRegisterNum = doctorNoRegisterNum;
        this.nursingStaffNum = nursingStaffNum;
    }

}
