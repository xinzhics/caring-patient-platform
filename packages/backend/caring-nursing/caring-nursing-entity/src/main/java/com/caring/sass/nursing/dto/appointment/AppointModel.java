package com.caring.sass.nursing.dto.appointment;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.nursing.constant.AppointmentStatusEnum;
import com.caring.sass.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName AppointModel
 * @Description 患者查看医生预约排次。 每周剩余号源。
 * @Author yangShuai
 * @Date 2021/1/28 11:39
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("医生号源情况")
public class AppointModel {

    @ApiModelProperty("日期说明")
    private String title;

    @ApiModelProperty("日期")
    private Date date;

    @ApiModelProperty("号源状态 0 约满, 1 有号, 2 未设置 ")
    private Integer status;

    @ApiModelProperty("上午剩余号源")
    private Integer morning;

    @ApiModelProperty("下午剩余号源")
    private Integer afternoon;

    @ApiModelProperty("我是否约了上午 -1 没约; 0 约了未就诊;  1约了已就诊; -2 约了待审核")
    private Integer myMorningAppoint;

    @ApiModelProperty("我是否约了下午 -1 没约; 0 约了未就诊;  1约了已就诊; -2 约了待审核")
    private Integer myAfterAppoint;

    public void setMorning(Integer morning) {
        if (morning != null && morning < 0) {
            this.morning = 0;
        } else {
            this.morning = morning;
        }
    }

    public void setAfternoon(Integer afternoon) {
        if (afternoon != null && afternoon < 0) {
            this.afternoon = 0;
        } else {
            this.afternoon = afternoon;
        }
    }

    /**
     * @Author yangShuai
     * @Description 根据医生配置情况和 预约剩余情况。计算状态
     * @Date 2021/1/28 14:09
     *
     * @param numOfSundayMorning
     * @param numOfSundayAfternoon
     * @return java.lang.Integer
     */
    public Integer calculationStatus(Integer numOfSundayMorning, Integer numOfSundayAfternoon) {
        if (numOfSundayMorning == 0 && numOfSundayAfternoon == 0) {
            return 2;
        } else if (this.morning == 0 && this.afternoon == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * @Author yangShuai
     * @Description 计算预约资格
     * @Date 2021/2/25 16:27
     *
     * @param appointModel
     * @param map
     * @return void
     */
    public void calculationQualifications(AppointModel appointModel, Map<String, Integer> map) {
        if (CollectionUtils.isEmpty(map) ) {
            appointModel.myAfterAppoint = -1;
            appointModel.myMorningAppoint = -1;
        }
        String format = DateUtils.format(appointModel.getDate(), DateUtils.DEFAULT_DATE_FORMAT);
        Integer time1 = map.get(format + 1);
        Integer time2 = map.get(format + 2);
        appointModel.myMorningAppoint = time1 == null ? -1 : time1;
        appointModel.myAfterAppoint = time2 == null ? -1 : time2;
    }
}
