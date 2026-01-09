package com.caring.sass.msgs.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xinzh
 */
@Getter
@Setter
@Accessors(chain = true)
public class IMConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orgName;
    private String appName;
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String userPassword;
    private String accessToken;
}
