package com.caring.sass.ai.controller.knows;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.docuSearch.dao.KnowledgeQuestionEvidenceMapper;
import com.caring.sass.ai.docuSearch.service.KnowledgeUserBrowsingHistoryService;
import com.caring.sass.ai.dto.docuSearch.KnowledgeUserBrowsingHistoryPageDTO;
import com.caring.sass.ai.entity.docuSearch.KnowledgeQuestionEvidence;
import com.caring.sass.ai.entity.docuSearch.KnowledgeUserBrowsingHistory;
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


/**
 * <p>
 * 前端控制器
 * 知识库证据浏览记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-17
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeUserBrowsingHistory")
@Api(value = "KnowledgeUserBrowsingHistory", tags = "知识库-知识库证据浏览记录")
public class KnowledgeUserBrowsingHistoryController{


    @Autowired
    KnowledgeUserBrowsingHistoryService knowledgeUserBrowsingHistoryService;

    @Autowired
    KnowledgeQuestionEvidenceMapper evidenceMapper;

    @ApiOperation("删除浏览记录")
    @DeleteMapping
    public R<String> delete(Long id) {

        knowledgeUserBrowsingHistoryService.removeById(id);
        return R.success("success");

    }


    @ApiOperation("查询用户的浏览记录")
    @PostMapping("page")
    public R<IPage<KnowledgeUserBrowsingHistory>> query(@RequestBody  PageParams<KnowledgeUserBrowsingHistoryPageDTO> params) {

        IPage<KnowledgeUserBrowsingHistory> builtPage = params.buildPage();

        LbqWrapper<KnowledgeUserBrowsingHistory> wrapper = Wraps.<KnowledgeUserBrowsingHistory>lbQ()
                .eq(KnowledgeUserBrowsingHistory::getUserId, BaseContextHandler.getUserId());
        wrapper.orderByDesc(KnowledgeUserBrowsingHistory::getViewTime);

        IPage<KnowledgeUserBrowsingHistory> historyIPage = knowledgeUserBrowsingHistoryService.page(builtPage, wrapper);
        for (KnowledgeUserBrowsingHistory record : historyIPage.getRecords()) {
            KnowledgeQuestionEvidence evidence = evidenceMapper.selectById(record.getProblemEvidenceId());
            record.setQuestionEvidence(evidence);
        }


        return R.success(builtPage);

    }





}
