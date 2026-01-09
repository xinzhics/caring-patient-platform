package com.caring.sass.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.entity.AudioAnalysisTask;
import com.caring.sass.ai.dto.AudioAnalysisTaskSaveDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskPageDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskTranscriptUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskAnalysisUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskChatUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskPublishDTO;
import com.caring.sass.ai.service.AudioAnalysisTaskService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 音频解析任务表
 * </p>
 *
 * @author leizhi
 * @date 2025-12-12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/audioAnalysisTask")
@Api(value = "AudioAnalysisTask", tags = "音频解析任务表")
public class AudioAnalysisTaskController extends SuperController<AudioAnalysisTaskService, Long, AudioAnalysisTask, AudioAnalysisTaskPageDTO, AudioAnalysisTaskSaveDTO, AudioAnalysisTaskUpdateDTO> {

    /**
     * 根据创建用户ID分页查询
     */
    @ApiOperation(value = "根据创建用户ID分页查询", notes = "支持根据创建用户ID过滤音频解析任务")
    @PostMapping("/pageByCreateUser")
    public R<IPage<AudioAnalysisTask>> pageByCreateUser(@RequestBody PageParams<AudioAnalysisTaskPageDTO> params) {
        IPage<AudioAnalysisTask> builtPage = params.buildPage();
        AudioAnalysisTaskPageDTO paramsModel = params.getModel();
        
        LbqWrapper<AudioAnalysisTask> wrapper = Wraps.<AudioAnalysisTask>lbQ()
                .orderByDesc(AudioAnalysisTask::getCreateTime);
        
        if (paramsModel != null && paramsModel.getCreateUser() != null) {
            wrapper.eq(AudioAnalysisTask::getCreateUser, paramsModel.getCreateUser());
        }
        
        baseService.page(builtPage, wrapper);
        return R.success(builtPage);
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<AudioAnalysisTask> audioAnalysisTaskList = list.stream().map((map) -> {
            AudioAnalysisTask audioAnalysisTask = AudioAnalysisTask.builder().build();
            //TODO 请在这里完成转换
            return audioAnalysisTask;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(audioAnalysisTaskList));
    }

    /**
     * 更新转录数据
     */
    @ApiOperation(value = "更新转录数据", notes = "转录完成后调用此接口更新转录文本")
    @PutMapping("/transcript")
    public R<Boolean> updateTranscript(@RequestBody @Validated AudioAnalysisTaskTranscriptUpdateDTO dto) {
        boolean result = baseService.updateTranscript(dto);
        return R.success(result);
    }

    /**
     * 更新AI解析数据
     */
    @ApiOperation(value = "更新AI解析数据", notes = "AI解析完成后调用此接口更新总结和思维导图")
    @PutMapping("/analysis")
    public R<Boolean> updateAnalysis(@RequestBody @Validated AudioAnalysisTaskAnalysisUpdateDTO dto) {
        boolean result = baseService.updateAnalysis(dto);
        return R.success(result);
    }

    /**
     * 保存聊天记录
     */
    @ApiOperation(value = "保存聊天记录", notes = "增量保存用户与AI的问答历史")
    @PutMapping("/chat")
    public R<Boolean> updateChatHistory(@RequestBody @Validated AudioAnalysisTaskChatUpdateDTO dto) {
        boolean result = baseService.updateChatHistory(dto);
        return R.success(result);
    }

    /**
     * 保存到我的作品
     */
    @ApiOperation(value = "保存到我的作品", notes = "将任务保存为正式作品")
    @PostMapping("/publish")
    public R<Boolean> publishWork(@RequestBody @Validated AudioAnalysisTaskPublishDTO dto) {
        boolean result = baseService.publishWork(dto);
        return R.success(result);
    }

    /**
     * 根据主键ID获取任务详情
     */
    @ApiOperation(value = "根据ID获取详情", notes = "通过数据库主键ID获取任务详情")
    @GetMapping("/detail/{id}")
    public R<AudioAnalysisTask> getById(@PathVariable("id") Long id) {
        AudioAnalysisTask task = baseService.getById(id);
        return R.success(task);
    }
}
