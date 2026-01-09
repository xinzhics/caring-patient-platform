package com.caring.sass.nursing.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.dto.form.FormResultPageDTO;
import com.caring.sass.nursing.dto.form.FormResultSaveDTO;
import com.caring.sass.nursing.dto.form.FormResultUpdateDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName PlanApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:27
 * @Version 1.0
 **/
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", path = "/formResult", qualifier = "FormResultApi")
public interface FormResultApi extends SuperApi<Long, FormResult, FormResultPageDTO, FormResultSaveDTO, FormResultUpdateDTO> {


    @ApiOperation(value = "首次分阶段保存基本信息或疾病信息")
    @PostMapping("saveFormResultStage")
    R<FormResult> saveFormResultStage(@RequestBody FormResult formResult);

    @GetMapping("byCategory/{patientId}")
    R<FormResult> getFormResultByCategory(
            @PathVariable("patientId") Long patientId,
            @RequestParam("formEnum") @NotNull(message = "表单类型不能为空") FormEnum formEnum);


    @ApiOperation(value = "根据表单类型，获取 患者的 基本信息或疾病信息")
    @GetMapping("getFromResultByCategory/completeEnterGroup/{patientId}")
    R<FormResult> getFromByCategory(
            @PathVariable("patientId") Long patientId,
            @RequestParam("completeEnterGroup") Integer completeEnterGroup,
            @RequestParam("formEnum") @NotNull(message = "表单类型不能为空") FormEnum formEnum);


    @PostMapping("getCheckData/{patientId}")
    R<Page<FormResult>> getCheckData(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "type") @NotNull(message = "类型不能为空") Integer type);
}
