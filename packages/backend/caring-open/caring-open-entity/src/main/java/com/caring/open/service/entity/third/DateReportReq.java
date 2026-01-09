package com.caring.open.service.entity.third;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 数据上传
 *
 * @author xinz
 */
@Data
@ToString
public class DateReportReq implements Serializable {

    public static final long serialVersionUID = 1L;

    private int accountId;
    private String accountName;
    private double glucose;
    private String mealTime;
    private String measTime;
    private int rate;
    private String serial;
    private double spo2;

    private List<Integer> ecg;

    private Integer freq;

}
