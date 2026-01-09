package com.caring.sass.cms.dto;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName MassCallBack
 * @Description
 * @Author yangShuai
 * @Date 2021/11/24 14:11
 * @Version 1.0
 */
@Data
public class MassCallBack {

    private String tenantCode;

    private String msgId;

    private String status;

    private Integer totalCount;

    private Integer filterCount;

    private Integer sentCount;

    private Integer errorCount;

    private Map<String, Object> copyrightCheckResult;

    private Map<String, Object> articleUrlResult;



}
