package com.caring.sass.tenant.controller;

import com.caring.sass.base.R;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import com.caring.sass.tenant.utils.SequenceNumUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sequenceNum")
@Api(value = "sequenceNum", tags = "序号生成器")
public class SequenceNumController {


    @ApiOperation("生成不重复递增的序号")
    @GetMapping
    public R<String> generate(@RequestParam(value = "sequenceEnum") SequenceEnum sequenceEnum,
                              @RequestParam(value = "length", defaultValue = "4") Integer length) {
        String r = SequenceNumUtil.incrSequenceNum(sequenceEnum, length);
        return R.success(r);
    }
}
