package com.caring.sass.user.redis;

import com.caring.sass.user.constant.KeyWordEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 项目关键字开关配置
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KeywordProjectSettingsRedisDTO", description = "项目关键字开关配置redis存储")
public class KeywordProjectSettingsRedisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 快捷回复开关
     * {@link KeyWordEnum}
     */
    @ApiModelProperty(value = "快捷回复开关功能开关(open, close)")
    @Length(max = 255, message = "快捷回复开关长度不能超过255")
    private String keywordReplySwitch;
    /**
     * 快捷回复形式
     */
    @ApiModelProperty(value = "回复形式(system，medical_assistance 医助)")
    @Length(max = 255, message = "快捷回复形式长度不能超过255")
    private String keywordReplyForm;

}
