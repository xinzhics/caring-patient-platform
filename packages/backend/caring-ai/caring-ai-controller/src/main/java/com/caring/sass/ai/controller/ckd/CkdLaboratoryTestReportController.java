package com.caring.sass.ai.controller.ckd;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.ckd.server.CkdLaboratoryTestReportService;
import com.caring.sass.ai.dto.ckd.CkdLaboratoryTestReportPageDTO;
import com.caring.sass.ai.dto.ckd.CkdLaboratoryTestReportSaveDTO;
import com.caring.sass.ai.entity.article.ArticlePodcastJoin;
import com.caring.sass.ai.entity.ckd.CkdLaboratoryTestReport;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * <p>
 * 前端控制器
 * ckd用户化验单
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdLaboratoryTestReport")
@Api(value = "CkdLaboratoryTestReport", tags = "ckd用户化验单")
public class CkdLaboratoryTestReportController {

    @Autowired
    CkdLaboratoryTestReportService ckdLaboratoryTestReportService;


    @ApiOperation("提交用户的化验单")
    @PostMapping("submitLaboratoryTestReport")
    public R<CkdLaboratoryTestReport> submitLaboratoryTestReport(@RequestBody @Validated CkdLaboratoryTestReportSaveDTO ckdLaboratoryTestReportSaveDTO) {

       CkdLaboratoryTestReport ckdLaboratoryTestReport = ckdLaboratoryTestReportService.submitLaboratoryTestReport(ckdLaboratoryTestReportSaveDTO);
       return R.success(ckdLaboratoryTestReport);

    }


    @ApiOperation("我的化验单列表")
    @PostMapping("page")
    public R<IPage<CkdLaboratoryTestReport>> page(@RequestBody @Validated PageParams<CkdLaboratoryTestReportPageDTO> params) {

        IPage<CkdLaboratoryTestReport> buildPage = params.buildPage();
        CkdLaboratoryTestReportPageDTO model = params.getModel();
        String openId = model.getOpenId();
        if (StrUtil.isEmpty(openId)) {
            throw new RuntimeException("openId不能为空");
        }
        LbqWrapper<CkdLaboratoryTestReport> wrapper = Wraps.<CkdLaboratoryTestReport>lbQ()
                .eq(CkdLaboratoryTestReport::getOpenId, openId)
                .orderByDesc(CkdLaboratoryTestReport::getCreateTime);
        ckdLaboratoryTestReportService.page(buildPage, wrapper);
        return R.success(buildPage);
    }


    @ApiOperation("删除用户化验单")
    @DeleteMapping("delete/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean b = ckdLaboratoryTestReportService.removeById(id);
        return R.success(b);
    }


}
