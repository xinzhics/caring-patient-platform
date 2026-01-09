package com.caring.sass.wx.dto.config;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


/**
 * @author xinzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PublishMenuDTO", description = "发布微信菜单DTO")
public class PublishMenuDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "appId不能为空")
    @ApiModelProperty(value = "appID")
    private String appId;

    @ApiModelProperty(value = "菜单json", reference = "https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Getting_Custom_Menu_Configurations.html")
    private String wxMenuJson;
}
