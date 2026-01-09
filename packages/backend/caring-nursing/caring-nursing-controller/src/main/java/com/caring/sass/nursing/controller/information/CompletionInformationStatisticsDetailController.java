package com.caring.sass.nursing.controller.information;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.nursing.dto.information.CompletionInformationStatisticsDetailPageDTO;
import com.caring.sass.nursing.dto.information.CompletionInformationStatisticsDetailSaveDTO;
import com.caring.sass.nursing.dto.information.CompletionInformationStatisticsDetailUpdateDTO;
import com.caring.sass.nursing.entity.information.CompletionInformationStatisticsDetail;
import com.caring.sass.nursing.service.information.CompletionInformationStatisticsDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 信息完整度区间统计详细表
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/completionInformationStatisticsDetail")
@Api(value = "CompletionInformationStatisticsDetail", tags = "信息完整度区间统计详细表")
//@PreAuth(replace = "completionInformationStatisticsDetail:")
public class CompletionInformationStatisticsDetailController extends SuperController<CompletionInformationStatisticsDetailService, Long, CompletionInformationStatisticsDetail, CompletionInformationStatisticsDetailPageDTO, CompletionInformationStatisticsDetailSaveDTO, CompletionInformationStatisticsDetailUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<CompletionInformationStatisticsDetail> completionInformationStatisticsDetailList = list.stream().map((map) -> {
            CompletionInformationStatisticsDetail completionInformationStatisticsDetail = CompletionInformationStatisticsDetail.builder().build();
            //TODO 请在这里完成转换
            return completionInformationStatisticsDetail;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(completionInformationStatisticsDetailList));
    }

    @ApiOperation(value = "统计详细信息，完整度占比")
    @GetMapping
    public R<List<CompletionInformationStatisticsDetail>> collection(@RequestParam("date") String datetime,
                                                                     @RequestParam("statisticsId") Long statisticsId) {
        CompletionInformationStatisticsDetail statisticsDetail = new CompletionInformationStatisticsDetail();
        statisticsDetail.setCreateTime(LocalDateTimeUtil.parse(datetime));
        statisticsDetail.setStatisticsId(statisticsId);
        QueryWrap<CompletionInformationStatisticsDetail> wrap = Wraps.q(statisticsDetail);
        List<CompletionInformationStatisticsDetail> list = baseService.list(wrap);
        return R.success(list);
    }
}
