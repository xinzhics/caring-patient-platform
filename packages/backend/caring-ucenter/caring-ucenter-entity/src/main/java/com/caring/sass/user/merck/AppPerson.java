package com.caring.sass.user.merck;

import lombok.Data;

/**
 * @author james
 */
@Data
public class AppPerson extends BasePersonMerck {

    private String appId;

    private String username;

    private String password;

    private String oldPassword;

    private Integer adminLogin;

    private String mobile;

    private String newPassword;

}
