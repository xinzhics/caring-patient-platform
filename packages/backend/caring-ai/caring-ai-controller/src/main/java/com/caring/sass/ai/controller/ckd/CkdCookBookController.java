package com.caring.sass.ai.controller.ckd;

import com.caring.sass.ai.ckd.server.CkdCookBookService;
import com.caring.sass.ai.dto.ckd.CkdCookBookUpdateDTO;
import com.caring.sass.ai.entity.ckd.CkdCookBook;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdCookBook")
@Api(value = "CkdCookBook", tags = "用户食谱")
public class CkdCookBookController {

    @Autowired
    CkdCookBookService ckdCookBookService;

    @ApiOperation("根据cookbookId查询一个食谱")
    @GetMapping(value = "/{cookbookId}")
    public R<CkdCookBook> findCookBookById(@PathVariable Long cookbookId) {
        CkdCookBook cookBook = ckdCookBookService.getById(cookbookId);
        return R.success(cookBook);
    }

    @ApiOperation("上传食谱的图片")
    @PostMapping(value = "uploadImage")
    public R<CkdCookBook> uploadImage(@PathVariable CkdCookBookUpdateDTO ckdCookBookUpdateDTO) {
        CkdCookBook cookBook = ckdCookBookService.uploadImage(ckdCookBookUpdateDTO);
        return R.success(cookBook);
    }

}
