package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.user.entity.Group;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 小组
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Repository
public interface GroupMapper extends SuperMapper<Group> {

    /**
     * 带数据权限的分页查询
     *
     * @param page
     * @param wrapper
     * @param dataScope
     * @return
     */
    IPage<Group> findPage(IPage<Group> page, @Param(Constants.WRAPPER) Wrapper<Group> wrapper, DataScope dataScope);

}
