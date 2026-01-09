package com.caring.sass.authority.dao.auth;

import com.caring.sass.authority.entity.auth.Menu;
import com.caring.sass.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 菜单
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
@Repository
public interface MenuMapper extends SuperMapper<Menu> {

    /**
     * 查询用户可用菜单
     *
     * @param userId
     * @return
     */
    List<Menu> findVisibleMenu(@Param("userId") Long userId);
}
