package com.caring.sass.ai.controller.card;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.card.service.BusinessCardOrganService;
import com.caring.sass.ai.entity.card.BusinessCardOrgan;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 科普名片组织
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardOrgan")
@Api(value = "BusinessCardOrgan", tags = "科普名片组织")
public class BusinessCardOrganController {

    @Autowired
    BusinessCardOrganService businessCardOrganService;


    @ApiOperation("新增组织")
    @PostMapping("")
    public R<BusinessCardOrgan> save(@RequestBody BusinessCardOrgan businessCardOrgan) {

        businessCardOrganService.save(businessCardOrgan);
        return R.success(businessCardOrgan);
    }


    @ApiOperation("查询组织列表")
    @GetMapping
    public R<List<BusinessCardOrgan>> list(String organName) {

        LbqWrapper<BusinessCardOrgan> lbqWrapper = Wraps.<BusinessCardOrgan>lbQ();
        if (StrUtil.isNotEmpty(organName)) {
            lbqWrapper.like(BusinessCardOrgan::getOrganName, organName);
        }
        List<BusinessCardOrgan> organList = businessCardOrganService.list(lbqWrapper);
        return R.success(organList);
    }



}
