package com.caring.sass.nursing.dto.plan;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName ReminderLog
 * @Description
 * @Author yangShuai
 * @Date 2020/10/27 11:54
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ReminderLogUpdateDTO", description = "推送记录")
public class ReminderLogUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;


}
