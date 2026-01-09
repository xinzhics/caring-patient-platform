package com.caring.sass.ai.controller.knows;

import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.service.KnowledgeService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * dify知识库关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledge")
@Api(value = "Knowledge", tags = "知识库-dify知识库关联表")
public class KnowledgeController {


    @Autowired
    KnowledgeService knowledgeService;


    @Autowired
    KnowledgeUserService knowledgeUserService;


    @ApiOperation(value = "初始化现有知识库的元数据")
    @GetMapping("initKnowledgeMetadata")
    public R<Boolean> initKnowledgeMetadata() {
        knowledgeService.initKnowledgeMetadata();
        return R.success(true);
    }




    @ApiOperation(value = "查询知识库列表")
    @GetMapping("knowledgeList/{domain}")
    public R<List<Knowledge>> knowledgeList(@PathVariable("domain") String domain) {

        KnowledgeUser user = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ().eq(KnowledgeUser::getUserDomain, domain)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last("limit 1"));
        if (user == null) {
            throw new BizException("域名错误");
        }
        Long userId = user.getId();

        List<Knowledge> knowledgeList = knowledgeService.list(Wraps.<Knowledge>lbQ().eq(Knowledge::getKnowUserId, userId).orderByAsc(SuperEntity::getCreateTime));
        return R.success(knowledgeList);
    }


    @ApiOperation(value = "修改知识库权限")
    @PutMapping("update")
    public R<Boolean> knowledgeDetail(@RequestBody @Validated List<Knowledge> knowledgeList) {

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser user = knowledgeUserService.getById(userId);
        if (user.getUserType().equals(KnowDoctorType.GENERAL_PRACTITIONER)) {
            throw new BizException("权限不足");
        }

        List<Knowledge> oldKnowList = knowledgeService.list(Wraps.<Knowledge>lbQ().eq(Knowledge::getKnowUserId, userId));
        knowledgeService.updatePermission(knowledgeList, oldKnowList);
        return R.success(true);
    }



}
