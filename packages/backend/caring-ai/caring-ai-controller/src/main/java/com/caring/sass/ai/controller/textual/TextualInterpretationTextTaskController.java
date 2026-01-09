package com.caring.sass.ai.controller.textual;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.textual.TextualInterpretationTextTaskPageDTO;
import com.caring.sass.ai.dto.textual.TextualInterpretationTextTaskSaveDTO;
import com.caring.sass.ai.dto.textual.TextualInterpretationTextTaskUpdateDTO;
import com.caring.sass.ai.entity.textual.TextualInterpretationTextTask;
import com.caring.sass.ai.textual.service.TextualInterpretationTextTaskService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 文献解读txt
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/textualInterpretationTextTask")
@Api(value = "TextualInterpretationTextTask", tags = "文献解读txt")
public class TextualInterpretationTextTaskController {

    @Autowired
    TextualInterpretationTextTaskService textualInterpretationTextTaskService;

    @ApiOperation("保存文献解读任务")
    @PostMapping()
    public R<TextualInterpretationTextTask> addPodcast(@RequestBody @Validated TextualInterpretationTextTaskSaveDTO textTaskSaveDTO) {

        TextualInterpretationTextTask textTask = new TextualInterpretationTextTask();

        BeanUtils.copyProperties(textTaskSaveDTO, textTask);

        textualInterpretationTextTaskService.save(textTask);

        return R.success(textTask);

    }


    @ApiOperation("重新生产并扣费")
    @PostMapping("/{id}")
    public R<Boolean> reUpdate(@PathVariable Long id) {

        textualInterpretationTextTaskService.reUpdate(id);

        return R.success(true);

    }


    @ApiOperation("更新文献解读结果")
    @PutMapping()
    public R<TextualInterpretationTextTask> addPodcast(@RequestBody @Validated TextualInterpretationTextTaskUpdateDTO textTaskUpdateDTO) {

        TextualInterpretationTextTask textTask = new TextualInterpretationTextTask();

        BeanUtils.copyProperties(textTaskUpdateDTO, textTask);

        textualInterpretationTextTaskService.updateById(textTask);

        return R.success(textTask);

    }

    @ApiOperation("保存到作品集")
    @PostMapping("/saveDraft/{id}")
    public R<String> saveDraft(@PathVariable("id") Long id) {

        TextualInterpretationTextTask task = new TextualInterpretationTextTask();
        task.setId(id);
        task.setTaskStatus(2);
        textualInterpretationTextTaskService.updateById(task);
        return R.success("success");

    }


    @ApiOperation("文件解读分页列表")
    @PostMapping("draftPage")
    public R<IPage<TextualInterpretationTextTask>> draftPage(@RequestBody @Validated PageParams<TextualInterpretationTextTaskPageDTO> params) {

        IPage<TextualInterpretationTextTask> buildPage = params.buildPage();
        TextualInterpretationTextTaskPageDTO model = params.getModel();
        LbqWrapper<TextualInterpretationTextTask> wrapper = Wraps.<TextualInterpretationTextTask>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(SuperEntity::getCreateUser, model.getUserId())
                .eq(TextualInterpretationTextTask::getTaskStatus, model.getTaskStatus());
        if (model.getShowAIKnowledge() != null) {
            wrapper.eq(TextualInterpretationTextTask::getShowAIKnowledge, model.getShowAIKnowledge());
        }
        if (StrUtil.isNotBlank(model.getTitle())) {
            wrapper.and(
                wrapper1 -> wrapper1.like(TextualInterpretationTextTask::getTitle, model.getTitle())
                    .or(
                        wrapper2 -> wrapper2.like(TextualInterpretationTextTask::getFileName, model.getTitle())
                                .isNull(TextualInterpretationTextTask::getTitle)
                    )

            );
        }

        textualInterpretationTextTaskService.page(buildPage, wrapper);
        return R.success(buildPage);
    }


    @ApiOperation("发布在医生数字分身平台")
    @PutMapping("/showInAIKnowledge")
    public R<Boolean> showInAIKnowledge(@RequestBody List<Long> ids) {
        textualInterpretationTextTaskService.update(new TextualInterpretationTextTask(), new UpdateWrapper<TextualInterpretationTextTask>()
                .in("id", ids)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }



    @ApiOperation("隐藏在医生数字分身平台")
    @PutMapping("/hideInAIKnowledge/{id}")
    public R<Boolean> hideInAIKnowledge(@PathVariable("id") Long id) {
        textualInterpretationTextTaskService.update(new TextualInterpretationTextTask(), new UpdateWrapper<TextualInterpretationTextTask>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }


    @ApiOperation("删除文献解读任务")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {

        textualInterpretationTextTaskService.removeById(id);

        return R.success(true);

    }


    @ApiOperation("查询详情")
    @GetMapping("/{id}")
    public R<TextualInterpretationTextTask> get(@PathVariable("id") Long id) {

        TextualInterpretationTextTask textTask = textualInterpretationTextTaskService.getById(id);

        return R.success(textTask);

    }








}
