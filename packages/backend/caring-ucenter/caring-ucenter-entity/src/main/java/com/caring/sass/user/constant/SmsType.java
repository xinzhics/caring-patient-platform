package com.caring.sass.user.constant;

public enum SmsType {
    ResetPwd("找回密码", "ResetPwd"),
    Register("账号注册", "Register"),
    Login("登录", "Login");
    private String name;
    private String code;

    SmsType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
