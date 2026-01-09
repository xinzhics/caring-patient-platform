package com.caring.sass.authority.service.common.impl;


import com.caring.sass.authority.dao.common.CityMapper;
import com.caring.sass.authority.entity.common.City;
import com.caring.sass.authority.service.common.CityService;
import com.caring.sass.base.service.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 字典类型
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
@Slf4j
@Service

public class CityServiceImpl extends SuperServiceImpl<CityMapper, City> implements CityService {

}
