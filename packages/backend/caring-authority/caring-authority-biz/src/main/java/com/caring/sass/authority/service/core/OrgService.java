package com.caring.sass.authority.service.core;

import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.base.service.SuperCacheService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 组织
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
public interface OrgService extends SuperCacheService<Org> {
    /**
     * 查询指定id集合下的所有子集
     *
     * @param ids
     * @return
     */
    List<Org> findChildren(List<Long> ids);

    /**
     * 批量删除以及删除其子节点
     *
     * @param ids
     * @return
     */
    boolean remove(List<Long> ids);

    /**
     * 根据 id 查询组织，并转换成Map结构
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findOrgByIds(Set<Serializable> ids);

    /**
     * 根据 id 查询名称，并转换成Map结构
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findOrgNameByIds(Set<Serializable> ids);

    List<Org> listWithScope(LbqWrapper<Org> wrapper);
}
