package com.caring.sass.wx.dao.guide;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.guide.RegGuide;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 微信注册引导
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface RegGuideMapper extends SuperMapper<RegGuide> {

    @Select("select t.tenant_code,t.unregistered_reminder from t_wx_reg_guide t where t.has_unregistered_reminder = 1")
    @Results({
            @Result(column="tenant_code", javaType=String.class, property="tenantCode"),
            @Result(column="unregistered_reminder", javaType=String.class, property="unregisteredReminder")
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<RegGuide> getOpenUnregisteredReminder();


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<RegGuide> selectWxUserDefaultRole();


}
