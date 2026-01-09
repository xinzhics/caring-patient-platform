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
 * 医生的自定义小组患者
 * </p>
 *
 * @author yangshuai
 * @since 2022-08-11
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_doctor_custom_group_patient")
@ApiModel(value = "DoctorCustomGroupPatient", description = "医生的自定义小组患者")
@AllArgsConstructor
public class DoctorCustomGroupPatient extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医生自定义小组ID
     */
    @ApiModelProperty(value = "医生自定义小组ID")
    @TableField("doctor_custom_group_id")
    @Excel(name = "医生自定义小组ID")
    private Long doctorCustomGroupId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;


    @Builder
    public DoctorCustomGroupPatient(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long doctorCustomGroupId, Long patientId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.doctorCustomGroupId = doctorCustomGroupId;
        this.patientId = patientId;
    }

}
