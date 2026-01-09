package com.caring.sass.nursing.dto.follow;

import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("功能配置的列表数据模型")
public class FunctionConfigurationListDTO {

    @ApiModelProperty("计划的ID")
    private Long planId;

    @ApiModelProperty("计划类型")
    private Integer planType;

    @ApiModelProperty("功能名称")
    private String functionName;

    @ApiModelProperty("功能类型")
    private PlanFunctionTypeEnum functionType;

    @ApiModelProperty("是否选择微信模板")
    private Integer hasWeiXinTemplate;

    @ApiModelProperty("微信模板的ID")
    private String weiXinTemplateId;

    @ApiModelProperty("计划的状态 1, 开启， 0关闭")
    private Integer planStatus;

    @ApiModelProperty("是否有推送配置")
    private Integer hasPushConfig;

    @ApiModelProperty("是否有功能配置")
    private Integer hasFunctionConfig;


}
