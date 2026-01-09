package com.caring.sass.nursing.service.tag;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.tag.TagPatientCountResult;
import com.caring.sass.nursing.entity.tag.Tag;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 标签管理
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
public interface TagService extends SuperService<Tag> {

    /**
     * 复制标签
     */
    Boolean copyTag(String fromTenantCode, String toTenantCode);

    void openTagBindPatientTask(Long tagId);

    void judgeUpdate(Long tagId);


    List<TagPatientCountResult> selectTagCountPatientNumber(Long serviceAdvisorId, Long doctorId, List<Long> doctorIds);
}
