package com.caring.sass.sms.dto;

import lombok.Data;

/**
 * @ClassName SmsDto
 * @Description
 * @Author yangShuai
 * @Date 2020/10/13 14:43
 * @Version 1.0
 */
@Data
public class SmsDto {

    private String phone;

    private String content;

    private Boolean resetPassword;
}
