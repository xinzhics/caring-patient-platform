package com.caring.sass.nursing.controller.drugs;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.dto.drugs.DrugsResultInformationPageDTO;
import com.caring.sass.nursing.dto.drugs.DrugsResultInformationSaveDTO;
import com.caring.sass.nursing.dto.drugs.DrugsResultInformationUpdateDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultInformation;
import com.caring.sass.nursing.service.drugs.DrugsResultInformationService;
import com.caring.sass.nursing.vo.DrugsAvailableVo;
import com.caring.sass.nursing.vo.DrugsListVo;
import com.caring.sass.nursing.vo.DrugsStatisticsVo;
import com.caring.sass.security.annotation.PreAuth;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * 患者管理-用药预警-预警结果表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/drugsResultInformation")
@Api(value = "DrugsResultInformation", tags = "患者管理-用药预警-预警结果表")
@PreAuth(replace = "drugsResultInformation:")
public class DrugsResultInformationController extends SuperController<DrugsResultInformationService, Long, DrugsResultInformation, DrugsResultInformationPageDTO, DrugsResultInformationSaveDTO, DrugsResultInformationUpdateDTO> {

    /**
     * 用药预警-患者数
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 1)
    @ApiOperation(value = "用药预警-患者数")
    @GetMapping("getDrugsNumber")
    public R<Integer> getDrugsNumber() {

        return R.success(baseService.getDrugsNumber());
    }

    /**
     * 用药预警-余药不足
     *
     * @param dto
     */
    @ApiOperationSupport(author = "代嘉乐", order = 2)
    @ApiOperation(value = "用药预警-余药不足")
    @PostMapping("getDrugsDeficiency")
    public R<IPage<DrugsAvailableVo>> getDrugsDeficiency(@RequestBody @Valid DrugsAvailableDTO dto) {
        return R.success(baseService.getDrugsDeficiency(dto));
    }

    /**
     * 用药预警-购药逾期
     *
     * @param dto
     */
    @ApiOperationSupport(author = "代嘉乐", order = 3)
    @ApiOperation(value = "用药预警-购药逾期")
    @PostMapping("getDrugsBeOverdue")
    public R<IPage<DrugsAvailableVo>> getDrugsBeOverdue(@RequestBody @Valid DrugsAvailableDTO dto) {
        return R.success(baseService.getDrugsBeOverdue(dto));
    }

    /**
     * 用药预警-余药不足-单个（全部）处理
     *
     * @param patientId
     */
    @ApiOperationSupport(author = "代嘉乐", order = 4)
    @ApiOperation(value = "用药预警-余药不足-单个（全部）处理")
    @GetMapping(value = {"getDrugsDeficiencyHandle/{patientId}", "getDrugsDeficiencyHandle"})
    public R getDrugsDeficiencyHandle(@PathVariable(value = "patientId", required = false) Long patientId) {

        return R.success(baseService.getDrugsDeficiencyHandle(patientId));
    }

    /**
     * 用药预警-用药逾期-单个（全部）处理
     *
     * @param patientId
     */
    @ApiOperationSupport(author = "代嘉乐", order = 5)
    @ApiOperation(value = "用药预警-用药逾期-单个（全部）处理")
    @GetMapping(value = {"getDrugsBeOverdueHandle/{patientId}", "getDrugsBeOverdueHandle"})
    public R getDrugsBeOverdueHandle(@PathVariable(value = "patientId", required = false) Long patientId) {

        return R.success(baseService.getDrugsBeOverdueHandle(patientId));
    }


    /**
     * 用药预警-统计
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 6)
    @ApiOperation(value = "用药预警-统计")
    @GetMapping(value = { "getDrugsStatistics/{drugsId}", "getDrugsStatistics"})
    public R<DrugsStatisticsVo> getDrugsStatistics(@PathVariable(value = "drugsId",required = false ) Long drugsId) {
        return R.success(baseService.getDrugsStatistics(drugsId));
    }

    /**
     * 用药预警-统计-药品列表
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 7)
    @ApiOperation(value = "用药预警-统计-药品列表")
    @GetMapping("getDrugsList")
    public R<List<DrugsListVo>> getDrugsList() {
        return R.success(baseService.getDrugsList());
    }
}
