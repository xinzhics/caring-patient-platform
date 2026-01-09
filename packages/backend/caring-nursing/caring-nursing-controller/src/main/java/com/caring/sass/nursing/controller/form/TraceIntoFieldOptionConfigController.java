package com.caring.sass.nursing.controller.form;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dto.traceInto.*;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFieldOptionConfig;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.service.traceInto.TraceIntoFieldOptionConfigService;
import com.caring.sass.utils.BizAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 选项跟踪表单字段选项配置表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/traceIntoFieldOptionConfig")
@Api(value = "TraceIntoFieldOptionConfig", tags = "选项跟踪表单字段选项配置表")
public class TraceIntoFieldOptionConfigController extends SuperController<TraceIntoFieldOptionConfigService, Long, TraceIntoFieldOptionConfig, TraceIntoFieldOptionConfigPageDTO, TraceIntoFieldOptionConfigSaveDTO, TraceIntoFieldOptionConfigUpdateDTO> {

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TraceIntoFieldOptionConfig> traceIntoFieldOptionConfigList = list.stream().map((map) -> {
            TraceIntoFieldOptionConfig traceIntoFieldOptionConfig = TraceIntoFieldOptionConfig.builder().build();
            //TODO 请在这里完成转换
            return traceIntoFieldOptionConfig;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(traceIntoFieldOptionConfigList));
    }


    @ApiOperation("查询选中跟踪配置")
    @GetMapping("queryConfig")
    public R<List<PlanFormDTO>> queryConfig(@RequestParam(name = "tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        List<PlanFormDTO> planFormDTOList = baseService.webQueryConfig();
        return R.success(planFormDTOList);
    }

    @ApiOperation("更新选项跟踪配置")
    @PutMapping("updateConfig")
    public R<Boolean> updateConfig(@RequestBody TraceIntoFieldOptionConfigSaveDTO saveDTO) {
        String tenantCode = saveDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        baseService.updateConfig(saveDTO.getUpdateDTOList());
        return R.success(true);
    }

    @ApiOperation(value = "查询患者管理-选项跟踪-立即生效-任务状态  1|2|3  待执行任务|执行任务中|已执行任务")
    @GetMapping("getTraceIntoTaskSwitch/{tenantCode}")
    public R<String> getTraceIntoTaskSwitch(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenantCode);
        if (Objects.nonNull(value)) {
            return R.success(value);
        }
        return R.success(MonitoringTaskType.WAIT.operateType);
    }

    @ApiOperation(value = "患者管理-选项跟踪-立即生效")
    @GetMapping(value = "traceIntoTask/{tenantCode}")
    public R<Boolean> traceIntoTask(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenantCode);
        if (!StringUtils.isEmpty(value) && value.equals(MonitoringTaskType.RUNING.operateType)){
            BizAssert.notNull(null, "存在执行任务中！禁止进行立即生效！");
        }
        baseService.traceIntoTask(tenantCode);
        return R.success(true);
    }

}
