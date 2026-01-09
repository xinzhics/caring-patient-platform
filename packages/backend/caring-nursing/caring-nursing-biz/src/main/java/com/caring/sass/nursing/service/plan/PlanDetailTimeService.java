package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;

import java.io.Serializable;
import java.util.Collection;
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
public interface PlanDetailTimeService extends SuperService<PlanDetailTime> {


    /**
     * @Author yangShuai
     * @Description 删除护理计划详情时间记录
     * @Date 2020/9/21 13:41
     *
     * @return void
     */
    void deleteByDetailIds(Collection<? extends Serializable> collect);


    /**
     * @Author yangShuai
     * @Description 查询护理计划详情的时间设置
     * @Date 2020/9/21 15:28
     *
     * @return java.util.List<com.caring.sass.nursing.entity.plan.PlanDetailTime>
     */
    List<PlanDetailTime> findByDetailId(Long detailId);


    List<PlanDetailTime> findByDetailIdOrderByTime(Long detailId);

    /**
     * 检查文件夹的分享url是否存在
     * @param url
     * @return
     */
    List<PlanDetailTime> checkFolderShareUrlExist(String url);
}
