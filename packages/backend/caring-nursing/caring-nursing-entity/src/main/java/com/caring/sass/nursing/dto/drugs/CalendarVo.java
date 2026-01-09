package com.caring.sass.nursing.dto.drugs;

import java.io.Serializable;
import java.util.List;

import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import lombok.Data;

/**
 * @author xinzh
 */
@Data
public class CalendarVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PatientDayDrugs> calendar;

    private CheckInNumVo checkInNumVo;
}
