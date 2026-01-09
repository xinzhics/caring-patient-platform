package com.caring.sass.user.entity;

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
 * 护理医助统计
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
@TableName("s_statistics_nursing_staff")
@ApiModel(value = "StatisticsNursingStaff", description = "护理医助统计")
@AllArgsConstructor
public class StatisticsNursingStaff extends Entity<Long> {

    private static final long serialVersionUID = 1L;

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
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    @TableField("nursing_staff_id")
    @Excel(name = "医助ID")
    private Long nursingStaffId;

    /**
     * 医助姓名
     */
    @ApiModelProperty(value = "医助姓名")
    @Length(max = 32, message = "医助姓名长度不能超过32")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "医助姓名")
    private String name;

    /**
     * 医助头像
     */
    @ApiModelProperty(value = "医助头像")
    @Length(max = 250, message = "医助头像长度不能超过250")
    @Excel(name = "医助头像")
    private String avatar;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构")
    @Length(max = 50, message = "所属机构长度不能超过50")
    @TableField(value = "organ_name", condition = LIKE)
    @Excel(name = "所属机构")
    private String organName;

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
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

    @Builder
    public StatisticsNursingStaff(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                    Integer patientNum, Integer patientRegisterNum, Integer patientNoRegisterNum, Long nursingStaffId, String name,
                    String avatar, String organName, Integer doctorNum, Integer doctorRegisterNum, Integer doctorNoRegisterNum, String tenantCode) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientNum = patientNum;
        this.patientRegisterNum = patientRegisterNum;
        this.patientNoRegisterNum = patientNoRegisterNum;
        this.nursingStaffId = nursingStaffId;
        this.name = name;
        this.avatar = avatar;
        this.organName = organName;
        this.doctorNum = doctorNum;
        this.doctorRegisterNum = doctorRegisterNum;
        this.doctorNoRegisterNum = doctorNoRegisterNum;
        this.tenantCode = tenantCode;
    }

}
