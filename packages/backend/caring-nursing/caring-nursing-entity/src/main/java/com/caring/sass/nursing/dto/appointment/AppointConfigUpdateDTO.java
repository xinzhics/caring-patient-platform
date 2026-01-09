package com.caring.sass.nursing.dto.appointment;

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
 * 记录医生的周预约配置
 * </p>
 *
 * @author leizhi
 * @since 2021-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AppointConfigUpdateDTO", description = "记录医生的周预约配置")
public class AppointConfigUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 星期一上午人数
     */
    @ApiModelProperty(value = "星期一上午人数")
    private Integer numOfMondayMorning;
    /**
     * 星期一下午人数
     */
    @ApiModelProperty(value = "星期一下午人数")
    private Integer numOfMondayAfternoon;
    /**
     * 星期二上午人数
     */
    @ApiModelProperty(value = "星期二上午人数")
    private Integer numOfTuesdayMorning;
    /**
     * 星期二下午人数
     */
    @ApiModelProperty(value = "星期二下午人数")
    private Integer numOfTuesdayAfternoon;
    /**
     * 星期三上午人数
     */
    @ApiModelProperty(value = "星期三上午人数")
    private Integer numOfWednesdayMorning;
    /**
     * 星期三下午人数
     */
    @ApiModelProperty(value = "星期三下午人数")
    private Integer numOfWednesdayAfternoon;
    /**
     * 星期四上午人数
     */
    @ApiModelProperty(value = "星期四上午人数")
    private Integer numOfThursdayMorning;
    /**
     * 星期四下午人数
     */
    @ApiModelProperty(value = "星期四下午人数")
    private Integer numOfThursdayAfternoon;
    /**
     * 星期五上午人数
     */
    @ApiModelProperty(value = "星期五上午人数")
    private Integer numOfFridayMorning;
    /**
     * 星期五下午人数
     */
    @ApiModelProperty(value = "星期五下午人数")
    private Integer numOfFridayAfternoon;
    /**
     * 星期六上午人数
     */
    @ApiModelProperty(value = "星期六上午人数")
    private Integer numOfSaturdayMorning;
    /**
     * 星期六下午人数
     */
    @ApiModelProperty(value = "星期六下午人数")
    private Integer numOfSaturdayAfternoon;
    /**
     * 星期日上午人数
     */
    @ApiModelProperty(value = "星期日上午人数")
    private Integer numOfSundayMorning;
    /**
     * 星期日下午人数
     */
    @ApiModelProperty(value = "星期日下午人数")
    private Integer numOfSundayAfternoon;
}
