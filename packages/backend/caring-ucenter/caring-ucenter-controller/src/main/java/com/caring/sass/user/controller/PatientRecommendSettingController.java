package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.PatientRecommendSetting;
import com.caring.sass.user.service.PatientRecommendSettingService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 运营配置-患者推荐配置
 * </p>
 *
 * @author lixiang
 * @date 2022-07-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientRecommendSetting")
@Api(value = "PatientRecommendSetting", tags = "运营配置-患者推荐配置")
//@PreAuth(replace = "patientRecommendSetting:")
public class PatientRecommendSettingController extends SuperController<PatientRecommendSettingService, Long, PatientRecommendSetting, PatientRecommendSettingPageDTO, PatientRecommendSettingSaveDTO, PatientRecommendSettingUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PatientRecommendSetting> patientRecommendSettingList = list.stream().map((map) -> {
            PatientRecommendSetting patientRecommendSetting = PatientRecommendSetting.builder().build();
            //TODO 请在这里完成转换
            return patientRecommendSetting;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(patientRecommendSettingList));
    }
    /**
     * 超管端-患者推荐-查询
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 1)
    @ApiOperation(value = "超管端-患者推荐-查询")
    @GetMapping(value = "getPatientRecommend/{tenantCode}")
    public R<PatientRecommendSetting> getPatientRecommend(@PathVariable("tenantCode")  String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        return R.success(baseService.getPatientRecommend(tenantCode));
    }
    /**
     * 超管端-患者推荐-新增或更新
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 2)
    @ApiOperation(value = "超管端-患者推荐-新增或更新")
    @PostMapping(value = "savePatientRecommend")
    public R savePatientRecommend(@RequestBody @Valid PatientRecommendSettingSaveDTO model) {
        BaseContextHandler.setTenant(model.getTenantCode());
        PatientRecommendSetting patientRecommendSetting = BeanUtil.toBean(model, PatientRecommendSetting.class);
        return R.success(baseService.saveOrUpdate(patientRecommendSetting));
    }

    /**
     * 超管端-患者推荐-查询医生
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 3)
    @ApiOperation(value = "超管端-患者推荐-查询医生")
    @PostMapping(value = "getPatientRecommendDoctor")
    public R<IPage<GetPatientRecommendDoctorVo>> getPatientRecommendDoctor(@RequestBody @Valid GetPatientRecommendDoctorDTO dto) {
        BaseContextHandler.setTenant(dto.getTenantCode());
        return R.success(baseService.getPatientRecommendDoctor(dto));
    }

    /**
     * 超管端-患者推荐-推荐页链接地址-复制
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 4)
    @ApiOperation(value = "超管端-患者推荐-推荐页链接地址-复制")
    @GetMapping(value = "recommendUrlCopy/{tenantCode}")
    public R<RecommendUrlVo> recommendUrlCopy(@PathVariable("tenantCode")  String tenantCode) {
        return R.success(baseService.recommendUrlCopy(tenantCode));
    }
}
