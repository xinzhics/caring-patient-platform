package com.caring.sass.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.AppPatientPageDTO;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName AppPatientController
 * @Description
 * @Author yangShuai
 * @Date 2020/11/10 15:33
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/app/patient")
@Api(value = "Patient", tags = "app患者分组统计")
//@PreAuth(replace = "patient:")
public class AppPatientController {

    @Autowired
    PatientService patientService;

    @ApiOperation("返回会员状态列表页面")
    @GetMapping("/getPatientsByStatusGroupNew")
    public R<JSONArray> getPatientByStateNew(@RequestParam("userId") Long userId, @RequestParam("dimension") String dimension) {
        JSONArray result = new JSONArray();
        int count1 = patientService.countPatientByStatus(0, userId, dimension);
        int count2 = patientService.countPatientByStatus(1, userId, dimension);
        int count3 = patientService.countPatientByStatus(2, userId, dimension);
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject statu0 = new JSONObject();
        JSONObject statu1 = new JSONObject();
        JSONObject statu2 = new JSONObject();
        JSONObject statu3 = new JSONObject();
        statu0.put("id", String.valueOf(0));
        statu0.put("name", "未注册");
        String count = "count";
        statu0.put("count", count1);
        statu0.put("type", "status");
        statu1.put("id", String.valueOf(1));
        statu1.put("name", "已注册");
        statu1.put("count", count2);
        statu1.put("type", "status");
        statu2.put("id", String.valueOf(2));
        statu2.put("name", "取关");
        statu2.put("count", count3);
        statu2.put("type", "status");

        statu3.put("name", "全部");
        statu3.put("id", -1);
        statu3.put("count", count3 + count1 + count2);
        statu3.put("type", "status");
        array.add(statu0);
        array.add(statu1);
        array.add(statu2);
        array.add(statu3);
        json.put("key", "按会员类型");
        json.put("data", array);
        result.add(json);
        JSONObject zdlx = new JSONObject();
        zdlx.put("key", "按诊断类型");
        JSONArray countDiagnosisId = this.patientService.countDiagnosisId(userId, dimension);
        zdlx.put("data", countDiagnosisId);
        result.add(zdlx);
        return R.success(result);
    }


}
