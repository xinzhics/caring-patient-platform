package com.caring.sass.nursing.service.information;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.information.ManagementHistoryDetail;

/**
 * <p>
 * 业务接口
 * 管理历史详细记录
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
public interface ManagementHistoryDetailService extends SuperService<ManagementHistoryDetail> {

    /**
     * 构建缺失的参数
     *
     * @param page 分页数据
     * @return
     */
    IPage<ManagementHistoryDetail> build(IPage<ManagementHistoryDetail> page);
}
