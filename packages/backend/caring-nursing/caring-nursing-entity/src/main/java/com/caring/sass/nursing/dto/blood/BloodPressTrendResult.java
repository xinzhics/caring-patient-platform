package com.caring.sass.nursing.dto.blood;

import lombok.Data;

import java.util.Collection;

@Data
public class BloodPressTrendResult {
    private Collection<String> highBloodPress;
    private Collection<String> lowBloodPress;
    private Collection<String> heartRate;
    private Collection<String> xDatas;

    public BloodPressTrendResult(Collection<String> highBloodPress, Collection<String> lowBloodPress, Collection<String> heartRate, Collection<String> xDatas) {
        this.highBloodPress = highBloodPress;
        this.lowBloodPress = lowBloodPress;
        this.heartRate = heartRate;
        this.xDatas = xDatas;
    }
}
