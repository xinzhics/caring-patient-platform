package com.caring.sass.nursing.controller.drugs;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.dto.drugs.DrugsResultHandleHisPageDTO;
import com.caring.sass.nursing.dto.drugs.DrugsResultHandleHisSaveDTO;
import com.caring.sass.nursing.dto.drugs.DrugsResultHandleHisUpdateDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultHandleHis;
import com.caring.sass.nursing.service.drugs.DrugsResultHandleHisService;
import com.caring.sass.nursing.vo.DrugsHandledVo;
import com.caring.sass.security.annotation.PreAuth;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * <p>
 * 前端控制器
 * 患者管理-用药预警-预警处理历史表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/drugsResultHandleHis")
@Api(value = "DrugsResultHandleHis", tags = "患者管理-用药预警-预警处理历史表")
@PreAuth(replace = "drugsResultHandleHis:")
public class DrugsResultHandleHisController extends SuperController<DrugsResultHandleHisService, Long, DrugsResultHandleHis, DrugsResultHandleHisPageDTO, DrugsResultHandleHisSaveDTO, DrugsResultHandleHisUpdateDTO> {


    /**
     * 用药预警-已处理列表
     *
     * @param dto
     *
     */
    @ApiOperationSupport(author = "代嘉乐", order = 1)
    @ApiOperation(value = "用药预警-已处理列表")
    @PostMapping("getDrugsHandled")
    public R<IPage<DrugsHandledVo>> getDrugsHandled(@RequestBody @Valid DrugsAvailableDTO dto) {
        return R.success(baseService.getDrugsHandled(dto));
    }
    /**
     * 用药预警-已处理-清除标记
     *
     * @param
     */
    @ApiOperationSupport(author = "代嘉乐", order = 2)
    @ApiOperation(value = "用药预警-已处理-清除标记")
    @GetMapping("getDrugsEliminate")
    public R getDrugsEliminate() {
        return R.success(baseService.getDrugsEliminate());
    }
}
