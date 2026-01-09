package com.caring.sass.ai.textual.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.ckd.server.CkdUserCozeTokenService;
import com.caring.sass.ai.dto.ckd.CkdUserCozeTokenSaveDTO;
import com.caring.sass.ai.entity.ckd.CkdUserCozeToken;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.service.CozeServer;
import com.caring.sass.ai.textual.PptCreateTask;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.dao.TextualInterpretationPptTaskMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationPptTaskService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 文献解读PPT
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Service

public class TextualInterpretationPptTaskServiceImpl extends SuperServiceImpl<TextualInterpretationPptTaskMapper, TextualInterpretationPptTask> implements TextualInterpretationPptTaskService {

    @Autowired
    TextualInterpretationPptTaskMapper pptTaskMapper;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    CkdUserCozeTokenService ckdUserCozeTokenService;

    @Autowired
    CozeServer cozeServer;

    @Autowired
    PptCreateTask pptCreateTask;

    @Override
    public void reCreatePptOutline(Long id) {

        TextualInterpretationPptTask textTask = baseMapper.selectById(id);
        Long createUser = textTask.getCreateUser();
        textualInterpretationUserService.deductEnergy(createUser, textualUserPayConfig.getReCreatePptOutlineSpend(), TextualConsumptionType.PPT_OUTLINE_CREATION);
    }

    @Override
    public boolean save(TextualInterpretationPptTask model) {

        Long userId = BaseContextHandler.getUserId();
        model.setCreateUser(userId);
        boolean codeCheck = textualInterpretationUserService.deductEnergy(userId, textualUserPayConfig.getCreatePptSpend(), TextualConsumptionType.PPT_CREATION);
        if (!codeCheck) {
            throw  new BizException("用户能量豆不足");
        }
        model.setStep(1);

        // 获取coze的授权
        CkdUserCozeTokenSaveDTO cozeToken = new CkdUserCozeTokenSaveDTO();
        cozeToken.setOpenId(userId.toString());
        CkdUserCozeToken userCozeToken = ckdUserCozeTokenService.queryOrCreateToken(cozeToken);

        // 上传文件
        String fileUrl = model.getFileUrl();
        String cozeFileId = cozeServer.updateFile(fileUrl, userCozeToken.getAccessToken());
        model.setCozeFileId(cozeFileId);

        return super.save(model);
    }

    /**
     * 根据ppt的设置要求。提交给ppt第三方创建
     * @param id
     */
    @Override
    public void startPpt(Long id, String uid) {

        TextualInterpretationPptTask pptTask = baseMapper.selectById(id);
        TextualInterpretationUser user = textualInterpretationUserService.getById(pptTask.getCreateUser());
        String userName = user.getUserName();
        if (StrUtil.isBlank(userName)) {
            userName = "作者名称";
        }
        try {
            String taskPpt = pptCreateTask.createPpt(pptTask, userName);
            pptTask.setLastQueryStatusTime(LocalDateTime.now());
            pptTask.setPptTaskId(taskPpt);
            pptTask.setPptTaskStatus("1");
            pptTask.setUid(uid);
            baseMapper.updateById(pptTask);

            if (StrUtil.isNotBlank(uid)) {
                SseEmitter emitter = SseEmitterSession.get(uid);
                if (emitter != null) {
                    try {
                        emitter.send(SseEmitter.event().id(uid).name("ppt创建任务已提交").data(""));
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            log.error("startPpt", e);
            pptTask.setPptTaskStatus("3");
            pptTask.setPptDataResult("创建ppt任务失败");
            baseMapper.updateById(pptTask);
        }

    }

    public SseEmitter createSse(String uid, Long id) {

        if (Objects.nonNull(id)) {
            TextualInterpretationPptTask pptTask = baseMapper.selectById(id);
            pptTask.setUid(uid);
            baseMapper.updateById(pptTask);
        }

        // 默认2分钟超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data("")
                                .reconnectTime(3000));
                        SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }

}
