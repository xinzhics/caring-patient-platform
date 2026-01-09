package com.caring.sass.nursing.api;

import com.alibaba.fastjson.JSONArray;
import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.api.hystrix.FormApiFallback;
import com.caring.sass.nursing.dto.form.CopyFormDTO;
import com.caring.sass.nursing.dto.form.FormPageDTO;
import com.caring.sass.nursing.dto.form.FormSaveDTO;
import com.caring.sass.nursing.dto.form.FormUpdateDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName PlanApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:27
 * @Version 1.0
 **/
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = FormApiFallback.class
        , path = "/form", qualifier = "FormApi")
public interface FormApi extends SuperApi<Long, Form, FormPageDTO, FormSaveDTO, FormUpdateDTO> {

    /**
     * 存在更新记录，否插入一条记录
     *
     * @return 实体
     */
    @PostMapping(value = "saveOrUpdate")
    R<Form> saveOrUpdate(@RequestBody FormSaveDTO model);


    @GetMapping("getFromByCategory/{tenantCode}")
    R<List<Form>> getFromByCategory(@PathVariable("tenantCode") String tenantCode, @RequestParam("categorys") String categorys);

    @ApiOperation(value = "查询表单是否一题一页填写,1：是 。 2否")
    @GetMapping("getFormIntoTheGroup")
    R<Integer> getFormIntoTheGroup(@RequestParam(value = "category", required = false) String category,
                                          @RequestParam(value = "planId", required = false) Long planId);

    /**
     * 复制表单
     */
    @PostMapping("copyForm")
    R<Boolean> copyForm(@RequestBody CopyFormDTO copyFormDTO);

    @GetMapping("getDiagnosis")
    @ApiOperation("获取诊断类型")
    R<JSONArray> getDiagnosis();


    @ApiOperation(value = "查询表单")
    @GetMapping(value = "/getFormByCategoryAndBizId")
    R<Form> getFormByCategoryAndBizId(@RequestParam(name = "category") FormEnum category,
                                             @RequestParam(name = "bizId", required = false) String bizId);
}
