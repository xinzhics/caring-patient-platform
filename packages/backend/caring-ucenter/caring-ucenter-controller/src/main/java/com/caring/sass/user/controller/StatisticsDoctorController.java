package com.caring.sass.user.controller;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.user.dto.StatisticsDoctorPageDTO;
import com.caring.sass.user.dto.StatisticsDoctorSaveDTO;
import com.caring.sass.user.dto.StatisticsDoctorUpdateDTO;
import com.caring.sass.user.entity.StatisticsDoctor;
import com.caring.sass.user.service.StatisticsDoctorService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 医生统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/statisticsDoctor")
@Api(value = "StatisticsDoctor", tags = "医生统计")
public class StatisticsDoctorController extends SuperController<StatisticsDoctorService, Long, StatisticsDoctor, StatisticsDoctorPageDTO, StatisticsDoctorSaveDTO, StatisticsDoctorUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<StatisticsDoctor> statisticsDoctorList = list.stream().map((map) -> {
            StatisticsDoctor statisticsDoctor = StatisticsDoctor.builder().build();
            //TODO 请在这里完成转换
            return statisticsDoctor;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(statisticsDoctorList));
    }
}
