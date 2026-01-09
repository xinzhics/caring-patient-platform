package com.caring.sass.ai.controller.card;

import com.caring.sass.ai.card.service.BusinessCardUseDataService;
import com.caring.sass.ai.entity.card.BusinessCardUseData;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 名片使用数据收集
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardUseData")
@Api(value = "BusinessCardUseData", tags = "名片使用数据收集")
public class BusinessCardUseDataController {

    @Autowired
    BusinessCardUseDataService useDataService;


    @ApiOperation("提交用户使用数据")
    @PostMapping()
    public R<Boolean> save(@RequestBody @Validated BusinessCardUseData useData) {

        useDataService.save(useData);
        return R.success(true);

    }



    @ApiOperation("查询用户是否还可以点赞, true 可以")
    @GetMapping("check/exist")
    public R<Boolean> query(@RequestBody @Validated BusinessCardUseData useData) {

        LbqWrapper<BusinessCardUseData> wrapper = Wraps.<BusinessCardUseData>lbQ()
                .eq(BusinessCardUseData::getOpenId, useData.getOpenId())
                .eq(BusinessCardUseData::getBusinessCardId, useData.getBusinessCardId())
                .eq(BusinessCardUseData::getDataType, useData.getDataType());

        int count = useDataService.count(wrapper);
        return R.success(count <= 0);

    }


}
