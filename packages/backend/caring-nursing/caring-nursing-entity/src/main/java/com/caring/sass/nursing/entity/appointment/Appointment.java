package com.caring.sass.nursing.entity.appointment;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 患者预约表
 * </p>
 *
 * @author leizhi
 * @since 2021-01-27
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Builder
@TableName("a_appointment")
@ApiModel(value = "Appointment", description = "患者预约表")
@AllArgsConstructor
public class Appointment extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    @TableField("organization_id")
    @Excel(name = "机构id")
    private Long organizationId;

    @ApiModelProperty(value = "小组id")
    @TableField("group_id")
    @Excel(name = "小组id")
    @Deprecated
    private Long groupId;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    @TableField("patient_id")
    @Excel(name = "患者id")
    private Long patientId;

    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    @Length(max = 100, message = "患者名称长度不能超过100")
    @TableField(value = "patient_name", condition = LIKE)
    @Excel(name = "患者名称")
    private String patientName;

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期")
    @TableField("appoint_date")
    @Excel(name = "预约日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate appointDate;

    /**
     * 就诊状态  未就诊：0  已就诊：1,  取消就诊 2，审核中 -1，(预约失败)审核失败 3， 就诊过期：4
     * {@link com.caring.sass.nursing.constant.AppointmentStatusEnum}
     */
    @ApiModelProperty(value = "就诊状态  未就诊：0  已就诊：1,  取消就诊 2，审核中 -2，(预约失败)审核失败 3， 就诊过期：4")
    @TableField("status")
    private Integer status;

    /**
     * 1 上午  2：下午
     */
    @ApiModelProperty(value = "1 上午  2：下午")
    @TableField("time")
    @Excel(name = "1 上午  2：下午")
    private Integer time;

    /**
     * 医生id
     */
    @ApiModelProperty(value = "医生id")
    @TableField("doctor_id")
    @Excel(name = "医生id")
    private Long doctorId;

    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    @Length(max = 255, message = "医生名称长度不能超过255")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "医生名称")
    private String doctorName;


    /**
     * 预约记录排序的序号
     *  待就诊 （预约成功状态）
     *  审核中 （已开启预约审核功能的，有一个审核中的状态）
     *  预约失败 （医生拒绝和医生未处理的情况下，显示预约失败状态）
     *  已就诊 （医生端已对该患者点击“签到”按钮）
     *  已取消 （患者主动取消后该条记录还在，状态变更为已取消，医生端则不显示，当前号源释放）
     *  已过期 （已就诊的 超过预约时间未及时就诊的显示已过期）
     *  {@link com.caring.sass.nursing.constant.AppointmentSortEnum}
     */
    @ApiModelProperty(value = "排序序号 待就诊0 ，审核中 1 ，预约失败 2 ，已就诊 3 ，已取消 4 ，已过期 5")
    @TableField("appointment_sort")
    private Integer appointmentSort;

    /**
     * 审核拒绝原因
     */
    @ApiModelProperty(value = "ABOUT_FULL: 该时段已约满， OTHER: 其他, APPROVAL_NOT_PROCESSED ")
    @TableField("audit_fail_reason")
    private String auditFailReason;

    /**
     * 审核拒绝原因说明
     */
    @ApiModelProperty(value = "审核拒绝原因说明")
    @TableField("audit_fail_reason_desc")
    private String auditFailReasonDesc;

    /**
     * 患者删除标记
     */
    @ApiModelProperty(value = "患者删除标记 1：删除, 0: 未删除")
    @TableField(value = "patient_delete_mark",  condition = EQUAL)
    private Integer patientDeleteMark;

    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间")
    @TableField("appointment_date")
    private LocalDateTime appointmentDate;

    @ApiModelProperty(value = "就诊时间")
    @TableField("visit_time")
    private LocalDateTime visitTime;

    @ApiModelProperty(value = "头像")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty(value = "科室名称")
    @TableField(exist = false)
    private String deptartmentName;

    @ApiModelProperty(value = "职称")
    @TableField(exist = false)
    private String title;

    @ApiModelProperty(value = "医助的备注")
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty(value = "患者的医生iD")
    @TableField(exist = false)
    private Long patientDoctorId;

    @ApiModelProperty(value = "医生的备注")
    @TableField(exist = false)
    private String doctorRemark;

    @ApiModelProperty(value = "患者性别，0男，1女")
    @TableField(exist = false)
    private Integer sex;

    @ApiModelProperty(value = "患者出生年月")
    @TableField(exist = false)
    private String birthday;

    @ApiModelProperty(value = "剩余可预约人数")
    @TableField(exist = false)
    private Integer remainingNumber;

    @ApiModelProperty(value = "关闭预约(1 为关闭，0 或者 null 为开启 )")
    @TableField(exist = false)
    private Integer closeAppoint;


}
