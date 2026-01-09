package com.caring.sass.ai.controller.ckd;

import com.caring.sass.ai.ckd.server.CkdUserCozeTokenService;
import com.caring.sass.ai.dto.ckd.CkdUserCozeTokenSaveDTO;
import com.caring.sass.ai.entity.ckd.CkdUserCozeToken;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * CKD用户coze的token
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-17
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdUserCozeToken")
@Api(value = "CkdUserCozeToken", tags = "CKD用户coze的token")
public class CkdUserCozeTokenController {


    @Autowired
    private CkdUserCozeTokenService ckdUserCozeTokenService;


    @ApiOperation("查询或创建token")
    @PostMapping("/queryOrCreateToken")
    public R<CkdUserCozeToken> queryOrCreateToken(@RequestBody @Validated CkdUserCozeTokenSaveDTO saveDTO) {

        CkdUserCozeToken cozeToken = ckdUserCozeTokenService.queryOrCreateToken(saveDTO);
        return R.success(cozeToken);

    }



}
