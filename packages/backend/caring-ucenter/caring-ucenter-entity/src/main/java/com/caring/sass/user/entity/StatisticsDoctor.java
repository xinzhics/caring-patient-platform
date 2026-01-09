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
 * 医生统计
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
@TableName("s_statistics_doctor")
@ApiModel(value = "StatisticsDoctor", description = "医生统计")
@AllArgsConstructor
public class StatisticsDoctor extends Entity<Long> {

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
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    @Length(max = 32, message = "医生姓名长度不能超过32")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "医生姓名")
    private String name;

    /**
     * 医生头像
     */
    @ApiModelProperty(value = "医生头像")
    @Length(max = 250, message = "医生头像长度不能超过250")
    @TableField(value = "avatar", condition = LIKE)
    @Excel(name = "医生头像")
    private String avatar;

    /**
     * 护理医助
     */
    @ApiModelProperty(value = "医助")
    @Length(max = 100, message = "医助长度不能超过100")
    @TableField(value = "nursing_name", condition = LIKE)
    @Excel(name = "医助")
    private String nursingName;

    /**
     * 所属医院
     */
    @ApiModelProperty(value = "所属医院")
    @Length(max = 50, message = "所属医院长度不能超过50")
    @TableField(value = "hospital_name", condition = LIKE)
    @Excel(name = "所属医院")
    private String hospitalName;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构")
    @Length(max = 50, message = "所属机构长度不能超过50")
    @TableField(value = "organ_name", condition = LIKE)
    @Excel(name = "所属机构")
    private String organName;

    /**
     * 状态：1已注册（已登录） 2未注册（未登录）
     */
    @ApiModelProperty(value = "状态：1已注册（已登录） 2未注册（未登录）")
    @TableField("doctor_status")
    @Excel(name = "状态：1已注册（已登录） 2未注册（未登录）")
    private Integer doctorStatus;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

    @Builder
    public StatisticsDoctor(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                    Integer patientNum, Integer patientRegisterNum, Integer patientNoRegisterNum, Long doctorId, String name,
                    String avatar, String nursingName, String hospitalName, String organName, Integer doctorStatus, String tenantCode) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientNum = patientNum;
        this.patientRegisterNum = patientRegisterNum;
        this.patientNoRegisterNum = patientNoRegisterNum;
        this.doctorId = doctorId;
        this.name = name;
        this.avatar = avatar;
        this.nursingName = nursingName;
        this.hospitalName = hospitalName;
        this.organName = organName;
        this.doctorStatus = doctorStatus;
        this.tenantCode = tenantCode;
    }

}
