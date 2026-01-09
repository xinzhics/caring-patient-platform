package com.caring.sass.ai.controller.ckd;


import com.caring.sass.ai.ckd.server.CkdUserGfrService;
import com.caring.sass.ai.dto.ckd.CkdGfrTrend;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 用户GFR
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdUserGfr")
@Api(value = "CkdUserGfr", tags = "用户GFR")
public class CkdUserGfrController {


    @Autowired
    private CkdUserGfrService ckdUserGfrService;

    @ApiOperation("用户GFR趋势图")
    @GetMapping("getGfrTrend")
    public R<CkdGfrTrend> getGfrTrend(@RequestParam String openId) {
        CkdGfrTrend ckdGfrTrend = ckdUserGfrService.getGfrTrend(openId);
        return R.success(ckdGfrTrend);
    }


}
