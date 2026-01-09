package com.caring.sass.user.dto;

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
@ApiModel(value = "KeywordProjectSettingsSaveDTO", description = "项目关键字开关配置")
public class KeywordProjectSettingsSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 快捷回复开关
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
