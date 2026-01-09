package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者用药的时间设置
 * </p>
 *
 * @author 杨帅
 * @since 2022-08-19
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_patient_drugs_time_setting")
@ApiModel(value = "PatientDrugsTimeSetting", description = "患者用药的时间设置")
@AllArgsConstructor
public class PatientDrugsTimeSetting extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者药品的记录id
     */
    @ApiModelProperty(value = "患者药品的记录id")
    @TableField("patient_drugs_id")
    @Excel(name = "患者药品的记录id")
    private Long patientDrugsId;

    /**
     * 第几次(1-9)
     */
    @ApiModelProperty(value = "第几次(1-9)")
    @TableField("the_first_time")
    @Excel(name = "第几次(1-9)")
    private Integer theFirstTime;

    /**
     * 周期内第几天
     */
    @ApiModelProperty(value = "周期内第几天")
    @TableField("day_of_the_cycle")
    @Excel(name = "周期内第几天")
    private Integer dayOfTheCycle;

    /**
     * 当天触发的时间(N点00分，N点15分，N点30分，N点45分)
     */
    @ApiModelProperty(value = "当天触发的时间(N点00分，N点15分，N点30分，N点45分)，N小时1次需要传开始的小时时间")
    @TableField("trigger_time_of_the_day")
    @Excel(name = "当天触发的时间(N点00分，N点15分，N点30分，N点45分)", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalTime triggerTimeOfTheDay;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_Id")
    @Excel(name = "患者ID")
    private Long patientId;


    @Builder
    public PatientDrugsTimeSetting(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long patientDrugsId, Integer theFirstTime, Integer dayOfTheCycle, LocalTime triggerTimeOfTheDay, Long patientId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.patientDrugsId = patientDrugsId;
        this.theFirstTime = theFirstTime;
        this.dayOfTheCycle = dayOfTheCycle;
        this.triggerTimeOfTheDay = triggerTimeOfTheDay;
        this.patientId = patientId;
    }

}
