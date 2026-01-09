package com.caring.sass.authority.controller.common;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.dto.common.CityPageDTO;
import com.caring.sass.authority.dto.common.CitySaveDTO;
import com.caring.sass.authority.dto.common.CityUpdateDTO;
import com.caring.sass.authority.entity.common.City;
import com.caring.sass.authority.service.common.CityService;
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
@RequestMapping("/city")
@Api(value = "City", tags = "市")
//@PreAuth(replace = "dict:")
public class CityController extends SuperController<CityService, Long, City, CityPageDTO, CitySaveDTO, CityUpdateDTO> {



    @ApiOperation("无授权分页查询")
    @PostMapping("/anno/page")
    public R<IPage<City>> annoPage(@RequestBody PageParams<CityPageDTO> params) {
        return super.page(params);
    }

}
