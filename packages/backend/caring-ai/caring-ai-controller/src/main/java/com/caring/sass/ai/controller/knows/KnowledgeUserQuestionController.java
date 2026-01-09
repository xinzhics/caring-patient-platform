package com.caring.sass.ai.controller.knows;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.docuSearch.service.KnowledgeQuestionEvidenceService;
import com.caring.sass.ai.docuSearch.service.KnowledgeUserBrowsingHistoryService;
import com.caring.sass.ai.docuSearch.service.KnowledgeUserQuestionService;
import com.caring.sass.ai.dto.docuSearch.KnowledgeUserQuestionPageDTO;
import com.caring.sass.ai.entity.docuSearch.KnowledgeQuestionEvidence;
import com.caring.sass.ai.entity.docuSearch.KnowledgeUserBrowsingHistory;
import com.caring.sass.ai.entity.docuSearch.KnowledgeUserQuestion;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * 知识库用户问题
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-17
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeUserQuestion")
@Api(value = "KnowledgeUserQuestion", tags = "知识库-知识库用户问题")
public class KnowledgeUserQuestionController {

    @Autowired
    KnowledgeUserQuestionService knowledgeUserQuestionService;

    @Autowired
    KnowledgeUserBrowsingHistoryService knowledgeUserBrowsingHistoryService;

    @Autowired
    KnowledgeQuestionEvidenceService questionEvidenceService;

    // 用户提出问题，并和服务器建立see连接
    @ApiOperation("提问题并连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户see连接", required = true),
            @ApiImplicitParam(name = "question", value = "用户问题", required = true),
            @ApiImplicitParam(name = "conversation", value = "会话，一个会话可以有多个问题(字段前端自己生成)", required = true)
    })
    @GetMapping(value = "submitQuestion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter submitQuestion(@RequestParam String uid,
                                     @RequestParam String question,
                                     @RequestParam String conversation) {

        return knowledgeUserQuestionService.submitQuestion(uid, question, conversation);

    }


    @ApiOperation("创建连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户see连接", required = true),
    })
    @GetMapping(value = "createSse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createSse(@RequestParam String uid) {

        return knowledgeUserQuestionService.createSse(uid);

    }

    @ApiOperation("保存问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户see连接", required = true),
            @ApiImplicitParam(name = "question", value = "用户问题", required = true),
            @ApiImplicitParam(name = "conversation", value = "会话，一个会话可以有多个问题(字段前端自己生成)", required = true)
    })
    @GetMapping("saveQuestion")
    public R<KnowledgeUserQuestion> saveQuestion(
            @RequestParam String uid,
            @RequestParam String question,
            @RequestParam String conversation) {
        KnowledgeUserQuestion userQuestion = knowledgeUserQuestionService.saveQuestion(question, conversation, uid);
        return R.success(userQuestion);

    }


    @ApiOperation("取消响应")
    @GetMapping("cancel")
    public R<String> cancelQuestion(@RequestParam String uid) {


        knowledgeUserQuestionService.cancelQuestion(uid);
        return R.success("success");

    }



    @ApiOperation("删除一个会话组")
    @DeleteMapping("conversation")
    public R<Boolean> delete(@RequestParam String conversation) {

        boolean remove = knowledgeUserQuestionService.remove(Wraps.<KnowledgeUserQuestion>lbQ()
                .eq(KnowledgeUserQuestion::getUserId, BaseContextHandler.getUserId())
                .eq(KnowledgeUserQuestion::getConversation, conversation));
        return R.success(remove);
    }


    @ApiOperation("删除全部会话")
    @DeleteMapping("all")
    public R<Boolean> delete() {
        boolean remove = knowledgeUserQuestionService.remove(Wraps.<KnowledgeUserQuestion>lbQ()
                .eq(KnowledgeUserQuestion::getUserId, BaseContextHandler.getUserId()));
        return R.success(remove);
    }

    @ApiOperation("查询用户的历史问答")
    @PostMapping("page")
    public R<IPage<KnowledgeUserQuestion>> query(@RequestBody PageParams<KnowledgeUserQuestionPageDTO> params) {

        IPage<KnowledgeUserQuestion> builtPage = params.buildPage();

        LbqWrapper<KnowledgeUserQuestion> wrapper = Wraps.<KnowledgeUserQuestion>lbQ()
                .eq(KnowledgeUserQuestion::getIsNewQuestion, 1)
                .eq(KnowledgeUserQuestion::getUserId, BaseContextHandler.getUserId());
        wrapper.orderByDesc(KnowledgeUserQuestion::getCreateTime);

        knowledgeUserQuestionService.page(builtPage, wrapper);
        return R.success(builtPage);

    }


    @ApiOperation("查询一个会话组中的问题")
    @GetMapping("queryList")
    public R<List<KnowledgeUserQuestion>> queryList(@RequestParam String conversation) {

        LbqWrapper<KnowledgeUserQuestion> lbqWrapper = Wraps.<KnowledgeUserQuestion>lbQ().eq(KnowledgeUserQuestion::getConversation, conversation)
                .eq(KnowledgeUserQuestion::getUserId, BaseContextHandler.getUserId())
                .orderByAsc(SuperEntity::getCreateTime);

        List<KnowledgeUserQuestion> userQuestions = knowledgeUserQuestionService.list(lbqWrapper);
        // 查询问题得到的证据信息。
        for (KnowledgeUserQuestion question : userQuestions) {
            List<KnowledgeQuestionEvidence> evidences = questionEvidenceService.list(Wraps.<KnowledgeQuestionEvidence>lbQ()
                    .orderByAsc(SuperEntity::getCreateTime)
                    .eq(KnowledgeQuestionEvidence::getQuestionId, question.getId()));
            question.setQuestionEvidences(evidences);

        }
        return R.success(userQuestions);

    }


    @ApiOperation("查询一个证据详情")
    @GetMapping
    public R<KnowledgeQuestionEvidence> getEvidenceDetail(@RequestParam Long evidenceId,
                                                          @RequestParam(value = "createHistory", defaultValue = "true", required = false) Boolean createHistory) {

        if (createHistory) {
            KnowledgeUserBrowsingHistory browsingHistory = knowledgeUserBrowsingHistoryService.getOne(Wraps.<KnowledgeUserBrowsingHistory>lbQ()
                    .eq(KnowledgeUserBrowsingHistory::getUserId, BaseContextHandler.getUserId())
                    .eq(KnowledgeUserBrowsingHistory::getProblemEvidenceId, evidenceId)
                    .last(" limit 1 "));
            if (browsingHistory == null) {
                browsingHistory = new KnowledgeUserBrowsingHistory();
                browsingHistory.setUserId(BaseContextHandler.getUserId());
                browsingHistory.setViewTime(LocalDateTime.now());
                browsingHistory.setProblemEvidenceId(evidenceId);
                knowledgeUserBrowsingHistoryService.save(browsingHistory);
            } else {
                browsingHistory.setViewTime(LocalDateTime.now());
                knowledgeUserBrowsingHistoryService.updateById(browsingHistory);
            }
        }
        KnowledgeQuestionEvidence evidence = questionEvidenceService.getById(evidenceId);
        return R.success(evidence);

    }





}
