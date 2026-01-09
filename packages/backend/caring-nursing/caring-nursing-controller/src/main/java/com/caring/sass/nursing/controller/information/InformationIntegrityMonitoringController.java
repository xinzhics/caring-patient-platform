package com.caring.sass.nursing.controller.information;


import cn.hutool.core.bean.BeanUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.dto.information.InformationIntegrityMonitoringPageDTO;
import com.caring.sass.nursing.dto.information.InformationIntegrityMonitoringSaveDTO;
import com.caring.sass.nursing.dto.information.InformationIntegrityMonitoringTaskDTO;
import com.caring.sass.nursing.dto.information.InformationIntegrityMonitoringUpdateDTO;
import com.caring.sass.nursing.entity.information.InformationIntegrityMonitoring;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.information.InformationIntegrityMonitoringService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.BizAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 信息完整度监控指标
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/informationIntegrityMonitoring")
@Api(value = "InformationIntegrityMonitoring", tags = "信息完整度监控指标")
//@PreAuth(replace = "informationIntegrityMonitoring:")
public class InformationIntegrityMonitoringController extends SuperController<InformationIntegrityMonitoringService, Long, InformationIntegrityMonitoring, InformationIntegrityMonitoringPageDTO, InformationIntegrityMonitoringSaveDTO, InformationIntegrityMonitoringUpdateDTO> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PatientApi patientApi;
    @ApiOperation(value = "查询信息完整度监控指标")
    @GetMapping("getInformationIntegrityMonitorings/{tenantCode}")
    public R<List<InformationIntegrityMonitoring>> getInformationIntegrityMonitorings(@PathVariable String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<InformationIntegrityMonitoring> lbqWrapper = new LbqWrapper();
        lbqWrapper.orderByAsc(InformationIntegrityMonitoring::getMonitorSort);
        List<InformationIntegrityMonitoring> informationIntegrityMonitorings = baseService.list(lbqWrapper);
        return R.success(informationIntegrityMonitorings);
    }
    /**
     * 信息完整度监控指标新增|更新 （存在更新记录，否插入一条记录）
     *
     * @return 实体
     */
    @ApiOperation(value = "信息完整度监控指标新增|更新 （存在更新记录，否插入一条记录）")
    @PostMapping(value = "saveOrUpdate")
    public R<InformationIntegrityMonitoring> saveOrUpdate(@RequestBody InformationIntegrityMonitoringSaveDTO model) {
        BizAssert.notEmpty(model.getTenantCode(), "租户编码不能为空");
        BaseContextHandler.setTenant(model.getTenantCode());
        //监控来源
        BizAssert.notEmpty(model.getBusinessType(), "监控指标来源不能为空");
        if (!FormEnum.BASE_INFO.getDesc().equals(model.getBusinessType()) && !FormEnum.HEALTH_RECORD.getDesc().equals(model.getBusinessType())){
        //计划
            BizAssert.notNull(model.getPlanId(), "计划ID不能为空");
            BizAssert.notNull(model.getPlanName(), "计划名称不能为空");
        }
        //表单
        BizAssert.notNull(model.getFormId(), "表单ID不能为空");
        BizAssert.notEmpty(model.getFieldId(), "表单中字段ID不能为空");
        BizAssert.notEmpty(model.getFieldLabel(), "字段名称不能为空");
        //排序
        BizAssert.notNull(model.getMonitorSort(), "排序不能为空");
        InformationIntegrityMonitoring regGuide = BeanUtil.toBean(model, InformationIntegrityMonitoring.class);
        baseService.saveOrUpdate(regGuide);
        return R.success(regGuide);
    }
    @ApiOperation("删除信息完整度监控指标通过ID")
    @DeleteMapping("/delete/{tenantCode}/{id}")
    public R<Boolean> deleteById( @PathVariable("tenantCode") String tenantCode, @PathVariable("id") Long id) {
        BaseContextHandler.setTenant(tenantCode);
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        baseService.removeByIds(ids);
        return R.success();
    }
    @ApiOperation(value = "查询信息完整的同步任务状态  true|false  有执行中任务|无执行中任务")
    @GetMapping("getCodeInformationIntegritySwitch/{tenantCode}")
    public R<Boolean> getCodeInformationIntegritySwitch(@PathVariable("tenantCode") String tenantCode) {
        Boolean hasKey = redisTemplate.hasKey(SaasRedisBusinessKey.getTenantCodeInformationIntegritySwitch()+":"+tenantCode);
        if (Objects.nonNull(hasKey) && hasKey) {
            return R.success(hasKey);
        }
        return R.success(Boolean.FALSE);
    }

    /**
     * 信息完整度同步任务 （信息完整度监控指标录入结束触发）
     *
     * @return 实体
     */
    @ApiOperation(value = "信息完整度同步任务 （信息完整度监控指标录入结束触发）")
    @PostMapping(value = "informationIntegrityMonitoringTask")
    public R<Boolean> informationIntegrityMonitoringTask(@RequestBody InformationIntegrityMonitoringTaskDTO model) {
        BizAssert.notEmpty(model.getTenantCode(), "租户编码不能为空");
        return R.success(baseService.synInformationIntegrityMonitoringTask(model));
    }

    /**
     * 执行同步任务信息完整度的逻辑(指定患者触发)
     * @param tenantCode
     * @param patientId
     * @return
     */
    @ApiOperation(value = "执行同步任务信息完整度的逻辑(指定患者触发)")
    @GetMapping("singleSynInformationIntegrityMonitoringTask/{tenantCode}")
    public R singleSynInformationIntegrityMonitoringTask(@PathVariable("tenantCode") String tenantCode, @RequestParam("patientId") Long patientId) {
        R<Patient> patientR = patientApi.get(patientId);
        if (!patientR.getIsSuccess() || Objects.isNull(patientR.getData())) {
            throw new BizException("获取用户数据异常");
        }
        List<Patient> patients = new ArrayList<>();
        patients.add(patientR.getData());
        baseService.doSingleSynInformationIntegrityMonitoringTask(tenantCode,patients);
        return R.success();
    }

}
