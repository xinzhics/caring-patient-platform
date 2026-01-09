package com.caring.sass.ai.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.entity.dto.*;
import com.caring.sass.ai.entity.face.MegviiFusionDiagramResult;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;
import com.caring.sass.ai.face.service.MegviiTemplateDiagramService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.wx.WechatOrdersApi;
import com.caring.sass.wx.entity.config.WechatOrders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 模版图管理
 * </p>
 *
 * @author 杨帅
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/megviiTemplateDiagram")
@Api(value = "MegviiTemplateDiagram", tags = "哆咔叽 模版图管理")
public class MegviiTemplateDiagramController extends SuperController<MegviiTemplateDiagramService, Long, MegviiTemplateDiagram, MegviiTemplateDiagramPageDTO, MegviiTemplateDiagramSaveDTO, MegviiTemplateDiagramUpdateDTO> {


    @ApiOperation(value = "上传一个模版图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order", value = "排序", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "gender", value = "Male 男, Female 女", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "templateDiagramType", value = "分类id", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "模版图", dataType = "MultipartFile", allowMultiple = true, required = true),
    })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @SysLog("上传文件")
    public R<MegviiTemplateDiagram> upload(
            @RequestParam(value = "gender") String gender,
            @RequestParam(value = "order", required = false) Integer order,
            @RequestParam(value = "templateDiagramType") Long templateDiagramType,
            @RequestParam(value = "file") MultipartFile file) {

        MegviiTemplateDiagram diagram = baseService.save(file, templateDiagramType, order, gender);
        diagram.setImageBase64(null);
        return R.success(diagram);
    }

    @ApiOperation(value = "上传一个模版图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order", value = "排序", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "gender", value = "Male 男, Female 女", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "templateDiagramType", value = "分类id", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "模版图", dataType = "MultipartFile", allowMultiple = true, required = true),
    })
    @RequestMapping(value = "/upload/files", method = RequestMethod.POST)
    @SysLog("上传文件")
    public R<String> uploadFiles(
            @RequestParam(value = "gender") String gender,
            @RequestParam(value = "order", required = false) Integer order,
            @RequestParam(value = "templateDiagramType") Long templateDiagramType,
            @RequestParam(value = "file") List<MultipartFile> files) {

        for (MultipartFile file : files) {
            MegviiTemplateDiagram diagram = baseService.save(file, templateDiagramType, order, gender);
            diagram.setImageBase64(null);
        }
        return R.success("SUCCESS");
    }


    @Deprecated
    @PostMapping("customPage")
    @ApiOperation(value = "模版图分页查询")
    public R<IPage<MegviiTemplateDiagram>> customPage(@RequestBody PageParams<MegviiTemplateDiagramPageDTO> params) {

        IPage<MegviiTemplateDiagram> builtPage = params.buildPage();
        MegviiTemplateDiagramPageDTO model = params.getModel();

        LbqWrapper<MegviiTemplateDiagram> wrapper = Wraps.<MegviiTemplateDiagram>lbQ().eq(MegviiTemplateDiagram::getGender, model.getGender());
        wrapper.orderByDesc(MegviiTemplateDiagram::getOrder);
        if (model.getTemplateDiagramType() != null) {
            wrapper.eq(MegviiTemplateDiagram::getTemplateDiagramType, model.getTemplateDiagramType());
        }
        wrapper.select(SuperEntity::getId, MegviiTemplateDiagram::getGender, MegviiTemplateDiagram::getTemplateDiagramType, MegviiTemplateDiagram::getImageObsUrl);
        baseService.page(builtPage, wrapper);
        return R.success(builtPage);
    }




}
