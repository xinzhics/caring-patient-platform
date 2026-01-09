package com.caring.sass.cms.dto;

import com.caring.sass.cms.constant.MassMailingMessageStatus;
import com.caring.sass.cms.constant.MediaTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MassMailingPageDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 14:22
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ApiModel(value = "MassMailingPageDto", description = "群发管理")
@AllArgsConstructor
public class MassMailingPageDto {


    /**
     * 定时发送： timing
     * 手动发送： manual
     */
    @ApiModelProperty(value = "发送类型 定时发送： timing、手动发送： manual")
    private String sendType;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private MediaTypeEnum mediaTypeEnum;


    @ApiModelProperty(value = "发送的状态")
    private MassMailingMessageStatus messageStatus;



}
