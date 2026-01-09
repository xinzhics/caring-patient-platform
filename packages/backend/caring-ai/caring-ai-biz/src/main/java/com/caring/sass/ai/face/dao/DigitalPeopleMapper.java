package com.caring.sass.ai.face.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.entity.face.DigitalPeople;
import com.caring.sass.base.mapper.SuperMapper;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 数字人
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-05
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DigitalPeopleMapper extends SuperMapper<DigitalPeople> {

}
