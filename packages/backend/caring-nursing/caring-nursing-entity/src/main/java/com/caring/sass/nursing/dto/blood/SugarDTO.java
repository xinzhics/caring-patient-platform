package com.caring.sass.nursing.dto.blood;

import com.caring.sass.nursing.entity.form.SugarFormResult;

import java.util.Date;
import java.util.List;

public class SugarDTO {

    public Date createDate;

    public List<SugarFormResult> sugars;

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<SugarFormResult> getSugars() {
        return this.sugars;
    }

    public void setSugars(List<SugarFormResult> sugars) {
        this.sugars = sugars;
    }
}
