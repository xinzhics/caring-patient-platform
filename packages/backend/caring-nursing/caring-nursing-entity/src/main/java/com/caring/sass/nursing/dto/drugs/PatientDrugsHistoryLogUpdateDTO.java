package com.caring.sass.nursing.dto.drugs;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName PatientDrugsHistory
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 11:27
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientDrugsHistoryLogUpdateDTO", description = "患者药箱操作的记录")
public class PatientDrugsHistoryLogUpdateDTO implements Serializable {

}
