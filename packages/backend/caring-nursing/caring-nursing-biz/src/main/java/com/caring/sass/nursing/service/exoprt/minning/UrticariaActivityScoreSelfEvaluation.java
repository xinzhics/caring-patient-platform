package com.caring.sass.nursing.service.exoprt.minning;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @className: UrticariaActivityScoreSelfEvaluation
 * @author: 杨帅
 * @date: 2023/11/23
 *
 * 荨麻疹活动评分自我评价
 *
 */
@Data
@ApiModel("荨麻疹活动评分自我评价")
public class UrticariaActivityScoreSelfEvaluation {


    @ApiModelProperty("评估日期")
    private LocalDate aaluationDate;

    @ApiModelProperty("评估日期和时间")
    private LocalDateTime aaluationDateTime;

    /**
     * 风团数量
     */
    @ApiModelProperty("风团数量")
    private Integer numberWindMasses;

    /**
     * 瘙痒程度
     */
    @ApiModelProperty("瘙痒程度")
    private Integer itchingDegree;

    @ApiModelProperty("针次")
    private String needleCount;

    /**
     * 总分
     */
    @ApiModelProperty("总分")
    private Integer total;


    public Integer getTotal() {

        if (numberWindMasses != null && itchingDegree != null) {
            total = numberWindMasses + itchingDegree;
        } else if (numberWindMasses != null) {
            total = numberWindMasses;
        } else if (itchingDegree != null) {
            total = itchingDegree;
        }
        return total;
    }
}
