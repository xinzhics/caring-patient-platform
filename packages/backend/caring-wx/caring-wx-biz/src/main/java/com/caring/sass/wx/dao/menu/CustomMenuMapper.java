package com.caring.sass.wx.dao.menu;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.menu.CustomMenu;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 项目自定义微信菜单
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface CustomMenuMapper extends SuperMapper<CustomMenu> {


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<CustomMenu> selectMenuLikeUrl(@Param(value = "shareUrl") String shareUrl);


}
