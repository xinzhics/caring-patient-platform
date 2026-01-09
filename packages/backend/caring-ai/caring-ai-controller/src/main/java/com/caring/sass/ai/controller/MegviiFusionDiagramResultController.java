package com.caring.sass.ai.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.entity.dto.MegviiFusionDiagramResultPageDTO;
import com.caring.sass.ai.entity.dto.MegviiFusionDiagramResultSaveDTO;
import com.caring.sass.ai.entity.dto.MegviiFusionDiagramResultUpdateDTO;
import com.caring.sass.ai.entity.face.MegviiFusionDiagramResult;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;
import com.caring.sass.ai.face.service.MegviiFusionDiagramResultService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 融合图结果
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/megviiFusionDiagramResult")
@Api(value = "MegviiFusionDiagramResult", tags = "哆咔叽 融合图结果")
public class MegviiFusionDiagramResultController extends SuperController<MegviiFusionDiagramResultService, Long, MegviiFusionDiagramResult, MegviiFusionDiagramResultPageDTO, MegviiFusionDiagramResultSaveDTO, MegviiFusionDiagramResultUpdateDTO> {


    @ApiOperation("查询用户可见的融合图结果")
    @PostMapping("/customPage")
    public R<IPage<MegviiFusionDiagramResult>> customPage(@RequestBody PageParams<MegviiFusionDiagramResultPageDTO> params) {

        IPage<MegviiFusionDiagramResult> builtPage = params.buildPage();
        MegviiFusionDiagramResultPageDTO paramsModel = params.getModel();
        LbqWrapper<MegviiFusionDiagramResult> wrapper = Wraps.<MegviiFusionDiagramResult>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .eq(MegviiFusionDiagramResult::getFusionDiagramId, paramsModel.getFusionDiagramId())
                .isNull(MegviiFusionDiagramResult::getErrorMessage);
        wrapper.select(SuperEntity::getId, MegviiFusionDiagramResult::getImageObsUrl, MegviiFusionDiagramResult::getFusionDiagramId);
        baseService.page(builtPage, wrapper);
        return R.success(builtPage);
    }



}
