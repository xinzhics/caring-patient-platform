package com.caring.sass.tenant.controller.router;

import cn.hutool.core.bean.BeanUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.cms.entity.Channel;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.tenant.dto.router.H5UiPageDTO;
import com.caring.sass.tenant.dto.router.H5UiSaveDTO;
import com.caring.sass.tenant.dto.router.H5UiUpdateDTO;
import com.caring.sass.tenant.entity.router.H5Ui;
import com.caring.sass.tenant.service.router.H5UiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * UI组件UI组件UI组件
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/h5Ui")
@Api(value = "H5Ui", tags = "UI组件")
//@PreAuth(replace = "h5Ui:")
public class H5UiController extends SuperController<H5UiService, Long, H5Ui, H5UiPageDTO, H5UiSaveDTO, H5UiUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<H5Ui> h5UiList = list.stream().map((map) -> {
            H5Ui h5Ui = H5Ui.builder().build();
            //TODO 请在这里完成转换
            return h5Ui;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(h5UiList));
    }

    @ApiOperation("根据项目code新增")
    @PostMapping("/save/{code}")
    public R<H5Ui> saveByTenant(@PathVariable("code") String code, @RequestBody @Validated H5UiSaveDTO saveDTO) {
        BaseContextHandler.setTenant(code);
        H5Ui model = BeanUtil.toBean(saveDTO, H5Ui.class);
        baseService.save(model);
        return success(model);
    }

    @ApiOperation(
            value = "根据项目code批量查询",
            notes = "根据项目code批量查询"
    )
    @PostMapping({"/query/{code}"})
    public R<List<H5Ui>> queryByTenant(@PathVariable("code") String code, @RequestBody H5Ui data) {
        BaseContextHandler.setTenant(code);
        QueryWrap<H5Ui> wrapper = Wraps.q(data).orderByAsc("sort_value");
        return success(baseService.list(wrapper));
    }


    @ApiOperation("根据项目code修改")
    @PutMapping("/update/{code}")
    public R<H5Ui> updateByTenant(@PathVariable("code") String code, @RequestBody @Validated H5UiUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(code);
        H5Ui model = BeanUtil.toBean(updateDTO, H5Ui.class);
        baseService.updateById(model);
        return success(model);
    }

    @ApiOperation("匿名查询项目UI")
    @GetMapping("/anno/query/{code}")
    public R<List<H5Ui>> anonQuery(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Ui> h5Uis = baseService.list(Wraps.<H5Ui>lbQ().orderByAsc(H5Ui::getSortValue));
        return R.success(h5Uis);
    }

    @ApiOperation("匿名查询项目UI以key-value形式")
    @GetMapping("/anno/mapQuery/{code}")
    public R<Map<String, H5Ui>> anonMapQuery(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Ui> h5Uis = baseService.list(Wraps.<H5Ui>lbQ().orderByAsc(H5Ui::getSortValue));
        Map<String, H5Ui> map = h5Uis.stream()
                .collect(Collectors.toMap(H5Ui::getCode, a -> a, (o, n) -> o));
        return R.success(map);
    }

    @ApiOperation("查询项目UI不存在，则创建")
    @GetMapping("/createIfNotExist/{code}")
    public R<List<H5Ui>> createIfNotExist(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Ui> h5Uis = baseService.createIfNotExist();
        return R.success(h5Uis);
    }

    @ApiOperation("重置项目UI")
    @GetMapping("/resetUI/{code}")
    public R<List<H5Ui>> resetUI(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Ui> h5Uis = baseService.resetUI();
        return R.success(h5Uis);
    }
}
