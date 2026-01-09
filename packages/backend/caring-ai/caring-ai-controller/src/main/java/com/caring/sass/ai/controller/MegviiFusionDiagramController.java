package com.caring.sass.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.CozeToken;
import com.caring.sass.ai.dto.FaceWechatOrder;
import com.caring.sass.ai.dto.UserSubscriptionTemplate;
import com.caring.sass.ai.entity.dto.*;
import com.caring.sass.ai.entity.face.MegviiFusionDiagram;
import com.caring.sass.ai.entity.face.MegviiFusionDiagramResult;
import com.caring.sass.ai.entity.face.VolcengineParams;
import com.caring.sass.ai.face.service.MegviiFusionDiagramResultService;
import com.caring.sass.ai.face.service.MegviiFusionDiagramService;
import com.caring.sass.ai.utils.VolcengineApi2;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.file.entity.File;
import com.caring.sass.wx.entity.config.WechatOrders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 融合图管理
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/megviiFusionDiagram")
@Api(value = "MegviiFusionDiagram", tags = "哆咔叽 融合图管理")
public class MegviiFusionDiagramController extends SuperController<MegviiFusionDiagramService, Long, MegviiFusionDiagram, MegviiFusionDiagramPageDTO, MegviiFusionDiagramSaveDTO, MegviiFusionDiagramUpdateDTO> {

    @Autowired
    MegviiFusionDiagramResultService megviiFusionDiagramResultService;

    @Autowired
    VolcengineApi2 volcengineApi2;

    @Autowired
    FileUploadApi fileUploadApi;


    @ApiOperation("计算价格 单位 分")
    @PostMapping("calculatePrice")
    public R<Integer> calculatePrice(@RequestBody @Validated FaceWechatOrder faceWechatOrder) {

        Integer cost = baseService.calculatePrice(faceWechatOrder.getUserId(), faceWechatOrder.getTypeIds());
        return R.success(cost);
    }

    @ApiOperation("创建一个微信订单")
    @PostMapping("createdWechatOrder")
    public R<WechatOrders> createdWechatOrder(@RequestBody @Validated FaceWechatOrder faceWechatOrder) {

        WechatOrders wechatOrders = baseService.createdWechatOrder(faceWechatOrder.getUserId(), faceWechatOrder.getTypeIds());
        return R.success(wechatOrders);
    }

    @ApiOperation(value = "重启某个任务")
    @GetMapping(value = "/restartMergeImage/{id}")
    public R<Boolean> restartMergeImage(@PathVariable(name = "id") Long id, @RequestParam Boolean resetImageSize) {
        baseService.reStartMergeImage(id, resetImageSize);
        return R.success(true);
    }


    @ApiOperation(value = "调参")
    @PostMapping(value = "/resetVolcengineParams")
    public R<String> restartMergeImage(@RequestBody VolcengineParams volcengineParams) {
        try {
            String string = volcengineApi2.mergeImageFacesWap(volcengineParams.getFaceImage(), volcengineParams.getTemplateImage(), "1", volcengineParams.getGpen(), volcengineParams.getSkin());
            MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile("data:image/jpeg;base64," + string);
            if (Objects.nonNull(multipartFile)) {
                R<File> uploaded = fileUploadApi.upload(2L, multipartFile);
                if (uploaded.getIsSuccess()) {
                    File fileInfo = uploaded.getData();
                    return R.success(fileInfo.getUrl());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return R.success("失败");
    }


    @ApiOperation(value = "上传一个融合图（微信支付成功后）")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public R<MegviiFusionDiagram> upload(
            @RequestParam(value = "wechatOrderId", required = false) Long wechatOrderId,
            @RequestParam(value = "templateDiagramTypeIds") String templateDiagramTypeIds,
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "file") MultipartFile file) {

        MegviiFusionDiagram diagram = baseService.cozeSave(file, templateDiagramTypeIds, userId, wechatOrderId);
        MegviiFusionDiagram temp = new MegviiFusionDiagram();
        BeanUtil.copyProperties(diagram, temp);
        temp.setImageBase64(null);
        return R.success(temp);


    }


    @ApiOperation(value = "用户订阅模版")
    @RequestMapping(value = "/addTemplateSubscription", method = RequestMethod.POST)
    public R<String> addTemplateSubscription(@RequestBody UserSubscriptionTemplate userSubscriptionTemplate) {

        baseService.addTemplateSubscription(userSubscriptionTemplate);
        return R.success("success");
    }

    @ApiOperation(value = "添加一个coze合并人脸的token")
    @PostMapping("addFaceMergeCozeToken")
    public R<String> addFaceMergeCozeToken(@RequestBody CozeToken cozeToken) {
        baseService.addFaceMergeCozeToken(cozeToken.getToken(), cozeToken.getBotId());
        return R.success(cozeToken.getToken());
    }


    @ApiOperation(value = "移除一个coze合并人脸的token")
    @PostMapping("removeFaceMergeCozeToken")
    public R<String> removeFaceMergeCozeToken(@RequestBody CozeToken cozeToken) {
        baseService.removeFaceMergeCozeToken(cozeToken.getToken());
        return R.success(cozeToken.getToken());
    }


    @ApiOperation("查询用户可见的融合图结果")
    @PostMapping("/customPage")
    public R<IPage<MegviiFusionDiagram>> customPage(@RequestBody @Validated PageParams<MegviiFusionDiagramPageDTO> params) {

        IPage<MegviiFusionDiagram> builtPage = params.buildPage();
        MegviiFusionDiagramPageDTO paramsModel = params.getModel();
        Long userId = paramsModel.getUserId();
        if (Objects.isNull(userId)) {
            return R.fail("请使用手机微信打开小程序");
        }
        LbqWrapper<MegviiFusionDiagram> wrapper = Wraps.<MegviiFusionDiagram>lbQ()
                .eq(MegviiFusionDiagram::getUserId, paramsModel.getUserId())
                .eq(MegviiFusionDiagram::getDeleteStatus, CommonStatus.NO)
                .orderByDesc(SuperEntity::getCreateTime);
        wrapper.select(SuperEntity::getId, SuperEntity::getCreateTime, MegviiFusionDiagram::getTaskStatus);
        baseService.page(builtPage, wrapper);
        for (MegviiFusionDiagram record : builtPage.getRecords()) {
            List<MegviiFusionDiagramResult> resultList = megviiFusionDiagramResultService.list(Wraps.<MegviiFusionDiagramResult>lbQ()
                    .select(MegviiFusionDiagramResult::getImageObsUrl, SuperEntity::getId)
                    .eq(MegviiFusionDiagramResult::getFusionDiagramId, record.getId())
                    .isNull(MegviiFusionDiagramResult::getErrorMessage));
            record.setResultList(resultList);
        }
        return R.success(builtPage);
    }


}
