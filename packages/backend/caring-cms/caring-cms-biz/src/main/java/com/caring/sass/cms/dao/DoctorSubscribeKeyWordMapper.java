package com.caring.sass.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.cms.entity.DoctorSubscribeKeyWord;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 医生订阅关键词表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DoctorSubscribeKeyWordMapper extends SuperMapper<DoctorSubscribeKeyWord> {

}
