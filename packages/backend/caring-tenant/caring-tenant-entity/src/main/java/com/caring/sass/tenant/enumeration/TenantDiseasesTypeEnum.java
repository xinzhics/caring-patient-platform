package com.caring.sass.tenant.enumeration;

import lombok.Getter;

@Getter
public enum TenantDiseasesTypeEnum {

    other("其他项目"),

    allergic("过敏项目")

    ;

    private String name;

    TenantDiseasesTypeEnum(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
