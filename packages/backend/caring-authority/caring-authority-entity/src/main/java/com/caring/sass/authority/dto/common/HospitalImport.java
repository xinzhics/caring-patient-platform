package com.caring.sass.authority.dto.common;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName HospitalImport
 * @Description
 * @Author yangShuai
 * @Date 2022/4/19 10:20
 * @Version 1.0
 */
@Data
@Builder
public class HospitalImport {

    private int row;

    private boolean error;

    private String errorMessage;

    private String hospitalName;

    private String provinceName;

    private String cityName;

    private String address;

    private String level;

}
