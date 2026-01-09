package com.caring.sass.wx.dto.template;

import com.caring.sass.wx.entity.template.TemplateMsgFields;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName TemplateMsgDto
 * @Description
 * @Author yangShuai
 * @Date 2020/9/16 17:58
 * @Version 1.0
 */
@Data
public class TemplateMsgDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 对应微信公众平台中的模板Id
     */
    @ApiModelProperty(value = "对应微信公众平台中的模板Id")
    private String templateId;
    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    private String businessId;
    /**
     * 模板json
     */
    @ApiModelProperty(value = "模板json")
    private String extraJson;

    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
    private String indefiner;
    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    private String title;
    /**
     * 原模板消息Id
     */
    @ApiModelProperty(value = "原模板消息Id")
    private String sourceId;


    @ApiModelProperty(value = "模板消息字段详细")
    private List<TemplateMsgFields> fields;

    @ApiModelProperty(value = "此模板是否是类目模板-服务工单提醒")
    private Boolean commonCategory = false;

}
