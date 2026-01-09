package com.caring.sass.nursing.controller.form;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.bean.BeanUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.form.IndicatorsConditionMonitoringPageDTO;
import com.caring.sass.nursing.dto.form.IndicatorsConditionMonitoringSaveDTO;
import com.caring.sass.nursing.dto.form.IndicatorsConditionMonitoringUpdateDTO;
import com.caring.sass.nursing.entity.form.IndicatorsConditionMonitoring;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.service.form.IndicatorsConditionMonitoringService;
import com.caring.sass.nursing.vo.IndicatorsConditionMonitoringVO;
import com.caring.sass.nursing.vo.IndicatorsPlanVo;
import com.caring.sass.utils.BizAssert;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;
import javax.validation.Valid;
/**
 * <p>
 * 前端控制器
 * 患者管理-监测数据-监控指标条件表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/indicatorsConditionMonitoring")
@Api(value = "IndicatorsConditionMonitoring", tags = "患者管理-监测数据-监控指标条件管理")
@PreAuth(replace = "indicatorsConditionMonitoring:")
public class IndicatorsConditionMonitoringController extends SuperController<IndicatorsConditionMonitoringService, Long, IndicatorsConditionMonitoring, IndicatorsConditionMonitoringPageDTO, IndicatorsConditionMonitoringSaveDTO, IndicatorsConditionMonitoringUpdateDTO> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperationSupport(author = "李祥", order = 1)
    @ApiOperation(value = "查询患者管理-监测数据-监测计划")
    @GetMapping("getIndicatorsPlan/{tenantCode}")
    public R<List<IndicatorsPlanVo>> getIndicatorsPlan(@PathVariable String tenantCode) {
        List<IndicatorsPlanVo> results = baseService.getIndicatorsPlan(tenantCode);
        return R.success(results);
    }

    @ApiOperationSupport(author = "李祥", order = 2)
    @ApiOperation(value = "查询患者管理-监测数据-监测计划-监测指标及条件")
    @GetMapping("getIndicatorsConditionMonitoring/{tenantCode}")
    public R<List<IndicatorsConditionMonitoringVO>> getIndicatorsConditionMonitoring(@PathVariable String tenantCode, @ApiParam(value = "计划ID",required = true)  @RequestParam("planId") Long planId) {
        List<IndicatorsConditionMonitoringVO> results = baseService.getIndicatorsConditionMonitoring(tenantCode,planId);
        return R.success(results);
    }

    @ApiOperationSupport(author = "李祥", order = 3)
    @ApiOperation(value = "患者管理-监测数据-监测计划-监测指标新增|更新 （存在更新记录，否插入一条记录）")
    @PostMapping(value = "saveOrUpdate")
    public R<IndicatorsConditionMonitoring> saveOrUpdate(@RequestBody @Valid IndicatorsConditionMonitoringSaveDTO model) {
        BaseContextHandler.setTenant(model.getTenantCode());
        if ((ObjectUtils.isEmpty(model.getMaxConditionSymbol()) && ObjectUtils.isEmpty(model.getMinConditionSymbol())) || (ObjectUtils.isEmpty(model.getMaxConditionValue()) && ObjectUtils.isEmpty(model.getMinConditionValue())) ){
            BizAssert.notNull(null, "请至少填写一个完整条件！");
        }
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch()+":"+model.getTenantCode());
        if (!StringUtils.isEmpty(value) && value.equals(MonitoringTaskType.RUNING.operateType)){
            BizAssert.notNull(null, "存在执行任务中！禁止进行监测条件调整！");
        }
        LbqWrapper<IndicatorsConditionMonitoring> lbqWrapper = new LbqWrapper();
        lbqWrapper.eq(IndicatorsConditionMonitoring::getPlanId,model.getPlanId());
        lbqWrapper.eq(IndicatorsConditionMonitoring::getFormId,model.getFormId());
        lbqWrapper.eq(IndicatorsConditionMonitoring::getFieldId,model.getFieldId());
        IndicatorsConditionMonitoring regGuide = BeanUtil.toBean(model, IndicatorsConditionMonitoring.class);
        if(regGuide.getId()==null){
            BizAssert.isTrue(baseService.count(lbqWrapper)<2, "最多只能填写两个条件！");
        }
        baseService.saveOrUpdate(regGuide);
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch() + ":" + model.getTenantCode(), MonitoringTaskType.WAIT.operateType);
        return R.success(regGuide);
    }

    @ApiOperationSupport(author = "李祥", order = 4)
    @ApiOperation("删除患者管理-监测数据-监测计划-监测指标通过ID")
    @DeleteMapping("/delete/{tenantCode}/{id}")
    public R<Boolean> deleteById( @PathVariable("tenantCode") String tenantCode, @PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        baseService.removeByIds(ids);
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch() + ":" +tenantCode, MonitoringTaskType.WAIT.operateType);
        return R.success();
    }

    @ApiOperationSupport(author = "李祥", order = 5)
    @ApiOperation(value = "查询患者管理-监测数据-监测计划-监测指标-立即生效-任务状态  1|2|3  待执行任务|执行任务中|已执行任务")
    @GetMapping("getTenantCodeIndicatorsConditionSwitch/{tenantCode}")
    public R<String> getTenantCodeIndicatorsConditionSwitch(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch()+":"+tenantCode);
        if (Objects.nonNull(value)) {
            return R.success(value);
        }
        return R.success(MonitoringTaskType.WAIT.operateType);
    }

    @ApiOperationSupport(author = "李祥", order = 6)
    @ApiOperation(value = "监测数据-监测计划-监测指标 -立即生效")
    @GetMapping(value = "indicatorsConditionMonitoringTask/{tenantCode}")
    public R<Boolean> indicatorsConditionMonitoringTask(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodeIndicatorsConditionSwitch()+":"+tenantCode);
        if (!StringUtils.isEmpty(value) && value.equals(MonitoringTaskType.RUNING.operateType)){
            BizAssert.notNull(null, "存在执行任务中！禁止进行立即生效！");
        }
        return R.success(baseService.synIndicatorsConditionMonitoringTask(tenantCode));
    }
}
