package com.caring.sass.ai.humanVideo.service;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskSaveDTO;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskUpdateDTO;
import com.caring.sass.ai.entity.humanVideo.BusinessDigitalHumanVideoTask;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 数字人视频制作任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
public interface BusinessDigitalHumanVideoTaskService extends SuperService<BusinessDigitalHumanVideoTask> {


    BusinessDigitalHumanVideoTask submitDigitalHumanVideoTask(BusinessDigitalHumanVideoTaskUpdateDTO taskUpdateDTO);


    /**
     * 查询用户是否有还在提交资料过程中的任务
     * @param userId
     * @return
     */
    BusinessDigitalHumanVideoTask getNotStartTask(Long userId);

    /**
     * 提交任意其他资料
     * @param updateDTO
     * @return
     */
    BusinessDigitalHumanVideoTask submitTaskOtherData(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO);


    /**
     * 管理员提交资料生产数字人
     * @param updateDTO
     * @return
     */
    BusinessDigitalHumanVideoTask adminSubmitStartTask(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO);

    /**
     * 最终提交资料。 开始制作任务
     * @param updateDTO
     * @return
     */
    BusinessDigitalHumanVideoTask submitEndStartTask(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO);


    JSONObject verifyImage(BusinessDigitalHumanVideoTaskSaveDTO updateDTO);

    /**
     * 百度回调 视频制作失败
     * @param taskId
     * @param errorMessage
     */
    void updateFailed(String taskId, String errorMessage);

    /**
     * 百度回调，视频制作成功
     * @param taskId
     * @param videoUrl
     */
    void updateSuccess(String taskId, String videoUrl);



//    BusinessDigitalHumanVideoTask useVideoAndAudioCreateHuman(BusinessDigitalHumanVideoTaskUpdateDTO updateDTO);

}
