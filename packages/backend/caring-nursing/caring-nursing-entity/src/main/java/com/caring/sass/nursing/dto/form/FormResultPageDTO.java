package com.caring.sass.nursing.dto.form;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.enumeration.FormEnum;
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
 * 表单填写结果表
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
@ApiModel(value = "FormResultPageDTO", description = "表单填写结果表")
public class FormResultPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对微信应消息回执中的Id
     */
    @ApiModelProperty(value = "对微信应消息回执中的Id")
    @Length(max = 32, message = "对微信应消息回执中的Id长度不能超过32")
    private String messageId;
    /**
     * 业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同
     */
    @ApiModelProperty(value = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同")
    @Length(max = 32, message = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同长度不能超过32")
    private String businessId;
    /**
     * 表单Id
     */
    @ApiModelProperty(value = "表单Id")
    private Long formId;
    /**
     * 填写人Id
     */
    @ApiModelProperty(value = "填写人Id")
    private Long userId;

    /**
     * 填写结果数据
     */
    @ApiModelProperty(value = "填写结果数据")
    private String jsonContent;
    /**
     * 对应CustomForm中的此属性
     */
    @ApiModelProperty(value = "对应CustomForm中的此属性")
    @Length(max = 255, message = "对应CustomForm中的此属性长度不能超过255")
    private String name;

    /**
     * 类型
     * #{HEALTH_RECORD=疾病信息;BASE_INFO=基本信息}
     */
    @NotNull(message = "表单类型不能为空")
    @ApiModelProperty(value = "类型")
    private FormEnum category;
}
