package com.caring.sass.nursing.dto.form;

import java.time.LocalDateTime;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * 自定义表单表
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
@ApiModel(value = "FormPageDTO", description = "自定义表单表")
public class FormPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 100, message = "业务Id长度不能超过100")
    private String businessId;
    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    @Length(max = 50, message = "表单名称长度不能超过50")
    private String name;
    /**
     * 表单的数据字典描述
     */
    @ApiModelProperty(value = "表单的数据字典描述")
    private String fieldsJson;
    /**
     * 原护理计划或者detail的Id
     */
    @ApiModelProperty(value = "原护理计划或者detail的Id")
    @Length(max = 32, message = "原护理计划或者detail的Id长度不能超过32")
    private String sourceBusinessId;

    /**
     * 类型
     * #{HEALTH_RECORD=疾病信息;BASE_INFO=基本信息}
     */
    @ApiModelProperty(value = "类型")
    private FormEnum category;

}
