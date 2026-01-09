package com.caring.open.service.controller.third;

import cn.hutool.core.bean.BeanUtil;
import com.caring.open.service.entity.third.AccountReq;
import com.caring.open.service.entity.third.DateReportReq;
import com.caring.sass.base.R;
import com.caring.sass.nursing.api.FormResultApi;
import com.caring.sass.nursing.dto.form.FormResultSaveDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 怡康智护血糖仪项目硬件数据对接
 *
 * @author xinz
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ykzh")
@Api(value = "ykzh", tags = "怡康智护")
public class YkzhController {

    private final PatientApi patientApi;

    private final FormResultApi formResultApi;

    public YkzhController(PatientApi patientApi, FormResultApi formResultApi) {
        this.patientApi = patientApi;
        this.formResultApi = formResultApi;
    }

    @ApiOperation(value = "注册账户", notes = "注册账户")
    @PostMapping("/api/account")
    public Boolean regAccount(@RequestBody AccountReq req) {
        log.info("收到注册账号信息:{}", req);
        return true;
    }

    @ApiOperation(value = "更新账户", notes = "更新账户")
    @PutMapping("/api/account")
    public Boolean updateAccount(@RequestBody AccountReq req) {
        log.info("收到更新账号信息:{}", req);
        return true;
    }

    /**
     * 负责向服务器上传设备上的血糖测试数据
     *
     * @param dateReport 数据上传信息
     */
    @ApiOperation(value = "数据上传", notes = "数据上传")
    @PostMapping("/api/data")
    public Boolean uploadData(@RequestBody DateReportReq dateReport) {
        log.info("数据上传信息:{}", dateReport);
        // 这里为手机号
        String mobile = dateReport.getAccountName();
        R<List<Patient>> patientsR = patientApi.query(new Patient().setMobile(mobile));
        if (patientsR.getIsError() || patientsR.getData().size() == 0) {
            log.error("用户{}未在怡康智护应用绑定或未填写手机号", mobile);
            return true;
        }
        Patient patient = patientsR.getData().get(0);
        FormResult formResult = FormResultBuilder.build(dateReport, patient.getId());
        formResultApi.save(BeanUtil.toBean(formResult, FormResultSaveDTO.class));
        return true;
    }

    /**
     * 注册/登录设备接口
     */
    @ApiOperation(value = "设备注册", notes = "设备注册")
    @GetMapping("/api/device/link")
    public Boolean deViceLink(@RequestParam("serial") String serial) {
        log.info("收到注册/登录设备信息:{}", serial);
        return true;
    }

}
