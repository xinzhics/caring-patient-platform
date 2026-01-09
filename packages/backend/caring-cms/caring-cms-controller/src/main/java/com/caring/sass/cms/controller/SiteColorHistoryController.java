package com.caring.sass.cms.controller;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dto.site.SiteColorHistoryPageDTO;
import com.caring.sass.cms.dto.site.SiteColorHistorySaveDTO;
import com.caring.sass.cms.dto.site.SiteColorHistoryUpdateDTO;
import com.caring.sass.cms.entity.SiteColorHistory;
import com.caring.sass.cms.service.SiteColorHistoryService;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
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
 * 建站最近使用颜色
 * </p>
 *
 * @author 杨帅
 * @date 2023-11-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteColorHistory")
@Api(value = "SiteColorHistory", tags = "建站最近使用颜色")
public class SiteColorHistoryController extends SuperController<SiteColorHistoryService, Long, SiteColorHistory, SiteColorHistoryPageDTO,
        SiteColorHistorySaveDTO, SiteColorHistoryUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteColorHistory> siteColorHistoryList = list.stream().map((map) -> {
            SiteColorHistory siteColorHistory = SiteColorHistory.builder().build();
            //TODO 请在这里完成转换
            return siteColorHistory;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteColorHistoryList));
    }

    @ApiOperation("查询用户颜色使用历史")
    @GetMapping("queryColorHistory")
    public R<List<SiteColorHistory>> queryColorHistory() {

        Long userId = BaseContextHandler.getUserId();
        LbqWrapper<SiteColorHistory> wrapper = Wraps.<SiteColorHistory>lbQ().eq(SuperEntity::getCreateUser, userId).orderByDesc(SuperEntity::getCreateTime);
        List<SiteColorHistory> histories = baseService.list(wrapper);
        return R.success(histories);
    }

}
