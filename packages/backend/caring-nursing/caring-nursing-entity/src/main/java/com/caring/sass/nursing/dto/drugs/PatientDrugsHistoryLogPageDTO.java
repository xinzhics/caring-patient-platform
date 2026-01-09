package com.caring.sass.nursing.dto.drugs;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName PatientDrugsHistory
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 11:27
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientDrugsHistoryLogPageDTO", description = "患者药箱操作的记录")
public class PatientDrugsHistoryLogPageDTO implements Serializable {


    @ApiModelProperty(value = "患者ID")
    @NotNull
    @TableField(value = "patient_id", condition = EQUAL)
    private Long patientId;


}
