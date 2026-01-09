package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.entity.plan.PlanDetail;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 护理计划详情
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
public interface PlanDetailService extends SuperService<PlanDetail> {

    boolean deleteByPlanId(Long plantId);

    /**
     * 查询护理计划（含计划时间详情）
     *
     * @param plantId 护理计划id
     */
    List<PlanDetailDTO> findDetailWithTimeById(Long plantId);


    List<PlanDetailDTO> findDetailWithTimeByIdOrderByTime(Long plantId);

    /**
     * 查询文件夹的分享链接被用在那个项目
     * @param url
     * @return
     */
    List<String> checkFolderShareUrlExist(String url);

}
