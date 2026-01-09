package com.caring.sass.nursing.controller.drugs;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.drugs.DrugsConditionMonitoringPageDTO;
import com.caring.sass.nursing.dto.drugs.DrugsConditionMonitoringSaveDTO;
import com.caring.sass.nursing.dto.drugs.DrugsConditionMonitoringUpdateDTO;
import com.caring.sass.nursing.dto.form.IndicatorsConditionMonitoringSaveDTO;
import com.caring.sass.nursing.entity.drugs.DrugsConditionMonitoring;
import com.caring.sass.nursing.entity.form.IndicatorsConditionMonitoring;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.service.drugs.DrugsConditionMonitoringService;
import com.caring.sass.nursing.vo.DrugsConditionMonitoringVO;
import com.caring.sass.nursing.vo.IndicatorsConditionMonitoringVO;
import com.caring.sass.security.annotation.PreAuth;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.utils.BizAssert;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 患者管理-用药预警-预警条件表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/drugsConditionMonitoring")
@Api(value = "DrugsConditionMonitoring", tags = "患者管理-用药预警-预警条件表")
@PreAuth(replace = "drugsConditionMonitoring:")
public class DrugsConditionMonitoringController extends SuperController<DrugsConditionMonitoringService, Long, DrugsConditionMonitoring, DrugsConditionMonitoringPageDTO, DrugsConditionMonitoringSaveDTO, DrugsConditionMonitoringUpdateDTO> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private  TenantApi tenantApi;
    @Autowired
    private  DrugsConditionMonitoringService drugsConditionMonitoringService;


    @ApiOperationSupport(author = "李祥", order = 1)
    @ApiOperation(value = "查询患者管理-用药预警-预警药品及条件")
    @GetMapping("getDrugsConditionMonitoring/{tenantCode}")
    public R<List<DrugsConditionMonitoringVO>> getDrugsConditionMonitoring(@PathVariable String tenantCode, @ApiParam(value = "药品ID",required = false)  @RequestParam(value = "drugsId",required = false) Long drugsId) {
        List<DrugsConditionMonitoringVO> results = baseService.getDrugsConditionMonitoring(tenantCode,drugsId);
        return R.success(results);
    }

    @ApiOperationSupport(author = "李祥", order = 2)
    @ApiOperation(value = "患者管理-用药预警-预警药品及条件新增|更新 （存在更新记录，否插入一条记录）")
    @PostMapping(value = "saveOrUpdate")
    public R<DrugsConditionMonitoring> saveOrUpdate(@RequestBody @Valid DrugsConditionMonitoringSaveDTO model) {
        BaseContextHandler.setTenant(model.getTenantCode());
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch()+":"+model.getTenantCode());
        if (!StringUtils.isEmpty(value) && value.equals(MonitoringTaskType.RUNING.operateType)){
            BizAssert.notNull(null, "存在执行任务中！禁止进行预警药品及条件调整！");
        }
        LbqWrapper<DrugsConditionMonitoring> lbqWrapper = new LbqWrapper();
        lbqWrapper.eq(DrugsConditionMonitoring::getDrugsId,model.getDrugsId());
        if (baseService.count(lbqWrapper)>0 && model.getId()==null){
            BizAssert.notNull(null, "当前药品已添加过用药预警，请勿重复添加！");
        }
        DrugsConditionMonitoring regGuide = BeanUtil.toBean(model, DrugsConditionMonitoring.class);
        baseService.saveOrUpdate(regGuide);
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch() + ":" + model.getTenantCode(), MonitoringTaskType.WAIT.operateType);
        return R.success(regGuide);
    }

    @ApiOperationSupport(author = "李祥", order = 3)
    @ApiOperation("删除患者管理-用药预警-预警药品及条件通过ID")
    @DeleteMapping("/delete/{tenantCode}/{id}")
    public R<Boolean> deleteById( @PathVariable("tenantCode") String tenantCode, @PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        baseService.removeByIds(ids);
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch() + ":" + tenantCode, MonitoringTaskType.WAIT.operateType);
        return R.success();
    }

    @ApiOperationSupport(author = "李祥", order = 4)
    @ApiOperation(value = "查询患者管理-用药预警-预警药品及条件-立即生效-任务状态  1|2|3  待执行任务|执行任务中|已执行任务")
    @GetMapping("getTenantCodeIndicatorsConditionSwitch/{tenantCode}")
    public R<String> getTenantCodeIndicatorsConditionSwitch(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch()+":"+tenantCode);
        if (Objects.nonNull(value)) {
            return R.success(value);
        }
        return R.success(MonitoringTaskType.WAIT.operateType);
    }

    @ApiOperationSupport(author = "李祥", order = 5)
    @ApiOperation(value = "患者管理-用药预警-预警药品及条件-立即生效")
    @GetMapping(value = "DrugsConditionMonitoringTask/{tenantCode}")
    public R<Boolean> DrugsConditionMonitoringTask(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantCodeDrugsConditionSwitch()+":"+tenantCode);
        if (!StringUtils.isEmpty(value) && value.equals(MonitoringTaskType.RUNING.operateType)){
            BizAssert.notNull(null, "存在执行任务中！禁止进行立即生效！");
        }
        return R.success(baseService.synDrugsConditionMonitoringTask(tenantCode,true));
    }


    @ApiOperationSupport(author = "杨帅", order = 6)
    @ApiOperation(value = "查询用药预警-购药信息")
    @GetMapping("getDrugsConditionMonitoring")
    public R<DrugsConditionMonitoringVO> getDrugsConditionMonitoring(@RequestParam(value = "drugsId") Long drugsId) {
        List<DrugsConditionMonitoringVO> results = baseService.getDrugsConditionMonitoring(BaseContextHandler.getTenant(),drugsId);
        if (CollUtil.isNotEmpty(results)) {
            return R.success(results.get(0));
        }
        return R.success(null);
    }
}
