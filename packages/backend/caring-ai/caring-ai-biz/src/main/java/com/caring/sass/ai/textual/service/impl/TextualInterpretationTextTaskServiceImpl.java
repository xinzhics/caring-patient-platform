package com.caring.sass.ai.textual.service.impl;

import com.caring.sass.ai.ckd.server.CkdUserCozeTokenService;
import com.caring.sass.ai.dto.ckd.CkdUserCozeTokenSaveDTO;
import com.caring.sass.ai.entity.ckd.CkdUserCozeToken;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationTextTask;
import com.caring.sass.ai.service.CozeServer;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.dao.TextualInterpretationTextTaskMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationTextTaskService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 业务实现类
 * 文献解读txt
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Service

public class TextualInterpretationTextTaskServiceImpl extends SuperServiceImpl<TextualInterpretationTextTaskMapper, TextualInterpretationTextTask> implements TextualInterpretationTextTaskService {

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    CkdUserCozeTokenService ckdUserCozeTokenService;

    @Autowired
    CozeServer cozeServer;

    @Override
    public void reUpdate(Long id) {

        TextualInterpretationTextTask textTask = baseMapper.selectById(id);
        Long createUser = textTask.getCreateUser();
        textualInterpretationUserService.deductEnergy(createUser, textualUserPayConfig.getReStartTextSpend(), TextualConsumptionType.IMAGE_TEXT_CREATION_RESTART);
    }

    @Override
    public boolean save(TextualInterpretationTextTask model) {

        Long userId = BaseContextHandler.getUserId();
        model.setCreateUser(userId);
        boolean codeCheck = textualInterpretationUserService.deductEnergy(userId, textualUserPayConfig.getImageTextSpend(), TextualConsumptionType.IMAGE_TEXT_CREATION);
        if (!codeCheck) {
            throw  new BizException("用户能量豆不足");
        }

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
}
