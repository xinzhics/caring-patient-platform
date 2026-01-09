package com.caring.sass.nursing.dto.drugs;

import java.io.Serializable;

/**
 * @author xinzh
 */
public class CheckInNumVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer totalDays = 0;
    private Integer continuityDays;
    private Integer toDayCheckIn;

    public Integer getTotalDays() {
        return this.totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getContinuityDays() {
        return this.continuityDays;
    }

    public void setContinuityDays(Integer continuityDays) {
        this.continuityDays = continuityDays;
    }

    public void setToDayCheckIn(Integer toDayCheckIn) {
        this.toDayCheckIn = toDayCheckIn;
    }

    public Integer getToDayCheckIn() {
        return this.toDayCheckIn;
    }
}
