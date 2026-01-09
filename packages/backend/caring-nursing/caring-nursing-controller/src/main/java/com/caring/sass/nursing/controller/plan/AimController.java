package com.caring.sass.nursing.controller.plan;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.plan.AimPageDTO;
import com.caring.sass.nursing.dto.plan.AimSaveDTO;
import com.caring.sass.nursing.dto.plan.AimUpdateDTO;
import com.caring.sass.nursing.entity.plan.Aim;
import com.caring.sass.nursing.service.plan.AimService;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/aim")
@Api(value = "Aim", tags = "护理目标")
@PreAuth(replace = "aim:")
public class AimController extends SuperController<AimService, Long, Aim, AimPageDTO, AimSaveDTO, AimUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<Aim> aimList = list.stream().map((map) -> {
            Aim aim = Aim.builder().build();
            //TODO 请在这里完成转换
            return aim;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(aimList));
    }

    @ApiOperation("获取aim设置")
    @GetMapping("getaim/{tenantCode}")
    public R<Aim> getAim(@PathVariable String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<Aim> aimLbqWrapper = new LbqWrapper<>();
        aimLbqWrapper.eq(Aim::getIsDefault, 1);
        Aim aim = baseService.getOne(aimLbqWrapper);
        return R.success(aim);

    }

    @ApiOperation("获取aim设置")
    @PostMapping("saveOrUpdateDefault/{tenantCode}")
    public R<Aim> save(@PathVariable String tenantCode, @RequestBody Aim aim) {

        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<Aim> aimLbqWrapper = new LbqWrapper<>();
        aimLbqWrapper.eq(Aim::getIsDefault, 1);
        Aim oldAim = baseService.getOne(aimLbqWrapper);
        if (oldAim != null) {
            oldAim.setUrl(aim.getUrl());
            baseService.updateById(oldAim);
        } else {
            aim.setIsDefault(1);
            aim.setName("默认目标");
            baseService.save(aim);
        }
        return R.success(aim);

    }


}
