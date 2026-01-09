package com.caring.open.service.controller.ucenter;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.open.service.entity.ucenter.OpenPatientDTO;
import com.caring.open.service.entity.ucenter.OpenPatientPageDTO;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.nursing.api.FormResultApi;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.PatientPageDTO;
import com.caring.sass.user.entity.Patient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patient")
@Api(value = "Patient", tags = "患者信息")
public class PatientController {

    private final PatientApi patientApi;

    private final FormResultApi formResultApi;

    public PatientController(PatientApi patientApi, FormResultApi formResultApi) {
        this.patientApi = patientApi;
        this.formResultApi = formResultApi;
    }

    @ApiOperation(value = "分页查询患者列表")
    @PostMapping(value = "/page")
    @SysLog(value = "'分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    public R<IPage<OpenPatientDTO>> page(@RequestBody @Validated PageParams<OpenPatientPageDTO> pageDTOPageParams) {
        OpenPatientPageDTO openPatientPageDTO = pageDTOPageParams.getModel();
        PatientPageDTO patientPageDTO = BeanUtil.copyProperties(openPatientPageDTO, PatientPageDTO.class);

        PageParams<PatientPageDTO> patientPageParams = new PageParams<>();
        patientPageParams.setModel(patientPageDTO);
        patientPageParams.setCurrent(pageDTOPageParams.getCurrent());
        patientPageParams.setSize(pageDTOPageParams.getSize());
        patientPageParams.setOrder(pageDTOPageParams.getOrder());
        patientPageParams.setSort(pageDTOPageParams.getSort());
        patientPageParams.setMap(pageDTOPageParams.getMap());
        R<IPage<Patient>> pageR = patientApi.page(patientPageParams);

        IPage<OpenPatientDTO> openPatientDTOPage = new Page<>();
        List<OpenPatientDTO> r = new ArrayList<>();
        if (pageR.getIsError()) {
            return R.fail(pageR.getMsg());
        }
        if (Objects.isNull(pageR.getData())) {
            return R.success(openPatientDTOPage);
        }
        IPage<Patient> patientPageInfo = pageR.getData();
        openPatientDTOPage.setPages(patientPageInfo.getPages());
        openPatientDTOPage.setSize(patientPageInfo.getSize());
        openPatientDTOPage.setCurrent(patientPageInfo.getCurrent());
        openPatientDTOPage.setTotal(patientPageInfo.getTotal());
        for (Patient record : patientPageInfo.getRecords()) {
            r.add(BeanUtil.copyProperties(record, OpenPatientDTO.class));
        }
        openPatientDTOPage.setRecords(r);
        return R.success(openPatientDTOPage);
    }

    @ApiOperation(value = "获取患者检验数据或健康日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页大小", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型：3是检验数据，1 血压监测, 5是健康日志", paramType = "query")
    })
    @PostMapping("getCheckData/{patientId}")
    public R<Page<FormResult>> getCheckData(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "type") @NotNull(message = "类型不能为空") Integer type) {
        return formResultApi.getCheckData(patientId, current, size, type);
    }

    /**
     * 批量同步应用内unionId
     */
    @ApiOperation(value = "批量同步应用内unionId", notes = "批量同步应用内unionId")
    @GetMapping(value = "/batchGetUnionId")
    public R<Boolean> batchGetUnionId() {
        return patientApi.batchUpdateUnionId();
    }


}
