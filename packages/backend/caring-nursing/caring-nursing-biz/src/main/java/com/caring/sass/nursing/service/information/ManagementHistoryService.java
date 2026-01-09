package com.caring.sass.nursing.service.information;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.information.ManagementHistory;

/**
 * <p>
 * 业务接口
 * 管理历史
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
public interface ManagementHistoryService extends SuperService<ManagementHistory> {

    ManagementHistory findAll(String type);

    void add(ManagementHistory history);

    /**
     * 通过类型删除
     *
     * @param id
     */
    void deleteById(Long id);
}
