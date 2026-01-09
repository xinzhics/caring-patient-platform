package com.caring.sass.nursing.service.form;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.form.FormResultExport;

/**
 * <p>
 * 业务接口
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-13
 */
public interface FormResultExportService extends SuperService<FormResultExport> {


    IPage<FormResultExport> findPage(IPage<FormResultExport> page, Wrapper<FormResultExport> queryWrapper);

    void updateExportProgress(Long id, long total, long queryTotal);

    void updateExportMessage(Long id, String message);

    void updateExportResult(Long id, String uploadDataUrl);
}
