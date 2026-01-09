package com.caring.sass.tenant.dao.router;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.router.H5Router;

import com.caring.sass.wx.entity.menu.CustomMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者路由
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Repository
public interface H5RouterMapper extends SuperMapper<H5Router> {



    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<H5Router> selectMenuLikeUrl(@Param(value = "shareUrl") String shareUrl);


}
