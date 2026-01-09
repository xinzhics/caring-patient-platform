package com.caring.sass.nursing.service.tag;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.entity.tag.Tag;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 业务关联标签记录表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
public interface AssociationService extends SuperService<Association> {

    boolean existsTag(Long tagId, String associationId);

    void clean(Long associationId, List<Tag> tagList);
}
