package com.caring.sass.ai.dto.ckd;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("趋势图")
public class CkdGfrTrend {


    private List<LocalDateTime> xData;


    private List<Double> yData;


}
