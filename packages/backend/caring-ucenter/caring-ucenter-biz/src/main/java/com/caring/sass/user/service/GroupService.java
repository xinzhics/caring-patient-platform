package com.caring.sass.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.entity.Group;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 小组
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
public interface GroupService extends SuperService<Group> {

    Group createGroup(Group group);

    /**
     * 数据权限 分页
     *
     * @param page
     * @param wrapper
     */
    IPage<Group> findPage(IPage<Group> page, LbqWrapper<Group> wrapper);

    void desensitization(List<Group> pageRecords);
}
