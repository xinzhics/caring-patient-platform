package com.caring.sass.ai.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.entity.dto.MegviiTemplateDiagramPageDTO;
import com.caring.sass.ai.entity.dto.MegviiTemplateDiagramTypePageDTO;
import com.caring.sass.ai.entity.dto.MegviiTemplateDiagramTypeSaveDTO;
import com.caring.sass.ai.entity.dto.MegviiTemplateDiagramTypeUpdateDTO;
import com.caring.sass.ai.entity.face.MegviiFusionDiagram;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagramType;
import com.caring.sass.ai.face.service.MegviiFusionDiagramService;
import com.caring.sass.ai.face.service.MegviiTemplateDiagramService;
import com.caring.sass.ai.face.service.MegviiTemplateDiagramTypeService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 模版图分类
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/megviiTemplateDiagramType")
@Api(value = "MegviiTemplateDiagramType", tags = "哆咔叽 模版图分类")
public class MegviiTemplateDiagramTypeController extends SuperController<MegviiTemplateDiagramTypeService, Long, MegviiTemplateDiagramType, MegviiTemplateDiagramTypePageDTO, MegviiTemplateDiagramTypeSaveDTO, MegviiTemplateDiagramTypeUpdateDTO> {


    @Autowired
    MegviiTemplateDiagramService megviiTemplateDiagramService;

    @Autowired
    MegviiFusionDiagramService fusionDiagramService;


    @PostMapping("customPage")
    @ApiOperation(value = "模版分类列表")
    public R<IPage<MegviiTemplateDiagramType>> customPage(@RequestBody PageParams<MegviiTemplateDiagramTypePageDTO> params) {

        Long userId = BaseContextHandler.getUserId();
        IPage<MegviiTemplateDiagramType> builtPage = params.buildPage();
        MegviiTemplateDiagramTypePageDTO model = params.getModel();
        LbqWrapper<MegviiTemplateDiagramType> wrapper = Wraps.<MegviiTemplateDiagramType>lbQ().eq(MegviiTemplateDiagramType::getGender, model.getGender());
        wrapper.orderByDesc(MegviiTemplateDiagramType::getOrder);
        baseService.page(builtPage, wrapper);


        List<MegviiTemplateDiagramType> records = builtPage.getRecords();
        if (records.isEmpty()) {
            return R.success(builtPage);
        }
        for (MegviiTemplateDiagramType record : records) {
            Long id = record.getId();
            if (record.getFree().equals(1)) {
                // 判断这个限免分类用户有没有使用过
                int count = fusionDiagramService.count(Wraps.<MegviiFusionDiagram>lbQ().eq(MegviiFusionDiagram::getUserId, userId)
                        .like(MegviiFusionDiagram::getTemplateDiagramTypeIds, id.toString()));
                if (count > 0) {
                    record.setFree(0);
                }
            }
            MegviiTemplateDiagram templateDiagram = megviiTemplateDiagramService.getOne(Wraps.<MegviiTemplateDiagram>lbQ()
                    .select(SuperEntity::getId, MegviiTemplateDiagram::getImageObsUrl)
                    .eq(MegviiTemplateDiagram::getTemplateDiagramType, id)
                    .eq(MegviiTemplateDiagram::getGender, model.getGender())
                    .orderByDesc(MegviiTemplateDiagram::getOrder)
                    .last("limit 1"));
            if (Objects.nonNull(templateDiagram)) {
                record.setImageObsUrl(templateDiagram.getImageObsUrl());
            }
        }
        return R.success(builtPage);
    }



}
