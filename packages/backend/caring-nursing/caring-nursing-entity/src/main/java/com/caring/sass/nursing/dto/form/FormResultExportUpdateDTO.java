package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-13
 */
@Data
@ApiModel(value = "FormResultExportUpdateDTO", description = "表单结果导出记录表")
public class FormResultExportUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

}
