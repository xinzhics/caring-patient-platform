package com.caring.sass.nursing.dto.drugs;

import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName DrugsArrange
 * @Description
 * @Author yangShuai
 * @Date 2022/1/12 10:01
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DrugsArrangeVo", description = "用药安排")
public class DrugsArrangeVo implements Comparable<DrugsArrangeVo>{

    @ApiModelProperty(value = "用药时间")
    private LocalDateTime drugsTime;

    @ApiModelProperty(value = "状态( 1: 全部打卡, 2: 未打卡或部分打卡，)")
    private Integer status;

    @ApiModelProperty(value = "用药记录")
    private List<PatientDrugsTime> patientDrugsTimes;


    @Override
    public int compareTo(DrugsArrangeVo o) {
        boolean after = this.drugsTime.isAfter(o.getDrugsTime());
        if (after) {
            return 1;
        } else {
            return -1;
        }
    }
}
