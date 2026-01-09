package com.caring.sass.ai.card.service;

import com.caring.sass.ai.entity.card.BusinessCardDiagramResult;
import com.caring.sass.ai.entity.card.BusinessCardDiagramTask;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.common.constant.GenderType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 医生名片头像合成结果
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-10
 */
public interface BusinessCardDiagramResultService extends SuperService<BusinessCardDiagramResult> {

    List<BusinessCardDiagramResult> queryTaskMergeResult(Long taskId);

    /**
     * 调用 火山引擎，立即合成用户图片并返回。
     * @param file
     * @param userId
     * @return
     */
    List<BusinessCardDiagramResult> saveAndMerge(MultipartFile file, Long userId, GenderType gender);


    void updateHistory(Long userId, String humanPoster);



    BusinessCardDiagramTask createTask(MultipartFile file, GenderType gender, Long userId);
}
