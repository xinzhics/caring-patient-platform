package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.user.entity.NursingStaff;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户-医助
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Repository
public interface NursingStaffMapper extends SuperMapper<NursingStaff> {



    @Select(value = "SELECT id, name, avatar, im_account, im_wx_template_status, open_id, wx_status  FROM u_user_nursing_staff WHERE id = #{id}")
    @Results({
            @Result(column="id",javaType=Long.class,property="id"),
            @Result(column="name",javaType=String.class,property="name"),
            @Result(column="avatar",javaType=String.class,property="avatar"),
            @Result(column="im_account",javaType=String.class,property="imAccount"),
            @Result(column="im_wx_template_status",javaType=Integer.class,property="imWxTemplateStatus"),
            @Result(column="open_id",javaType=String.class,property="openId"),
            @Result(column="wx_status",javaType=Integer.class,property="wxStatus")
    })
    NursingStaff getBaseNursingStaffById(@Param("id") Long id);

    /**
     * 带数据权限的分页查询
     *
     * @param page
     * @param wrapper
     * @param dataScope
     * @return
     */
    IPage<NursingStaff> findPage(IPage<NursingStaff> page, @Param(Constants.WRAPPER) Wrapper<NursingStaff> wrapper, DataScope dataScope);

}
