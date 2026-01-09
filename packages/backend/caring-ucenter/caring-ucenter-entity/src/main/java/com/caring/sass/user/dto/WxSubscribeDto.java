package com.caring.sass.user.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Locale;

/**
 * @ClassName WxSubscribeDto
 * @Description 接收 weixin 服务 发出的 用户关注消息
 * @Author yangShuai
 * @Date 2020/9/27 9:47
 * @Version 1.0
 */
@Data
public class WxSubscribeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "openId不能为空")
    private String openId;

    private Long doctorId;
    /**
     * 邀请人ID（患者）
     */
    private Long inviterPatientId;

    private String wxAppId;

    /**
     * 用户关注时 unionId 是不存在的
     */
    private String unionId;
    /**
     * 消息的创建时间
     */
    private Long createTime;
    // 默认语言是中文
    Locale locale = Locale.SIMPLIFIED_CHINESE;

}
