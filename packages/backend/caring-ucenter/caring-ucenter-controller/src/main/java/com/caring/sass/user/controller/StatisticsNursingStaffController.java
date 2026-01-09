package com.caring.sass.user.controller;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.user.dto.StatisticsNursingStaffPageDTO;
import com.caring.sass.user.dto.StatisticsNursingStaffSaveDTO;
import com.caring.sass.user.dto.StatisticsNursingStaffUpdateDTO;
import com.caring.sass.user.entity.StatisticsNursingStaff;
import com.caring.sass.user.service.StatisticsNursingStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 护理医助统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/statisticsNursingStaff")
@Api(value = "StatisticsNursingStaff", tags = "护理医助统计")
public class StatisticsNursingStaffController extends SuperController<StatisticsNursingStaffService, Long, StatisticsNursingStaff, StatisticsNursingStaffPageDTO, StatisticsNursingStaffSaveDTO, StatisticsNursingStaffUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<StatisticsNursingStaff> statisticsNursingStaffList = list.stream().map((map) -> {
            StatisticsNursingStaff statisticsNursingStaff = StatisticsNursingStaff.builder().build();
            //TODO 请在这里完成转换
            return statisticsNursingStaff;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(statisticsNursingStaffList));
    }

    /**
     * 查询所有用户数
     */
    @ApiOperation(value = "查询所有用户数", notes = "查询所有用户数")
    @GetMapping("/queryMemberNum")
    public R<Map<String, Long>> queryMemberNum() {
        Map<String, Long> ret = baseService.queryMemberNum();
        return R.success(ret);
    }

}
