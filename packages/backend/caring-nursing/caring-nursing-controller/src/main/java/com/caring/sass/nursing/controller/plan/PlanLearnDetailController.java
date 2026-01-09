package com.caring.sass.nursing.controller.plan;

import com.caring.sass.nursing.entity.plan.PlanLearnDetail;
import com.caring.sass.nursing.dto.plan.PlanLearnDetailSaveDTO;
import com.caring.sass.nursing.dto.plan.PlanLearnDetailUpdateDTO;
import com.caring.sass.nursing.dto.plan.PlanLearnDetailPageDTO;
import com.caring.sass.nursing.service.plan.PlanLearnDetailService;
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
 * 护理计划-学习
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/planLearnDetail")
@Api(value = "PlanLearnDetail", tags = "护理计划-学习")
@PreAuth(replace = "planLearnDetail:")
public class PlanLearnDetailController extends SuperController<PlanLearnDetailService, Long, PlanLearnDetail, PlanLearnDetailPageDTO, PlanLearnDetailSaveDTO, PlanLearnDetailUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PlanLearnDetail> planLearnDetailList = list.stream().map((map) -> {
            PlanLearnDetail planLearnDetail = PlanLearnDetail.builder().build();
            //TODO 请在这里完成转换
            return planLearnDetail;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(planLearnDetailList));
    }
}
