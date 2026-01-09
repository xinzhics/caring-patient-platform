package com.caring.sass.wx.dto.config;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 微信配置信息
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConfigPageDTO", description = "微信配置信息")
public class ConfigPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    @Length(max = 200, message = "应用ID长度不能超过200")
    private String appId;
    /**
     * 公众号名字
     */
    @ApiModelProperty(value = "公众号名字")
    @Length(max = 200, message = "公众号名字长度不能超过200")
    private String name;

}
