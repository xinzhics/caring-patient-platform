package com.caring.sass.authority.dto.auth;

import lombok.Builder;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
@Builder
public class DoctorToken {

    private Long doctorId;

    private String clientId;

    private String token;
}
