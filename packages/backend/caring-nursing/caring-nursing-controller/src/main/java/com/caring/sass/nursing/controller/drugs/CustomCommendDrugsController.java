package com.caring.sass.nursing.controller.drugs;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsSaveDTO;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.CustomCommendDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.service.drugs.CustomCommendDrugsService;
import com.caring.sass.nursing.service.drugs.SysDrugsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 自定义推荐药品
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/customCommendDrugs")
@Api(value = "CustomCommendDrugs", tags = "自定义推荐药品")
//@PreAuth(replace = "customCommendDrugs:")
public class CustomCommendDrugsController extends SuperController<CustomCommendDrugsService, Long, CustomCommendDrugs, CustomCommendDrugsPageDTO, CustomCommendDrugsSaveDTO, CustomCommendDrugsUpdateDTO> {

    @Autowired
    private SysDrugsService sysDrugsService;

    @PostMapping(value = "addRecommendDrugs")
    public R<Boolean> addRecommendDrugs(@RequestParam(value = "drugsId") @NotNull(message = "药品id不能为空") Long drugsId) {
        SysDrugs d = sysDrugsService.getById(drugsId);
        // 是否存在用药配置
        CustomCommendDrugs commendDrugs = CustomCommendDrugs.builder()
                .drugsId(drugsId).number(d.getNumber())
                .dosage(d.getDose()).time(d.getTime())
                .cycle(d.getCycle())
                .build();
        baseService.saveOrUpdate(commendDrugs, Wraps.<CustomCommendDrugs>lbQ().eq(CustomCommendDrugs::getDrugsId, drugsId));
        return R.success();
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<CustomCommendDrugs> customCommendDrugsList = list.stream().map((map) -> {
            CustomCommendDrugs customCommendDrugs = CustomCommendDrugs.builder().build();
            //TODO 请在这里完成转换
            return customCommendDrugs;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(customCommendDrugsList));
    }

    @DeleteMapping(value = "delRecommendDrugs")
    R<Boolean> delRecommendDrugs(@RequestParam(value = "drugsId") Long drugsId) {
        baseService.remove(Wraps.<CustomCommendDrugs>lbQ().eq(CustomCommendDrugs::getDrugsId, drugsId));
        return R.success();
    }

    @ApiOperation("复制推荐用药")
    @PostMapping("copyRecommendDrugs")
    public R<Boolean> copyRecommendDrugs(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                                        @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode) {
        baseService.copyRecommendDrugs(fromTenantCode, toTenantCode);
        return R.success();
    }
}
