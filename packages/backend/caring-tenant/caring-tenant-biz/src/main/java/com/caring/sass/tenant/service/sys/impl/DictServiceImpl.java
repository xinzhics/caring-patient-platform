package com.caring.sass.tenant.service.sys.impl;


import com.caring.sass.tenant.dao.sys.DictMapper;
import com.caring.sass.tenant.entity.sys.Dict;
import com.caring.sass.tenant.service.sys.DictService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 系统字典类型
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Service

public class DictServiceImpl extends SuperServiceImpl<DictMapper, Dict> implements DictService {
}
