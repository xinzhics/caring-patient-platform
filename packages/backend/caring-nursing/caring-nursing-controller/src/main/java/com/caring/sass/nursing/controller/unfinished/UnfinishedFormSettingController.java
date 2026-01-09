package com.caring.sass.nursing.controller.unfinished;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dto.traceInto.PlanFormDTO;
import com.caring.sass.nursing.dto.traceInto.TraceIntoFieldOptionConfigSaveDTO;
import com.caring.sass.nursing.dto.unfinished.UnfinishedFormSettingPageDTO;
import com.caring.sass.nursing.dto.unfinished.UnfinishedFormSettingSaveDTO;
import com.caring.sass.nursing.dto.unfinished.UnfinishedFormSettingUpdateDTO;
import com.caring.sass.nursing.entity.unfinished.UnfinishedFormSetting;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.service.unfinished.UnfinishedFormSettingService;
import com.caring.sass.security.annotation.PreAuth;
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
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 患者管理-未完成表单跟踪设置
 * </p>
 *
 * @author 杨帅
 * @date 2024-05-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/unfinishedFormSetting")
@Api(value = "UnfinishedFormSetting", tags = "患者管理-未完成表单跟踪设置")
@PreAuth(replace = "unfinishedFormSetting:")
public class UnfinishedFormSettingController extends SuperController<UnfinishedFormSettingService, Long, UnfinishedFormSetting, UnfinishedFormSettingPageDTO, UnfinishedFormSettingSaveDTO, UnfinishedFormSettingUpdateDTO> {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @ApiOperation("查询未完成跟踪配置")
    @GetMapping("queryConfig")
    public R<List<PlanFormDTO>> queryConfig(@RequestParam(name = "tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        List<PlanFormDTO> planFormDTOList = baseService.webQueryConfig();
        return R.success(planFormDTOList);
    }

    @ApiOperation("更新未完成跟踪配置")
    @PutMapping("updateConfig")
    public R<Boolean> updateConfig(@RequestBody UnfinishedFormSettingSaveDTO saveDTO) {
        String tenantCode = saveDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        baseService.updateConfig(saveDTO.getUpdateDTOList());
        return R.success(true);
    }

    @ApiOperation(value = "查询未完成跟踪的开关状态  1|2|3  待执行任务|执行任务中|已执行任务")
    @GetMapping("getUnFinishedRemindSwitch/{tenantCode}")
    public R<String> getUnFinishedRemindSwitch(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenantCode);
        if (Objects.nonNull(value)) {
            return R.success(value);
        }
        return R.success(MonitoringTaskType.WAIT.operateType);
    }

    @ApiOperation(value = "患者管理-未完成跟踪-立即生效")
    @GetMapping(value = "unFinishedTask/{tenantCode}")
    public R<Boolean> unFinishedTask(@PathVariable("tenantCode") String tenantCode) {
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenantCode);
        if (!StringUtils.isEmpty(value) && value.equals(MonitoringTaskType.RUNING.operateType)){
            BizAssert.notNull(null, "存在执行任务中！禁止进行立即生效！");
        }
        baseService.unFinishedTask(tenantCode);
        return R.success(true);
    }
}
