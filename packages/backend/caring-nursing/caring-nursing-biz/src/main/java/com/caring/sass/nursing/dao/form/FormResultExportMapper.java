package com.caring.sass.nursing.dao.form;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.nursing.entity.form.FormResultExport;
import com.caring.sass.user.entity.Patient;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-13
 */
@Repository
public interface FormResultExportMapper extends SuperMapper<FormResultExport> {


    IPage<FormResultExport> findPage(IPage<FormResultExport> page, @Param(Constants.WRAPPER) Wrapper<FormResultExport> queryWrapper, DataScope dataScope);

}
