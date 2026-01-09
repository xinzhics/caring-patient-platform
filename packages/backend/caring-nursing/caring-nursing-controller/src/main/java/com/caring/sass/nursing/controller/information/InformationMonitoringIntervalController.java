package com.caring.sass.nursing.controller.information;


import cn.hutool.core.bean.BeanUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.information.InformationMonitoringIntervalPageDTO;
import com.caring.sass.nursing.dto.information.InformationMonitoringIntervalSaveDTO;
import com.caring.sass.nursing.dto.information.InformationMonitoringIntervalUpdateDTO;
import com.caring.sass.nursing.entity.information.InformationMonitoringInterval;
import com.caring.sass.nursing.service.information.InformationMonitoringIntervalService;
import com.caring.sass.utils.BizAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * 信息完整度区间设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/informationMonitoringInterval")
@Api(value = "InformationMonitoringInterval", tags = "信息完整度区间设置")
//@PreAuth(replace = "informationMonitoringInterval:")
public class InformationMonitoringIntervalController extends SuperController<InformationMonitoringIntervalService, Long, InformationMonitoringInterval, InformationMonitoringIntervalPageDTO, InformationMonitoringIntervalSaveDTO, InformationMonitoringIntervalUpdateDTO> {


    @ApiOperation(value = "查询信息完整度监控指标区间设置")
    @GetMapping("getInformationMonitoringIntervals/{tenantCode}")
    public R<List<InformationMonitoringInterval>> getInformationMonitoringIntervals(@PathVariable String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<InformationMonitoringInterval> lbqWrapper = new LbqWrapper();
        lbqWrapper.orderByAsc(InformationMonitoringInterval::getMinValue);
        List<InformationMonitoringInterval> InformationMonitoringIntervals = baseService.list(lbqWrapper);
        return R.success(InformationMonitoringIntervals);
    }


    /**
     * 信息完整度监控指标新增|更新 （存在更新记录，否插入一条记录）
     *
     * @return 实体
     */
    @ApiOperation(value = "信息完整度监控指标区间设置新增|更新 （存在更新记录，否插入一条记录）")
    @PostMapping(value = "saveOrUpdate")
    public R<InformationMonitoringInterval> saveOrUpdate(@RequestBody InformationMonitoringIntervalSaveDTO model) {
        BaseContextHandler.setTenant(model.getTenantCode());
        BizAssert.notEmpty(model.getName(), "区间名称不能为空");
        BizAssert.notNull(model.getMinValue(), "最小值不能为空");
        BizAssert.notNull(model.getMaxValue(), "最大值不能为空");
        InformationMonitoringInterval regGuide = BeanUtil.toBean(model, InformationMonitoringInterval.class);
        baseService.saveOrUpdate(regGuide);
        return R.success(regGuide);
    }
    @ApiOperation("删除信息完整度监控指标区间设置通过ID")
    @DeleteMapping("/delete/{tenantCode}/{id}")
    public R<Boolean> deleteById( @PathVariable("tenantCode") String tenantCode,@PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        baseService.removeByIds(ids);
        return R.success();
    }


    @ApiOperation("医助查询区间设置，并统计区间下人数")
    @GetMapping("/nursingIntervalStatistics/{nursingId}")
    public R<List<InformationMonitoringInterval>> nursingIntervalStatistics(@PathVariable("nursingId") Long nursingId) {

        List<InformationMonitoringInterval> intervalList = baseService.nursingIntervalStatistics(nursingId);
        return R.success(intervalList);

    }


    @Override
    public R<List<InformationMonitoringInterval>> query(InformationMonitoringInterval data) {
        LbqWrapper<InformationMonitoringInterval> lbqWrapper = new LbqWrapper();
        lbqWrapper.orderByAsc(InformationMonitoringInterval::getMinValue);
        List<InformationMonitoringInterval> InformationMonitoringIntervals = baseService.list(lbqWrapper);
        return R.success(InformationMonitoringIntervals);
    }
}
