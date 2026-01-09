package com.caring.sass.authority.controller.common;


import com.caring.sass.authority.dto.common.AreaPageDTO;
import com.caring.sass.authority.dto.common.AreaSaveDTO;
import com.caring.sass.authority.dto.common.AreaUpdateDTO;
import com.caring.sass.authority.entity.common.Area;
import com.caring.sass.authority.service.common.AreaService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperCacheController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * 地区表
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Deprecated
@Validated
@RestController
@RequestMapping("/area")
@Api(value = "Area", tags = "地区表")
@PreAuth(replace = "area:")
public class AreaController extends SuperCacheController<AreaService, Long, Area, AreaPageDTO, AreaSaveDTO, AreaUpdateDTO> {

    @ApiOperation(value = "检测地区编码是否重复", notes = "检测地区编码是否重复")
    @GetMapping("/check/{code}")
    @SysLog("检测地区编码是否重复")
    public R<Boolean> check(@RequestParam(required = false) Long id, @PathVariable String code) {
        int count = baseService.count(Wraps.<Area>lbQ().eq(Area::getCode, code).ne(Area::getId, id));
        return success(count > 0);
    }


    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        //TODO 重点测试递归删除
        return R.success(baseService.recursively(ids));
    }

    /**
     * 级联查询缓存中的地区
     *
     * @param parentId 父ID
     * @return 查询结果
     */
    @ApiOperation(value = "级联查询缓存中的地区", notes = "级联查询缓存中的地区")
    @GetMapping("/linkage")
    @SysLog("级联查询地区")
    public R<List<Area>> linkageQuery(@RequestParam(defaultValue = "0", required = false) Long parentId) {
        //TODO 想办法做缓存
        LbqWrapper<Area> query = Wraps.<Area>lbQ()
                .eq(Area::getParentId, parentId)
                .orderByAsc(Area::getSortValue);
        return success(baseService.list(query));
    }

}
