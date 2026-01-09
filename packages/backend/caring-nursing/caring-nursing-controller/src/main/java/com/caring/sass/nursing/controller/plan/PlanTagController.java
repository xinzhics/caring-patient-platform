package com.caring.sass.nursing.controller.plan;

import com.caring.sass.nursing.entity.plan.PlanTag;
import com.caring.sass.nursing.dto.plan.PlanTagSaveDTO;
import com.caring.sass.nursing.dto.plan.PlanTagUpdateDTO;
import com.caring.sass.nursing.dto.plan.PlanTagPageDTO;
import com.caring.sass.nursing.service.plan.PlanTagService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/planTag")
@Api(value = "PlanTag", tags = "")
@PreAuth(replace = "planTag:")
public class PlanTagController extends SuperController<PlanTagService, Long, PlanTag, PlanTagPageDTO, PlanTagSaveDTO, PlanTagUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PlanTag> planTagList = list.stream().map((map) -> {
            PlanTag planTag = PlanTag.builder().build();
            //TODO 请在这里完成转换
            return planTag;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(planTagList));
    }
}
