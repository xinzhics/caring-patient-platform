package com.caring.sass.nursing.dto.form;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

/**
 * 表单复制数据模型
 *
 * @author xinzh
 */
@Data
@Accessors(chain = true)
public class CopyFormDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String fromTenantCode;

    @NotEmpty
    private String toTenantCode;

    private Map<Long, Long> planIdMaps;


}
