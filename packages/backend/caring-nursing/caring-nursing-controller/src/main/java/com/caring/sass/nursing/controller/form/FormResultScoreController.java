package com.caring.sass.nursing.controller.form;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.nursing.dto.form.FormResultScorePageDTO;
import com.caring.sass.nursing.dto.form.FormResultScoreSaveDTO;
import com.caring.sass.nursing.dto.form.FormResultScoreUpdateDTO;
import com.caring.sass.nursing.entity.form.FormResultScore;
import com.caring.sass.nursing.service.form.FormResultScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 表单结果的成绩计算
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/formResultScore")
@Api(value = "FormResultScore", tags = "表单结果的成绩计算")
public class FormResultScoreController extends SuperController<FormResultScoreService, Long, FormResultScore, FormResultScorePageDTO, FormResultScoreSaveDTO, FormResultScoreUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FormResultScore> formResultScoreList = list.stream().map((map) -> {
            FormResultScore formResultScore = FormResultScore.builder().build();
            //TODO 请在这里完成转换
            return formResultScore;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(formResultScoreList));
    }



    @GetMapping("queryFormResultScore")
    @ApiOperation("表单提交后。查询表单的成绩结果")
    public R<FormResultScore> queryFormResultScore(@RequestParam Long formResultId) {
        FormResultScore resultScore = baseService.queryFormResultScore(formResultId);
        return R.success(resultScore);
    }


}
