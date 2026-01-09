package com.caring.sass.ai.controller;


import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.entity.face.MergeImageFreeFrequency;
import com.caring.sass.ai.face.service.MergeImageFreeFrequencyService;
import com.caring.sass.base.R;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 融合图片免费次数
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/mergeImageFreeFrequency")
@Api(value = "MergeImageFreeFrequency", tags = "哆咔叽 融合图片免费次数")
@PreAuth(replace = "mergeImageFreeFrequency:")
public class MergeImageFreeFrequencyController {

    @Autowired
    MergeImageFreeFrequencyService mergeImageFreeFrequencyService;

    @Autowired
    FaceConfig faceConfig;

    @ApiOperation("查询免费次数")
    @GetMapping("getByUserId/{userId}")
    public R<MergeImageFreeFrequency> getByUserId(@PathVariable Long userId) {

        MergeImageFreeFrequency freeFrequency = mergeImageFreeFrequencyService.queryOrInitUserFreeFrequency(userId);
        freeFrequency.setMergeImageCost(faceConfig.getMerge_image_cost());
        return R.success(freeFrequency);

    }



}
