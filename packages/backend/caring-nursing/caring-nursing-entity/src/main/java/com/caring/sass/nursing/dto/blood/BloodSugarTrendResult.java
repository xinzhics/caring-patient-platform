package com.caring.sass.nursing.dto.blood;

import java.util.Collection;

public class BloodSugarTrendResult {
    private Collection<String> datas;
    private Collection<Long> xDatas;

    public BloodSugarTrendResult(Collection<String> datas, Collection<Long> xDatas) {
        this.datas = datas;
        this.xDatas = xDatas;
    }

    public Collection<String> getDatas() {
        return this.datas;
    }

    public void setDatas(Collection<String> datas) {
        this.datas = datas;
    }

    public Collection<Long> getxDatas() {
        return this.xDatas;
    }

    public void setxDatas(Collection<Long> xDatas) {
        this.xDatas = xDatas;
    }
}
