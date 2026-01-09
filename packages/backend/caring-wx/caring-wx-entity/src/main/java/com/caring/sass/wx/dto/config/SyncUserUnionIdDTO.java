package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ConfigAdditionalPageDTO", description = "微信附加信息配置")
public class SyncUserUnionIdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信appId
     */
    private String appId;

    /**
     * 用户openId列表
     */
    private List<String> openIdList;
}
