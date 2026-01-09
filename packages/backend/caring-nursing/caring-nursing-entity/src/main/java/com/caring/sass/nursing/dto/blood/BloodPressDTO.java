package com.caring.sass.nursing.dto.blood;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 匹配前段表单格式
 *
 * @author xinzh
 */
@Data
@Accessors(chain = true)
public class BloodPressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String day;

    @JsonIgnore
    private LocalDateTime createTime;
    private String type;
    private Double value;
}
