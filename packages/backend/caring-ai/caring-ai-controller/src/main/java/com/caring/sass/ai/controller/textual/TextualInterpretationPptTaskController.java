package com.caring.sass.ai.controller.textual;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.textual.*;
import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.ai.entity.textual.TextualInterpretationTextTask;
import com.caring.sass.ai.textual.service.TextualInterpretationPptTaskService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 文献解读PPT
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/textualInterpretationPptTask")
@Api(value = "TextualInterpretationPptTask", tags = "文献解读PPT")
public class TextualInterpretationPptTaskController {

    @Autowired
    TextualInterpretationPptTaskService pptTaskService;

    @ApiOperation("保存文献解读任务")
    @PostMapping()
    public R<TextualInterpretationPptTask> addPodcast(@RequestBody @Validated TextualInterpretationPptTaskSaveDTO pptTaskSaveDTO) {

        TextualInterpretationPptTask textTask = new TextualInterpretationPptTask();

        BeanUtils.copyProperties(pptTaskSaveDTO, textTask);

        pptTaskService.save(textTask);

        return R.success(textTask);

    }


    @ApiOperation("重新生成大纲")
    @PostMapping("/{id}")
    public R<Boolean> reCreatePptOutline(@PathVariable Long id) {

        pptTaskService.reCreatePptOutline(id);

        return R.success(true);

    }


    @ApiOperation("提交大纲")
    @PutMapping()
    public R<TextualInterpretationPptTask> submit(@RequestBody @Validated TextualInterpretationPptTaskUpdateDTO pptTaskUpdateDTO) {

        TextualInterpretationPptTask pptTask = new TextualInterpretationPptTask();

        BeanUtils.copyProperties(pptTaskUpdateDTO, pptTask);
        pptTask.setStep(2);
        pptTask.setTaskStatus(0);
        pptTaskService.updateById(pptTask);

        return R.success(pptTask);

    }

    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestParam(name = "uid", required = true) String uid, @RequestParam(name = "id", required = true) Long id) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("会话ID不存在");
        }
        return pptTaskService.createSse(uid, id);
    }



    @ApiOperation("开始ppt制作")
    @PutMapping("startPpt/{id}")
    public R<Boolean> startPpt(
            @PathVariable("id") Long id, @RequestParam(name = "uid", required = false) String uid) {

        pptTaskService.startPpt(id, uid);

        return R.success(true);

    }

    @ApiOperation("保存到作品集")
    @PostMapping("/saveDraft/{id}")
    public R<String> saveDraft(@PathVariable("id") Long id) {

        TextualInterpretationPptTask task = new TextualInterpretationPptTask();
        task.setId(id);
        task.setTaskStatus(2);
        pptTaskService.updateById(task);
        return R.success("success");

    }


    @ApiOperation("文件解读分页列表")
    @PostMapping("draftPage")
    public R<IPage<TextualInterpretationPptTask>> draftPage(@RequestBody @Validated PageParams<TextualInterpretationPptTaskPageDTO> params) {

        IPage<TextualInterpretationPptTask> buildPage = params.buildPage();
        TextualInterpretationPptTaskPageDTO model = params.getModel();
        LbqWrapper<TextualInterpretationPptTask> wrapper = Wraps.<TextualInterpretationPptTask>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(SuperEntity::getCreateUser, model.getUserId())
                .eq(TextualInterpretationPptTask::getTaskStatus, model.getTaskStatus());
        if (model.getShowAIKnowledge() != null) {
            wrapper.eq(TextualInterpretationPptTask::getShowAIKnowledge, model.getShowAIKnowledge());
        }
        if (StrUtil.isNotBlank(model.getTitle())) {
            wrapper.and(
                    wrapper1 -> wrapper1.like(TextualInterpretationPptTask::getTitle, model.getTitle())
                            .or(
                                    wrapper2 -> wrapper2.like(TextualInterpretationPptTask::getFileName, model.getTitle())
                                            .isNull(TextualInterpretationPptTask::getTitle)
                            )

            );
        }
        pptTaskService.page(buildPage, wrapper);
        return R.success(buildPage);
    }


    @ApiOperation("发布在医生数字分身平台")
    @PutMapping("/showInAIKnowledge")
    public R<Boolean> showInAIKnowledge(@RequestBody List<Long> ids) {
        pptTaskService.update(new TextualInterpretationPptTask(), new UpdateWrapper<TextualInterpretationPptTask>()
                .in("id", ids)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }



    @ApiOperation("隐藏在医生数字分身平台")
    @PutMapping("/hideInAIKnowledge/{id}")
    public R<Boolean> hideInAIKnowledge(@PathVariable("id") Long id) {
        pptTaskService.update(new TextualInterpretationPptTask(), new UpdateWrapper<TextualInterpretationPptTask>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }


    @ApiOperation("删除文献解读任务")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {

        pptTaskService.removeById(id);

        return R.success(true);

    }


    @ApiOperation("查询详情")
    @GetMapping("/{id}")
    public R<TextualInterpretationPptTask> get(@PathVariable("id") Long id) {

        TextualInterpretationPptTask pptTask = pptTaskService.getById(id);

        return R.success(pptTask);

    }





}
