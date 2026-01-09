package com.caring.sass.user.dto;

import com.caring.sass.user.constant.NotificationJumpType;
import com.caring.sass.common.enums.NotificationTarget;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName NotificationSaveDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 11:29
 * @Version 1.0
 */
@Data
public class NotificationSaveDto {

    @ApiModelProperty(value = "群发名称")
    @NotNull(message = "群发名称不能为空")
    @Length(max = 255, message = "群发名称长度不能超过255")
    private String notificationName;

    @NotNull(message = "通知目标不能为空")
    @ApiModelProperty(value = "通知的目标")
    private NotificationTarget notificationTarget;

    @NotNull(message = "跳转类型不能为空")
    @ApiModelProperty(value = "跳转类型")
    private NotificationJumpType notificationJumpType;

    @ApiModelProperty(value = "链接/内容ID/等")
    private String jumpBusinessContent;

    @NotNull(message = "微信模板ID不能为空")
    @ApiModelProperty(value = "微信的模板Id")
    private String templateId;

    @NotNull(message = "模板名称不能为空")
    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "微信模板")
    private List<FieldsDto> fieldsDtoList;

    @Data
    public static class FieldsDto {

        @ApiModelProperty(value = "标签")
        @Length(max = 100, message = "标签长度不能超过100")
        private String label;

        @ApiModelProperty(value = "属性")
        @Length(max = 200, message = "属性长度不能超过200")
        private String attr;

        @ApiModelProperty(value = "属性值")
        @Length(max = 65535, message = "属性值长度不能超过65,535")
        private String value;

        @ApiModelProperty(value = "颜色值")
        @Length(max = 20, message = "颜色值长度不能超过20")
        private String color;

    }

}
