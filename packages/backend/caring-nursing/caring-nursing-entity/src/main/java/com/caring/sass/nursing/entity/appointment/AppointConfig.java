package com.caring.sass.nursing.entity.appointment;

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
 * 记录医生的周预约配置
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
@TableName("a_appoint_config")
@ApiModel(value = "AppointConfig", description = "记录医生的周预约配置")
@AllArgsConstructor
public class AppointConfig extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    @TableField("organization_id")
    @Excel(name = "机构id")
    private Long organizationId;

    /**
     * 医生id
     */
    @ApiModelProperty(value = "医生id")
    @TableField("doctor_id")
    @Excel(name = "医生id")
    private Long doctorId;

    /**
     * 星期一上午人数
     */
    @ApiModelProperty(value = "星期一上午人数")
    @TableField("num_of_monday_morning")
    @Excel(name = "星期一上午人数")
    private Integer numOfMondayMorning;

    /**
     * 星期一下午人数
     */
    @ApiModelProperty(value = "星期一下午人数")
    @TableField("num_of_monday_afternoon")
    @Excel(name = "星期一下午人数")
    private Integer numOfMondayAfternoon;

    /**
     * 星期二上午人数
     */
    @ApiModelProperty(value = "星期二上午人数")
    @TableField("num_of_tuesday_morning")
    @Excel(name = "星期二上午人数")
    private Integer numOfTuesdayMorning;

    /**
     * 星期二下午人数
     */
    @ApiModelProperty(value = "星期二下午人数")
    @TableField("num_of_tuesday_afternoon")
    @Excel(name = "星期二下午人数")
    private Integer numOfTuesdayAfternoon;

    /**
     * 星期三上午人数
     */
    @ApiModelProperty(value = "星期三上午人数")
    @TableField("num_of_wednesday_morning")
    @Excel(name = "星期三上午人数")
    private Integer numOfWednesdayMorning;

    /**
     * 星期三下午人数
     */
    @ApiModelProperty(value = "星期三下午人数")
    @TableField("num_of_wednesday_afternoon")
    @Excel(name = "星期三下午人数")
    private Integer numOfWednesdayAfternoon;

    /**
     * 星期四上午人数
     */
    @ApiModelProperty(value = "星期四上午人数")
    @TableField("num_of_thursday_morning")
    @Excel(name = "星期四上午人数")
    private Integer numOfThursdayMorning;

    /**
     * 星期四下午人数
     */
    @ApiModelProperty(value = "星期四下午人数")
    @TableField("num_of_thursday_afternoon")
    @Excel(name = "星期四下午人数")
    private Integer numOfThursdayAfternoon;

    /**
     * 星期五上午人数
     */
    @ApiModelProperty(value = "星期五上午人数")
    @TableField("num_of_friday_morning")
    @Excel(name = "星期五上午人数")
    private Integer numOfFridayMorning;

    /**
     * 星期五下午人数
     */
    @ApiModelProperty(value = "星期五下午人数")
    @TableField("num_of_friday_afternoon")
    @Excel(name = "星期五下午人数")
    private Integer numOfFridayAfternoon;

    /**
     * 星期六上午人数
     */
    @ApiModelProperty(value = "星期六上午人数")
    @TableField("num_of_saturday_morning")
    @Excel(name = "星期六上午人数")
    private Integer numOfSaturdayMorning;

    /**
     * 星期六下午人数
     */
    @ApiModelProperty(value = "星期六下午人数")
    @TableField("num_of_saturday_afternoon")
    @Excel(name = "星期六下午人数")
    private Integer numOfSaturdayAfternoon;

    /**
     * 星期日上午人数
     */
    @ApiModelProperty(value = "星期日上午人数")
    @TableField("num_of_sunday_morning")
    @Excel(name = "星期日上午人数")
    private Integer numOfSundayMorning;

    /**
     * 星期日下午人数
     */
    @ApiModelProperty(value = "星期日下午人数")
    @TableField("num_of_sunday_afternoon")
    @Excel(name = "星期日下午人数")
    private Integer numOfSundayAfternoon;


    @Builder
    public AppointConfig(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long organizationId, Long doctorId, Integer numOfMondayMorning, Integer numOfMondayAfternoon, Integer numOfTuesdayMorning, 
                    Integer numOfTuesdayAfternoon, Integer numOfWednesdayMorning, Integer numOfWednesdayAfternoon, Integer numOfThursdayMorning, Integer numOfThursdayAfternoon, Integer numOfFridayMorning, 
                    Integer numOfFridayAfternoon, Integer numOfSaturdayMorning, Integer numOfSaturdayAfternoon, Integer numOfSundayMorning, Integer numOfSundayAfternoon) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.organizationId = organizationId;
        this.doctorId = doctorId;
        this.numOfMondayMorning = numOfMondayMorning;
        this.numOfMondayAfternoon = numOfMondayAfternoon;
        this.numOfTuesdayMorning = numOfTuesdayMorning;
        this.numOfTuesdayAfternoon = numOfTuesdayAfternoon;
        this.numOfWednesdayMorning = numOfWednesdayMorning;
        this.numOfWednesdayAfternoon = numOfWednesdayAfternoon;
        this.numOfThursdayMorning = numOfThursdayMorning;
        this.numOfThursdayAfternoon = numOfThursdayAfternoon;
        this.numOfFridayMorning = numOfFridayMorning;
        this.numOfFridayAfternoon = numOfFridayAfternoon;
        this.numOfSaturdayMorning = numOfSaturdayMorning;
        this.numOfSaturdayAfternoon = numOfSaturdayAfternoon;
        this.numOfSundayMorning = numOfSundayMorning;
        this.numOfSundayAfternoon = numOfSundayAfternoon;
    }

}
