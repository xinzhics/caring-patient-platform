package com.caring.sass.tenant.controller.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.dao.GlobalUserTenantMapper;
import com.caring.sass.tenant.dto.StatisticsTenantPageDTO;
import com.caring.sass.tenant.dto.StatisticsTenantSaveDTO;
import com.caring.sass.tenant.dto.StatisticsTenantUpdateDTO;
import com.caring.sass.tenant.entity.StatisticsTenant;
import com.caring.sass.tenant.service.StatisticsTenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 项目统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/statisticsTenant")
@Api(value = "StatisticsTenant", tags = "项目统计")
//@PreAuth(replace = "statisticsTenant:")
public class StatisticsTenantController extends SuperController<StatisticsTenantService, Long, StatisticsTenant, StatisticsTenantPageDTO, StatisticsTenantSaveDTO, StatisticsTenantUpdateDTO> {

    @Autowired
    GlobalUserTenantMapper globalUserTenantMapper;
    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<StatisticsTenant> statisticsTenantList = list.stream().map((map) -> {
            StatisticsTenant statisticsTenant = StatisticsTenant.builder().build();
            //TODO 请在这里完成转换
            return statisticsTenant;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(statisticsTenantList));
    }

    @Override
    public R<IPage<StatisticsTenant>> page(PageParams<StatisticsTenantPageDTO> params) {

        IPage<StatisticsTenant> iPage = params.buildPage();
        StatisticsTenantPageDTO model = params.getModel();
        baseService.page(iPage, model);
        return R.success(iPage);
    }


    /**
     * 查询所有用户数
     */
    @ApiOperation(value = "查询所有用户数", notes = "查询所有用户数")
    @GetMapping("/queryMemberNum")
    public R<Map<String, Long>> queryMemberNum() {
        Map<String, Long> ret = baseService.queryMemberNum();
        return R.success(ret);
    }

}
