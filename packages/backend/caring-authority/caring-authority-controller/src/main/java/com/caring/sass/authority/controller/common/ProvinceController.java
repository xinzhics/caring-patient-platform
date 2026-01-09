package com.caring.sass.authority.controller.common;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.dto.common.ProvincePageDTO;
import com.caring.sass.authority.dto.common.ProvinceSaveDTO;
import com.caring.sass.authority.dto.common.ProvinceUpdateDTO;
import com.caring.sass.authority.entity.common.Province;
import com.caring.sass.authority.service.common.ProvinceService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * 字典项
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/province")
@Api(value = "Province", tags = "省")
//@PreAuth(replace = "dict:")
public class ProvinceController extends SuperController<ProvinceService, Long, Province, ProvincePageDTO, ProvinceSaveDTO, ProvinceUpdateDTO> {



    @ApiOperation("无授权访问省数据")
    @PostMapping("anno/query")
    public R<List<Province>> annoQuery(@RequestBody Province data) {
        return super.query(data);
    }



    @ApiOperation("无授权访问省数据")
    @PostMapping("anno/page")
    public R<IPage<Province>> anno(PageParams<ProvincePageDTO> params) {
        return super.page(params);
    }
}
